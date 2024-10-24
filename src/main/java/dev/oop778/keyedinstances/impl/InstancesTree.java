package dev.oop778.keyedinstances.impl;

import dev.oop778.keyedinstances.api.annotation.Keyed;
import dev.oop778.keyedinstances.api.instance.KeyedInstance;
import dev.oop778.keyedinstances.impl.path.IKeyedPath;
import dev.oop778.keyedinstances.impl.path.KeyedPathFactory;
import dev.oop778.keyedinstances.impl.path.MultiRootKeyedPath;
import dev.oop778.keyedinstances.impl.path.SingleRootKeyedPath;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InstancesTree {
    protected final TreeNode root = new TreeNode(null, null);
    protected final Map<Class<? extends KeyedInstance>, TreeReference<?>> instances = new ConcurrentHashMap<>();

    public <T extends KeyedInstance> void register(T object) {
        final IKeyedPath<? extends KeyedInstance> path = KeyedPathFactory.create(object.getClass());
        if (path instanceof MultiRootKeyedPath) {
            this.registerMultiRoot(object, ((MultiRootKeyedPath<T>) path));
        } else {
            this.registerSingleRoot(object, ((SingleRootKeyedPath<T>) path));
        }
    }

    public @Nullable <T extends KeyedInstance> T getInstanceOfClass(@NonNull Class<T> clazz) {
        final TreeReference<?> treeReference = this.instances.get(clazz);
        if (treeReference == null) {
            return null;
        }

        return (T) treeReference.value;
    }

    public @Nullable KeyedInstance getInstance(Class<? extends KeyedInstance> rootOrParent, @NonNull String path) {
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

        return node.reference == null ? null : (KeyedInstance) node.reference.value;
    }

    public <T extends KeyedInstance> List<? extends T> getInstancesOfRoot(@NonNull Class<T> root) {
        final TreeNode node = this.findNode(this.getPathIncludingSelf(root));
        if (node == null) {
            return Collections.emptyList();
        }

        return this.getInstancesFromNode(node);
    }

    public <T extends KeyedInstance> List<? extends T> getInstancesOfRoot(@NonNull String path) {
        final String[] split = path.split("\\.");
        TreeNode node = this.root;
        for (final String key : split) {
            node = node.leafMap.get(key);
            if (node == null) {
                return Collections.emptyList();
            }
        }

        return this.getInstancesFromNode(node);
    }

    private <T extends KeyedInstance> List<? extends T> getInstancesFromNode(TreeNode node) {
        final Queue<TreeNode> toVisit = new LinkedList<>();
        toVisit.add(node);

        final List<T> result = new ArrayList<>();
        while (!toVisit.isEmpty()) {
            final TreeNode poll = toVisit.poll();
            if (poll.reference != null && poll.reference.value != null) {
                result.add((T) poll.reference.value);
            }

            for (final TreeNode child : poll.leafMap.values()) {
                toVisit.add(child);
            }
        }

        return result;
    }

    private <T extends KeyedInstance> void registerSingleRoot(T object, SingleRootKeyedPath<T> path) {
        final TreeNode parentNode = this.getOrCreateNode(path.getPath());
        final TreeNode objectNode = parentNode.leafMap.computeIfAbsent(object.key(), ($) -> new TreeNode(parentNode, null));
        objectNode.reference = new TreeReference<>(object, object.getClass());
        this.instances.put(object.getClass(), objectNode.reference);
    }

    private <T extends KeyedInstance> void registerMultiRoot(T object, MultiRootKeyedPath<T> path) {
        final TreeReference<T> treeReference = new TreeReference<>(object, object.getClass());
        this.instances.put(object.getClass(), treeReference);

        for (final List<Class<?>> pathFromRoot : path.getRootToPath().values()) {
            final TreeNode parentNode = this.getOrCreateNode(pathFromRoot);
            final TreeNode objectNode = parentNode.leafMap.computeIfAbsent(object.key(), ($) -> new TreeNode(parentNode, null));
            objectNode.reference = treeReference;
        }
    }

    private TreeNode getOrCreateNode(List<Class<?>> path) {
        TreeNode node = this.root;

        for (final Class<?> parentClass : path) {
            final String parentKey = parentClass.getDeclaredAnnotation(Keyed.class).value();

            final TreeNode nodeSave = node;
            node = node.leafMap.computeIfAbsent(parentKey, ($) -> new TreeNode(nodeSave, null));
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
    protected static class TreeReference<T> {
        @Nullable
        private volatile T value;
        private volatile Class<? extends KeyedInstance> clazz;

        public void update(T value, Class<? extends KeyedInstance> clazz) {
            this.value = value;
            this.clazz = clazz;
        }
    }

    @AllArgsConstructor
    protected static class TreeNode {
        protected final Map<String, TreeNode> leafMap = new ConcurrentHashMap<>();
        @Nullable
        protected TreeNode parent;
        @Nullable
        protected TreeReference<?> reference;
    }
}
