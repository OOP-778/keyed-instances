package dev.oop778.keyedinstances.impl.path;

import dev.oop778.keyedinstances.api.annotation.KeyedGroupId;
import dev.oop778.keyedinstances.api.instance.KeyedInstance;

import java.util.*;

public class KeyedPathFactory {

    private static void calculateDepth(Map<Class<?>, List<Class<?>>> hierarchy, Class<?> clazz, Map<Class<?>, Integer> depthMap, int depth) {
        depthMap.put(clazz, depth);
        if (!hierarchy.containsKey(clazz)) {
            return;
        }

        for (final Class<?> child : hierarchy.get(clazz)) {
            calculateDepth(hierarchy, child, depthMap, depth + 1);
        }
    }

    private static boolean isRootLeaf(Class<?> testingClass) {
        return Arrays.stream(testingClass.getInterfaces())
                .anyMatch(iface -> iface == KeyedInstance.class);
    }

    private static List<Class<?>> collectPath(Class<?> rootClass, Collection<Class<?>> candidates, Comparator<Class<?>> comparator) {
        final List<Class<?>> result = new ArrayList<>();
        for (final Class<?> candidate : candidates) {
            if (!rootClass.isAssignableFrom(candidate)) {
                continue;
            }

            result.add(candidate);
        }

        result.sort(comparator);
        return result;
    }

    public static <T extends KeyedInstance> IKeyedPath<T> create(Class<T> clazz) {
        // Step 1: Collect hierarchy
        final Set<Class<?>> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        final List<Class<?>> path = new ArrayList<>();
        final List<Class<?>> roots = new ArrayList<>();
        final Map<Class<?>, List<Class<?>>> hierarchyMap = new IdentityHashMap<>();

        collectHierarchy(clazz, visited, path, roots, hierarchyMap);

        // Step 2: Calculate depth for sorting
        final Map<Class<?>, Integer> depthMap = new HashMap<>();
        calculateDepth(hierarchyMap, clazz, depthMap, 0);

        // Step 3: Create separate paths for each root
        final Comparator<Class<?>> classComparator = createClassComparator(roots, depthMap);

        return roots.size() == 1 ?
                createSingleRootPath(clazz, roots.get(0), path, classComparator) :
                createMultiRootPath(clazz, roots, path, classComparator);
    }

    private static void collectHierarchy(
            Class<?> clazz, Set<Class<?>> visited, List<Class<?>> path,
            List<Class<?>> roots, Map<Class<?>, List<Class<?>>> hierarchyMap) {

        final Queue<Class<?>> toVisit = new LinkedList<>();
        toVisit.add(clazz);

        while (!toVisit.isEmpty()) {
            final Class<?> current = toVisit.poll();

            if (!visited.add(current)) {
                continue;
            }

            if (isRootLeaf(current)) {
                roots.add(current);
            }

            if (current.getDeclaredAnnotation(KeyedGroupId.class) != null) {
                path.add(current);
            }

            final List<Class<?>> relatedClasses = new ArrayList<>();
            Collections.addAll(relatedClasses, current.getInterfaces());

            if (current.getSuperclass() != null && current.getSuperclass() != Object.class) {
                relatedClasses.add(current.getSuperclass());
            }

            toVisit.addAll(relatedClasses);
            hierarchyMap.put(current, relatedClasses);
        }
    }

    private static Comparator<Class<?>> createClassComparator(
            List<Class<?>> roots, Map<Class<?>, Integer> depthMap) {

        return Comparator
                .comparing((Class<?> c) -> roots.stream()
                        .filter(root -> root.isAssignableFrom(c))
                        .findFirst()
                        .orElse(null), Comparator.comparingInt(roots::indexOf))
                .thenComparingInt(depthMap::get)
                .reversed();
    }

    private static <T> IKeyedPath<T> createSingleRootPath(
            Class<T> clazz, Class<?> root, List<Class<?>> path,
            Comparator<Class<?>> classComparator) {

        return new SingleRootKeyedPath<>(
                clazz,
                root,
                collectPath(root, path, classComparator)
        );
    }

    private static <T> IKeyedPath<T> createMultiRootPath(
            Class<T> clazz, List<Class<?>> roots, List<Class<?>> path,
            Comparator<Class<?>> classComparator) {

        final Map<Class<?>, List<Class<?>>> byRootPath = new IdentityHashMap<>();

        for (final Class<?> rootClass : roots) {
            if (rootClass.getDeclaredAnnotation(KeyedGroupId.class) == null) {
                continue;
            }

            byRootPath.put(rootClass, collectPath(rootClass, path, classComparator));
        }

        return new MultiRootKeyedPath<>(clazz, byRootPath);
    }
}
