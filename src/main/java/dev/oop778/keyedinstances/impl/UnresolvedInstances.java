package dev.oop778.keyedinstances.impl;

import dev.oop778.keyedinstances.api.KeyedReference;
import dev.oop778.keyedinstances.api.instance.KeyedInstance;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RequiredArgsConstructor
public class UnresolvedInstances {
    private final InstancesTree tree;
    private final List<Resolver> resolvers = new CopyOnWriteArrayList<>();

    public <T extends KeyedInstance> KeyedReference<T> createOfClass(@NonNull Class<T> instanceClass) {
        final WrappedKeyedReference<T> wrappedKeyedReference = new WrappedKeyedReference<>(null);

        this.resolvers.add(reference -> {
            if (reference.get().getClass() != instanceClass) {
                return false;
            }

            wrappedKeyedReference.setReference((KeyedReference<T>) reference);
            return true;
        });

        return wrappedKeyedReference;
    }

    public <T extends KeyedInstance> KeyedReference<T> createOfPath(@Nullable Class<? extends T> parent, @NonNull String fullPath) {
        final WrappedKeyedReference<T> wrappedKeyedReference = new WrappedKeyedReference<>(null);
        this.resolvers.add(reference -> {
            final KeyedInstance keyedInstance = reference.get();

            boolean resolve = false;
            if (parent != null) {
                resolve = keyedInstance.getClass().isAssignableFrom(parent);
                if (resolve) {
                    resolve = tree.getPathFrom(parent, keyedInstance).contentEquals(fullPath);
                }
            } else {
                resolve = tree.getPaths(keyedInstance).contains(fullPath);
            }

            if (resolve) {
                wrappedKeyedReference.setReference((KeyedReference<T>) reference);
                return true;
            }

            return false;
        });

        return wrappedKeyedReference;
    }

    public void resolve(InstancesTree.TreeReference<? extends KeyedInstance> reference) {
        this.resolvers.removeIf(resolver -> resolver.tryResolve(reference));
    }

    static interface Resolver {
        boolean tryResolve(InstancesTree.TreeReference<? extends KeyedInstance> reference);
    }
}
