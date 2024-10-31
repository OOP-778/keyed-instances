package dev.oop778.keyedinstances.impl.find;

import dev.oop778.keyedinstances.api.KeyedReference;
import dev.oop778.keyedinstances.api.find.KeyedCollector;
import dev.oop778.keyedinstances.api.find.KeyedFinder;
import dev.oop778.keyedinstances.api.find.SingleKeyedCollector;
import dev.oop778.keyedinstances.api.instance.KeyedInstance;
import dev.oop778.keyedinstances.impl.InstancesTree;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Internal
@RequiredArgsConstructor
public class FinderImpl<T extends KeyedInstance> implements KeyedFinder<T> {
    protected final InstancesTree tree;
    protected final List<KeyedReference<? extends T>> references = new ArrayList<>();

    public static class Single<T extends KeyedInstance> extends FinderImpl<T> implements KeyedFinder.Single<T, SingleKeyedCollector<T, T>> {
        public Single(InstancesTree tree) {
            super(tree);
        }

        @Override
        public SingleKeyedCollector<T, T> withInstance(@Nullable Class<? extends T> parent, @NonNull String fullPath) {
            final KeyedReference<? extends T> instance = this.tree.getInstance(parent, fullPath);
            if (instance != null) {
                this.references.add(instance);
            }

            return new SingleCollectorImpl<>(this.references, () -> this.tree.createUnresolvedReference(parent, fullPath));
        }

        @Override
        public <Z extends T> SingleKeyedCollector<T, T> withInstance(@NonNull Class<Z> instanceClazz) {
            final KeyedReference<? extends T> instance = this.tree.getInstanceOfClass(instanceClazz);
            if (instance != null) {
                this.references.add(instance);
            }

            return new SingleCollectorImpl<>(this.references, () -> this.tree.createUnresolvedReference(instanceClazz));
        }
    }

    public static class SingleAsReference<T extends KeyedInstance> extends FinderImpl<T> implements KeyedFinder.Single<T, SingleKeyedCollector<KeyedReference<? extends T>, T>> {
        public SingleAsReference(InstancesTree tree) {
            super(tree);
        }

        @Override
        public SingleKeyedCollector<KeyedReference<? extends T>, T> withInstance(@Nullable Class<? extends T> parent, @NonNull String fullPath) {
            final KeyedReference<? extends T> instance = this.tree.getInstance(parent, fullPath);
            if (instance != null) {
                this.references.add(instance);
            }

            return new SingleReferenceCollectorImpl<>(this.references, () -> this.tree.createUnresolvedReference(parent, fullPath));
        }

        @Override
        public <Z extends T> SingleKeyedCollector<KeyedReference<? extends T>, T> withInstance(@NonNull Class<Z> instanceClass) {
            final KeyedReference<? extends T> instance = this.tree.getInstanceOfClass(instanceClass);
            if (instance != null) {
                this.references.add(instance);
            }

            return new SingleReferenceCollectorImpl<>(this.references, () -> this.tree.createUnresolvedReference(instanceClass));
        }
    }

    public static class Multi<T extends KeyedInstance> extends FinderImpl<T> implements KeyedFinder.Multi<T> {
        public Multi(InstancesTree tree) {
            super(tree);
        }

        @Override
        public FinderImpl.Multi<T> withInstances(String path) {
            this.tree.collectInstancesFromPath(path, this.references);
            return this;
        }

        @Override
        public FinderImpl.Multi<T> withInstances(@NonNull Class<? extends T> parent) {
            this.tree.collectInstancesFromParent(parent, this.references);
            return this;
        }

        @Override
        public KeyedCollector<T> collect() {
            return new InstanceCollectorImpl<>(this.references);
        }

        @Override
        public KeyedCollector<KeyedReference<? extends T>> collectReferences() {
            return new ReferenceCollectorImpl<>(this.references);
        }

        @Override
        public FinderImpl.Multi<T> withInstance(@Nullable Class<? extends T> parent, @NonNull String fullPath) {
            final KeyedReference<? extends T> instance = this.tree.getInstance(parent, fullPath);
            if (instance != null) {
                this.references.add(instance);
            }

            return this;
        }

        @Override
        public <Z extends T> FinderImpl.Multi<T> withInstance(@NonNull Class<Z> instanceClass) {
            final KeyedReference<? extends T> instance = this.tree.getInstanceOfClass(instanceClass);
            if (instance != null) {
                this.references.add(instance);
            }

            return this;
        }
    }
}
