package dev.oop778.keyedinstances.impl;

import dev.oop778.keyedinstances.api.KeyedInstanceUpdater;
import dev.oop778.keyedinstances.api.KeyedReference;
import dev.oop778.keyedinstances.api.annotation.Keyed;
import dev.oop778.keyedinstances.api.instance.KeyedInstance;
import dev.oop778.keyedinstances.impl.path.IKeyedPath;
import dev.oop778.keyedinstances.impl.path.KeyedPathFactory;
import dev.oop778.keyedinstances.impl.path.MultiRootKeyedPath;
import dev.oop778.keyedinstances.impl.path.SingleRootKeyedPath;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ApiStatus.Internal
public class InstancesTree {
    protected final KeyedInstanceUpdater updater;
    protected final TreeNode root;
    protected final Map<Class<? extends KeyedInstance>, TreeReference<?>> instances;
    protected final UnresolvedInstances unresolvedInstances;

    public InstancesTree(KeyedInstanceUpdater updater) {
        this.updater = updater;
        this.root = new TreeNode(null);
        this.instances = new ConcurrentHashMap<>();
        this.unresolvedInstances = new UnresolvedInstances(this);
    }

    public <T extends KeyedInstance> boolean register(T object) {
        TreeReference<T> treeReference = (TreeReference<T>) this.instances.get(object.getClass());
        if (treeReference != null) {
            final T value = treeReference.value;
            final T updated = (T) this.updater.update(value, object);
            if (Objects.equals(updated, value)) {
                return false;
            }

            treeReference.update(updated);
        }

        treeReference = (TreeReference<T>) this.instances.computeIfAbsent(object.getClass(), ($) -> new TreeReference<>(object, object.getClass()));

        final IKeyedPath<? extends KeyedInstance> path = KeyedPathFactory.create(object.getClass());
        if (path instanceof MultiRootKeyedPath) {
            this.registerMultiRoot(object, ((MultiRootKeyedPath<T>) path), treeReference);
        } else {
            this.registerSingleRoot(object, ((SingleRootKeyedPath<T>) path), treeReference);
        }

        this.unresolvedInstances.resolve(treeReference);

        return true;
    }

    public @Nullable <T extends KeyedInstance> KeyedReference<T> getInstanceOfClass(@NonNull Class<T> clazz) {
        final TreeReference<?> treeReference = this.instances.get(clazz);
        return (KeyedReference<T>) treeReference;
    }

    public <T extends KeyedInstance> @Nullable KeyedReference<T> getInstance(@Nullable Class<? extends KeyedInstance> rootOrParent, @NonNull String path) {
        TreeNode node = this.root;
        if (rootOrParent != null) {
            final List<Class<?>> pathIncludingSelf = this.getPathIncludingSelf(rootOrParent);
            node = this.findNode(pathIncludingSelf);

            if (node == null) {
                return null;
            }

            final String toCut = this.joinPathToString(pathIncludingSelf);
            path = path.replace(toCut + ".", "");
        }

        final String[] split = path.split("\\.");
        for (final String key : split) {
            node = node.leafMap.get(key);
            if (node == null) {
                return null;
            }
        }

        return (KeyedReference<T>) node.reference;
    }

    public <T extends KeyedInstance> void collectInstancesFromParent(@NonNull Class<? extends T> parent, Collection<KeyedReference<? extends T>> collection) {
        final TreeNode node = this.findNode(this.getPathIncludingSelf(parent));
        if (node == null) {
            return;
        }

        this.collectInstanceOfNode(node, collection);
    }

    public <T extends KeyedInstance> void collectInstancesFromPath(@NonNull String path, Collection<KeyedReference<? extends T>> collection) {
        final String[] split = path.split("\\.");
        TreeNode node = this.root;
        for (final String key : split) {
            node = node.leafMap.get(key);
            if (node == null) {
                return;
            }
        }

        this.collectInstanceOfNode(node, collection);
    }

    public String getPath(@NonNull KeyedInstance instance) {
        final List<Class<?>> path = this.getPathIncludingSelf(instance.getClass());
        return this.getPath(instance, path);
    }

    public Set<String> getPaths(@NonNull KeyedInstance instance) {
        final IKeyedPath<? extends @NonNull KeyedInstance> path = KeyedPathFactory.create(instance.getClass());
        if (path instanceof SingleRootKeyedPath) {
            return new HashSet<>(Collections.singletonList(this.getPath(instance, ((SingleRootKeyedPath<?>) path).getPath())));
        }

        return ((MultiRootKeyedPath<?>) path).getRootToPath().values().stream()
                .map(classes -> this.getPath(instance, classes))
                .collect(Collectors.toSet());
    }

    public String getPathFrom(@NonNull Class<? extends KeyedInstance> parent, @NonNull KeyedInstance instance) {
        final List<Class<?>> instancePath = this.getPathIncludingSelf(instance.getClass(), parent);
        final int i = instancePath.indexOf(parent);

        final String rootPath = this.joinPathToString(instancePath.subList(i + 1, instancePath.size()));
        return rootPath.isEmpty() ? instance.getKey() : rootPath + "." + instance.getKey();
    }

    public <T extends KeyedInstance> KeyedReference<T> createUnresolvedReference(@NonNull Class<T> instanceClass) {
        return this.unresolvedInstances.createOfClass(instanceClass);
    }

    public <T extends KeyedInstance> KeyedReference<T> createUnresolvedReference(Class<? extends T> parent, @NonNull String fullPath) {
        return this.unresolvedInstances.createOfPath(parent, fullPath);
    }

    private String getPath(@NonNull KeyedInstance instance, List<Class<?>> path) {
        final String parent = this.joinPathToString(path);
        return parent.isEmpty() ? instance.getKey() : parent + "." + instance.getKey();
    }

    private <T extends KeyedInstance> void collectInstanceOfNode(TreeNode node, Collection<KeyedReference<? extends T>> collection) {
        final Queue<TreeNode> toVisit = new LinkedList<>();
        toVisit.add(node);

        while (!toVisit.isEmpty()) {
            final TreeNode poll = toVisit.poll();
            if (poll.reference != null && poll.reference.value != null) {
                collection.add((KeyedReference<T>) poll.reference);
            }

            toVisit.addAll(poll.leafMap.values());
        }
    }

    private <T extends KeyedInstance> void registerSingleRoot(T object, SingleRootKeyedPath<T> path, TreeReference<T> treeReference) {
        final TreeNode parentNode = this.getOrCreateNode(path.getPath());
        final TreeNode objectNode = parentNode.leafMap.computeIfAbsent(object.getKey(), ($) -> new TreeNode(null));
        objectNode.reference = treeReference;
    }

    private <T extends KeyedInstance> void registerMultiRoot(T object, MultiRootKeyedPath<T> path, TreeReference<T> treeReference) {
        for (final List<Class<?>> pathFromRoot : path.getRootToPath().values()) {
            final TreeNode parentNode = this.getOrCreateNode(pathFromRoot);
            final TreeNode objectNode = parentNode.leafMap.computeIfAbsent(object.getKey(), ($) -> new TreeNode(null));
            objectNode.reference = treeReference;
        }
    }

    private TreeNode getOrCreateNode(List<Class<?>> path) {
        TreeNode node = this.root;

        for (final Class<?> parentClass : path) {
            final String parentKey = parentClass.getDeclaredAnnotation(Keyed.class).value();

            node = node.leafMap.computeIfAbsent(parentKey, ($) -> new TreeNode(null));
        }

        return node;
    }

    private String joinPathToString(List<Class<?>> path) {
        return path.stream().map(clazz -> clazz.getDeclaredAnnotation(Keyed.class).value()).collect(Collectors.joining("."));
    }

    private List<Class<?>> getPathIncludingSelf(Class<? extends KeyedInstance> of) {
        final IKeyedPath<? extends KeyedInstance> keyedPath = KeyedPathFactory.create(of);

        final List<Class<?>> path = new ArrayList<>();
        if (keyedPath instanceof SingleRootKeyedPath) {
            path.addAll(((SingleRootKeyedPath<?>) keyedPath).getPath());
        } else {
            path.addAll(((MultiRootKeyedPath<?>) keyedPath).getFirstPath().getValue());
        }

        return path;
    }

    private List<Class<?>> getPathIncludingSelf(Class<? extends KeyedInstance> from, Class<? extends KeyedInstance> contains) {
        final IKeyedPath<? extends KeyedInstance> keyedPath = KeyedPathFactory.create(from);

        final List<Class<?>> path;
        if (keyedPath instanceof SingleRootKeyedPath) {
            path = new ArrayList<>(((SingleRootKeyedPath<?>) keyedPath).getPath());
        } else {
            return ((MultiRootKeyedPath<?>) keyedPath).getRootToPath().entrySet().stream()
                    .filter(entry -> entry.getValue().contains(contains))
                    .flatMap(entry -> entry.getValue().stream())
                    .collect(Collectors.toList());

        }

        return path;
    }

    private TreeNode findNode(List<Class<?>> path) {
        TreeNode node = this.root;

        for (final Class<?> parentClass : path) {
            final String parentKey = parentClass.getDeclaredAnnotation(Keyed.class).value();
            node = node.leafMap.get(parentKey);

            if (node == null) {
                break;
            }
        }

        return node;
    }

    @AllArgsConstructor
    @Getter
    public static class TreeReference<T extends KeyedInstance> implements KeyedReference<T> {
        @Nullable
        private volatile T value;
        private volatile Class<? extends KeyedInstance> clazz;

        @Override
        public T get() {
            return this.value;
        }

        public void update(T value) {
            this.value = value;
            this.clazz = value.getClass();
        }
    }

    @AllArgsConstructor
    protected static class TreeNode {
        protected final Map<String, TreeNode> leafMap = new ConcurrentHashMap<>();
        @Nullable
        protected TreeReference<?> reference;
    }
}
