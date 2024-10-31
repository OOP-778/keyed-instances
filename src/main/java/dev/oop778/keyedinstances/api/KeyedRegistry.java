package dev.oop778.keyedinstances.api;

import dev.oop778.keyedinstances.api.find.KeyedFinderSelector;
import dev.oop778.keyedinstances.api.instance.KeyedInstance;
import lombok.NonNull;

import java.util.Collection;

/**
 * KeyedRegistry interface provides methods for managing and retrieving instances
 * of objects that implement the KeyedInstance interface. It allows registering,
 * unregistering, and finding instances based on their class type or key.
 */
public interface KeyedRegistry {
    /**
     * Creates and returns a builder for constructing a KeyedRegistry.
     *
     * @return a KeyedRegistryBuilder instance for building a KeyedRegistry
     */
    static KeyedRegistryBuilder builder() {
        return KeyedRegistryBuilder.create();
    }

    /**
     * Registers an instance of {@link KeyedInstance} in the KeyedRegistry.
     *
     * @param instance The instance to be registered, must not be null.
     * @return true if the instance was successfully registered or updated otherwise false
     */
    boolean registerInstance(@NonNull KeyedInstance instance);

    /**
     * Unregisters a {@link KeyedInstance} from the KeyedRegistry.
     *
     * @param instance The instance to be unregistered, must not be null.
     */
    void unregisterInstance(@NonNull KeyedInstance instance);

    /**
     * Unregisters all instances of the specified class type from the KeyedRegistry.
     *
     * @param <T> the type of the instances to be unregistered
     * @param ofClass the class type of instances to be unregistered, must not be null
     * @return a collection of instances that were unregistered, which extends the specified class type
     */
    <T> Collection<? extends T> unregisterInstances(@NonNull Class<T> ofClass);

    /**
     * Retrieves a collection of all registered {@link KeyedInstance} objects.
     *
     * @return immutable collection of {@link KeyedInstance} objects managed by the KeyedRegistry
     */
    Collection<? extends KeyedInstance> getInstances();

    /**
     * Initiates a KeyedFinder to locate instances of KeyedInstance based on specified criteria.
     *
     * @param <T> the type of KeyedInstance to be found
     * @return a KeyedFinder instance configured for locating objects of type T
     */
    <T extends KeyedInstance> KeyedFinderSelector<T> find();

    String getInstancePath(@NonNull KeyedInstance instance);

    String getInstancePathFrom(@NonNull Class<? extends KeyedInstance> parent, @NonNull KeyedInstance instance);
}
