package dev.oop778.keyedinstances.api;

import dev.oop778.keyedinstances.impl.KeyedRegistryBuilderImpl;
import lombok.NonNull;

/**
 * The KeyedRegistryBuilder interface provides a builder pattern for constructing
 * instances of {@link KeyedRegistry}. It allows customization of the registry
 * through various methods and creates a fully configured KeyedRegistry.
 */
public interface KeyedRegistryBuilder {
    static KeyedRegistryBuilder create() {
        return new KeyedRegistryBuilderImpl();
    }

    /**
     * Configures the KeyedRegistryBuilder to use the provided KeyedInstanceUpdater.
     *
     * @param updater the KeyedInstanceUpdater to be used for updating instances
     * @return the KeyedRegistryBuilder instance with the configured updater
     */
    KeyedRegistryBuilder withUpdater(@NonNull KeyedInstanceUpdater updater);

    /**
     * Constructs and returns a fully configured instance of {@link KeyedRegistry}.
     *
     * @return a newly built {@link KeyedRegistry} instance
     */
    KeyedRegistry build();
}
