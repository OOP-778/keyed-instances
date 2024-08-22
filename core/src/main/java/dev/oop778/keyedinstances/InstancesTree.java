package dev.oop778.keyedinstances;

import dev.oop778.keyedinstances.api.instance.KeyedInstance;
import dev.oop778.keyedinstances.path.KeyedPath;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InstancesTree {
    private final TreeNode root = new TreeNode();
    private final Map<Class<?>, KeyedPath> cache = new ConcurrentHashMap<>();

    protected static class TreeReference<T> {
        private volatile T value;
        private volatile Class<? extends KeyedInstance> clazz;

        public void update(T value, Class<? extends KeyedInstance> clazz) {
            this.value = value;
            this.clazz = clazz;
        }
    }

    protected static class TreeNode {
        @Nullable
        protected TreeNode parent;

        @Nullable
        protected TreeReference<?> reference;

        protected Map<String, TreeNode> leafMap = new ConcurrentHashMap<>();
    }
}
