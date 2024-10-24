package dev.oop778.keyedinstances.impl;

import dev.oop778.keyedinstances.api.KeyedInstances;
import dev.oop778.keyedinstances.api.instance.KeyedInstance;
import dev.oop778.keyedinstances.impl.util.SafeIterator;
import dev.oop778.keyedinstances.impl.util.TransformingIterator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class KeyedInstancesImpl implements KeyedInstances {
    private final InstancesTree tree = new InstancesTree();
    private final TreeReferenceCollection treeReferenceCollection = new TreeReferenceCollection(this.tree.instances.values());

    @Override
    public void registerInstance(@NonNull KeyedInstance instance) {
        this.tree.register(instance);
    }

    @Override
    public void unregisterInstance(@NonNull KeyedInstance instance) {}

    @Override
    public Collection<KeyedInstance> unregisterInstances(@NonNull Class<? extends KeyedInstance> ofClass) {
        return Collections.emptyList();
    }

    @Override
    public @Nullable KeyedInstance getInstance(@Nullable Class<? extends KeyedInstance> rootOrParent, @NonNull String path) {
        return this.tree.getInstance(rootOrParent, path);
    }

    @Override
    public <T extends KeyedInstance> @Nullable T getInstanceOfClass(@NonNull Class<T> clazz) {
        return this.tree.getInstanceOfClass(clazz);
    }

    @Override
    public Collection<? extends KeyedInstance> getInstances() {
        return this.treeReferenceCollection;
    }

    @Override
    public <T extends KeyedInstance> List<? extends T> getInstancesOfRoot(@NonNull Class<T> root) {
        return this.tree.getInstancesOfRoot(root);
    }

    @Override
    public <T extends KeyedInstance> List<? extends T> getInstancesOfRoot(@NonNull String rootPath) {
        return this.tree.getInstancesOfRoot(rootPath);
    }

    @RequiredArgsConstructor
    public static class TreeReferenceCollection implements Collection<KeyedInstance> {
        private final Collection<InstancesTree.TreeReference<?>> collection;

        @Override
        public int size() {
            return this.collection.size();
        }

        @Override
        public boolean isEmpty() {
            return this.collection.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return this.collection.contains(o);
        }

        @Override
        public @NotNull Iterator<KeyedInstance> iterator() {
            return new TransformingIterator<>(
                    (ref) -> (KeyedInstance) ref.getValue(),
                    new SafeIterator<InstancesTree.TreeReference<?>>(this.collection.iterator()) {
                        @Override
                        protected boolean validate(InstancesTree.TreeReference<?> value) {
                            return value.getValue() != null;
                        }
                    }
            );
        }

        @Override
        public @NotNull Object[] toArray() {
            return this.collection.stream()
                    .filter(ref -> ref.getValue() != null)
                    .toArray();
        }

        @Override
        public @NotNull <T> T[] toArray(@NotNull T[] a) {
            return this.collection.stream()
                    .filter(ref -> ref.getValue() != null)
                    .toArray(($) -> a);
        }

        @Override
        public boolean add(KeyedInstance keyedInstance) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(@NotNull Collection<?> c) {
            for (final InstancesTree.TreeReference<?> treeReference : this.collection) {
                if (treeReference.getValue() == null) {
                    continue;
                }

                if (!c.contains(treeReference.getValue())) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean addAll(@NotNull Collection<? extends KeyedInstance> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(@NotNull Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(@NotNull Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }
    }
}
