package dev.oop778.keyedinstances.impl;

import dev.oop778.keyedinstances.api.KeyedInstanceUpdater;
import dev.oop778.keyedinstances.api.KeyedRegistry;
import dev.oop778.keyedinstances.api.KeyedRegistryBuilder;
import lombok.NonNull;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class KeyedRegistryBuilderImpl implements KeyedRegistryBuilder {
    protected KeyedInstanceUpdater updater = KeyedInstanceUpdater.NEWER_VALUE;

    @Override
    public KeyedRegistryBuilder withUpdater(@NonNull KeyedInstanceUpdater updater) {
        this.updater = updater;
        return this;
    }

    @Override
    public KeyedRegistry build() {
        return new KeyedRegistryImpl(this);
    }
}
