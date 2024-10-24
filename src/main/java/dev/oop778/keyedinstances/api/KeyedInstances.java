package dev.oop778.keyedinstances.api;

import dev.oop778.keyedinstances.api.instance.KeyedInstance;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * KeyedInstance represents a tree of registered instances under their keys, you can retrieve a keyed instance by its path, by class or by iterating of leafs
 */
public interface KeyedInstances {

    static KeyedInstances get() {
        return KeyedInstancesProvider.REFERENCE.get().get();
    }

    /**
     * Registers the given {@link KeyedInstance} instance.
     *
     * @param instance the {@link KeyedInstance} instance to be registered
     * @throws NullPointerException if the instance is null
     */
    void registerInstance(@NonNull KeyedInstance instance);

    /**
     * Unregisters the given {@link KeyedInstance} instance.
     *
     * @param instance the {@link KeyedInstance} instance to be unregistered
     * @throws NullPointerException if the instance is null
     */
    void unregisterInstance(@NonNull KeyedInstance instance);

    Collection<KeyedInstance> unregisterInstances(@NonNull Class<? extends KeyedInstance> ofClass);

    /**
     * Returns an Optional {@link KeyedInstance} for the given full path.
     *
     * @param fullPath the full path of the instance
     * @return an Optional containing the KeyedInstance associated with the given full path, or an empty Optional if no instance found
     */
    default Optional<KeyedInstance> getOptionalInstance(@NonNull String fullPath) {
        return Optional.ofNullable(this.getInstance(fullPath));
    }

    /**
     * Returns a {@link KeyedInstance} for the given full path.
     *
     * @param fullPath the full path of the instance
     * @return a {@link KeyedInstance} associated with the given full path, or null if no instance found
     */
    @Nullable
    default KeyedInstance getInstance(@NonNull String fullPath) {
        return this.getInstance(null, fullPath);
    }

    /**
     * Retrieves a {@link KeyedInstance} based on the given root class and full path.
     *
     * @param parent     parent of the instance or null to start search from the top of the tree.
     * @param fullPath the full path of the instance
     * @return a {@link KeyedInstance} associated with the given full path, or null if no instance found
     */
    @Nullable
    KeyedInstance getInstance(@Nullable Class<? extends KeyedInstance> parent, @NonNull String fullPath);

    /**
     * Returns an Optional instance of {@link KeyedInstance} for the given root class and path.
     *
     * @param parent parent of the instance or null to start search from the top of the tree.
     * @param path the path of the instance
     * @return an Optional instance of {@link KeyedInstance} associated with the given root class and path, or an empty Optional if no instance found
     */
    default Optional<KeyedInstance> getOptionalInstance(@Nullable Class<? extends KeyedInstance> parent, String path) {
        return Optional.ofNullable(this.getInstance(parent, path));
    }

    /**
     * Retrieves an optional instance of a class that extends KeyedInstance from the KeyedInstances container.
     *
     * @param clazz the class of the instance to retrieve
     * @return an Optional containing the instance of the given class if it exists in the container, or an empty Optional if it does not
     */
    default <T extends KeyedInstance> Optional<T> getOptionalInstanceOfClass(@NonNull Class<T> clazz) {
        return Optional.ofNullable(this.getInstanceOfClass(clazz));
    }

    /**
     * Retrieves an instance of a class that extends KeyedInstance from the KeyedInstances container.
     *
     * @param clazz the class of the instance to retrieve
     * @param <T> the type of the instance to retrieve
     * @return an instance of the given class if it exists in the container, or null if it does not
     */
    @Nullable
    <T extends KeyedInstance> T getInstanceOfClass(@NonNull Class<T> clazz);

    /**
     * Returns a collection of {@link KeyedInstance} objects.
     * This method retrieves all instances stored in the KeyedInstances container.
     *
     * @return a collection of KeyedInstance objects representing all instances in the container
     */
    Collection<? extends KeyedInstance> getInstances();

    /**
     * Retrieves a list of instances of a class that extends KeyedInstance from the KeyedInstances container.
     *
     * @param root the root class of the instance tree, cannot be null
     * @param <T> the type of instances to retrieve
     * @return a list of instances of the specified class that are descendants of the root class, or an empty list if no instances are found
     * @throws NullPointerException if the root class is null
     */
    <T extends KeyedInstance> List<? extends T> getInstancesOfRoot(@NonNull Class<T> root);

    /**
     * Retrieves a list of instances of a class that extends KeyedInstance from the KeyedInstances
     * according to the given root path.
     *
     * @param rootPath the root path of the instance tree, cannot be null
     * @param <T> the type of instances to retrieve
     * @return a list of instances of the specified class that are descendants of the root path,
     *         or an empty list if no instances are found
     * @throws NullPointerException if the root path is null
     */
    <T extends KeyedInstance> List<? extends T> getInstancesOfRoot(@NonNull String rootPath);
}
