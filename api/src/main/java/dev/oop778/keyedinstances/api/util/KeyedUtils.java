package dev.oop778.keyedinstances.api.util;

import dev.oop778.keyedinstances.api.instance.KeyedInstance;
import lombok.experimental.UtilityClass;

import java.util.*;

@UtilityClass
public class KeyedUtils {

    /**
     * Collects all classes in the hierarchy of the given leaf class.
     *
     * @param leaf the leaf class
     * @return a collection of all classes in the hierarchy of the given leaf class
     */
    public Collection<Class<?>> collectHierarchy(Class<?> leaf) {
        final Queue<Class<?>> toVisit = new LinkedList<>();
        final Set<Class<?>> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        toVisit.add(leaf);

        while (!toVisit.isEmpty()) {
            final Class<?> current = toVisit.poll();
            visited.add(current);

            final Class<?> superclass = current.getSuperclass();
            if (superclass != null && visited.add(superclass)) {
                toVisit.add(superclass);
            }

            for (final Class<?> iface : current.getInterfaces()) {
                if (visited.add(iface)) {
                    toVisit.add(iface);
                }
            }
        }

        return visited;
    }

    /**
     * Groups the given classes by their tree roots.
     *
     * @param classes the classes to group
     * @return a map of tree roots to their children
     */
    public Map<Class<?>, List<Class<?>>> groupByTreeRoots(Collection<Class<?>> classes) {
        final Map<Class<?>, List<Class<?>>> treeRoots = new HashMap<>();
        for (final Class<?> clazz : classes) {
            final Class<?> superclass = clazz.getSuperclass();
            if (superclass == null || superclass == Object.class || classes.contains(superclass)) {
                continue;
            }

            treeRoots.put(clazz, new ArrayList<>());
        }

        for (final Class<?> clazz : classes) {
            final Class<?> superclass = clazz.getSuperclass();
            if (superclass != null && treeRoots.containsKey(superclass)) {
                treeRoots.get(superclass).add(clazz);
            }
        }

        return treeRoots;
    }

    public static boolean isRootLeaf(Class<? extends KeyedInstance> testingClass) {
        return Arrays.stream(testingClass.getInterfaces())
                .anyMatch(iface -> iface == KeyedInstance.class);
    }
}
