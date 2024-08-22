package dev.oop778.keyedinstances;

import dev.oop778.keyedinstances.api.KeyedInstances;
import dev.oop778.keyedinstances.api.KeyedWalker;
import dev.oop778.keyedinstances.api.instance.KeyedInstance;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class KeyedInstancesImpl implements KeyedInstances {

    @Override
    public void registerInstance(@NonNull KeyedInstance instance) {

    }

    @Override
    public void unregisterInstance(@NonNull KeyedInstance instance) {

    }

    @Override
    public @Nullable KeyedInstance getInstance(@Nullable Class<? extends KeyedInstance> root, @NonNull String fullPath) {
        return null;
    }

    @Override
    public <T extends KeyedInstance> @Nullable T getInstanceOfClass(@NonNull Class<T> clazz) {
        return null;
    }

    @Override
    public Collection<? extends KeyedInstance> getInstances() {
        return Collections.emptyList();
    }

    @Override
    public <T extends KeyedInstance> List<? extends T> getInstancesOfRoot(@NonNull Class<T> root) {
        return Collections.emptyList();
    }

    @Override
    public <T extends KeyedInstance> List<? extends T> getInstancesOfRoot(@NonNull String rootPath) {
        return Collections.emptyList();
    }

    @Override
    public void walk(@NonNull KeyedWalker walker) {

    }
}
