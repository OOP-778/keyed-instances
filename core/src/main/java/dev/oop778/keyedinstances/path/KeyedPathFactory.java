package dev.oop778.keyedinstances.path;

import dev.oop778.keyedinstances.api.annotation.Keyed;
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

    public <T extends KeyedInstance> KeyedPath<T> create(Class<T> clazz) {
        // Step 1 collect hierarchy
        final Queue<Class<?>> toVisit = new LinkedList<>();
        toVisit.add(clazz);

        final List<Class<?>> path = new ArrayList<>();

        final Collection<Class<?>> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        final List<Class<?>> roots = new ArrayList<>();
        final Map<Class<?>, List<Class<?>>> nonIndexedMap = new IdentityHashMap<>();

        while (!toVisit.isEmpty()) {
            final Class<?> current = toVisit.poll();

            // If already visited
            if (!visited.add(current)) {
                continue;
            }

            // If the clazz is root leaf
            if (isRootLeaf(current)) {
                roots.add(current);
            }

            if (current.getDeclaredAnnotation(Keyed.class) != null) {
                path.add(current);
            }

            final List<Class<?>> addToQueue = new ArrayList<>();
            Collections.addAll(addToQueue, current.getInterfaces());

            if (current.getSuperclass() != null && current.getSuperclass() != Object.class) {
                addToQueue.add(current.getSuperclass());
            }

            toVisit.addAll(addToQueue);
            nonIndexedMap.put(current, addToQueue);
        }

        // #2 Find out depth so we can sort
        final HashMap<Class<?>, Integer> indexedDepthMap = new HashMap<>();
        calculateDepth(nonIndexedMap, clazz, indexedDepthMap, 0);

        // #3 Create separate paths for each root
        final Comparator<Class<?>> pathSeparationComparator = (c1, c2) -> {
            final Class<?> c1Head = roots.stream().filter(head -> head.isAssignableFrom(c1)).findFirst().orElse(null);
            final Class<?> c2Head = roots.stream().filter(head -> head.isAssignableFrom(c2)).findFirst().orElse(null);

            return c1Head == c2Head ? 0 : roots.indexOf(c1Head) > roots.indexOf(c2Head) ? 1 : -1;
        }.thenComparingInt(indexedDepthMap::get).reversed();

        List<Class<?>> pathBuilder = new ArrayList<>();
        final List<KeyedPath<?>> paths = new ArrayList<>();

        for (final Class<?> rootClass : roots) {
            if (rootClass.getDeclaredAnnotation(Keyed.class) == null) {
                continue;
            }

            pathBuilder.add(rootClass);

            for (final Class<?> possibleCandidate : path) {
                if (!rootClass.isAssignableFrom(possibleCandidate)) {
                    continue;
                }

                pathBuilder.add(possibleCandidate);
            }

            pathBuilder.sort(pathSeparationComparator.thenComparingInt(indexedDepthMap::get).reversed());

            globalTypeHierarchies.add(new GlobalTypeHierarchy(rootClass, pathBuilder));
            pathBuilder = new ArrayList<>();
        }

    }
}
