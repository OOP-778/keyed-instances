package dev.oop778.keyedinstances.impl;

import dev.oop778.keyedinstances.api.KeyedRegistry;
import dev.oop778.keyedinstances.api.find.KeyedFinderSelector;
import dev.oop778.keyedinstances.api.instance.KeyedInstance;
import dev.oop778.keyedinstances.impl.find.FinderSelectorImpl;
import dev.oop778.keyedinstances.impl.util.SafeIterator;
import dev.oop778.keyedinstances.impl.util.TransformingIterator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

@ApiStatus.Internal
public class KeyedRegistryImpl implements KeyedRegistry {
    private final InstancesTree tree;
    private final TreeReferenceCollection treeReferenceCollection;

    public KeyedRegistryImpl(KeyedRegistryBuilderImpl builder) {
        this.tree = new InstancesTree(builder.updater);
        this.treeReferenceCollection = new TreeReferenceCollection(this.tree.instances.values());
    }

    @Override
    public boolean registerInstance(@NonNull KeyedInstance instance) {
        return this.tree.register(instance);
    }

    @Override
    public void unregisterInstance(@NonNull KeyedInstance instance) {}

    @Override
    public <T> Collection<? extends T> unregisterInstances(@NonNull Class<T> ofClass) {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends KeyedInstance> getInstances() {
        return this.treeReferenceCollection;
    }

    @Override
    public <T extends KeyedInstance> KeyedFinderSelector<T> find() {
        return new FinderSelectorImpl<>(this.tree);
    }

    @Override
    public String getInstancePath(@NonNull KeyedInstance instance) {
        return this.tree.getPath(instance);
    }

    @Override
    public String getInstancePathFrom(@NonNull Class<? extends KeyedInstance> parent, @NonNull KeyedInstance instance) {
        return this.tree.getPathFrom(parent, instance);
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
                    InstancesTree.TreeReference::getValue,
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
