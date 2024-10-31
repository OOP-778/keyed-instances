package dev.oop778.keyedinstances.api;

import dev.oop778.keyedinstances.api.instance.KeyedInstance;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

/**
 * Functional interface for updating keyed instances.
 * <p>
 * The KeyedInstanceUpdater allows the user to define custom update strategies
 * for keyed instances when new values are available. Two default implementations
 * are provided:
 * - NEWER_VALUE: always accepts the new value.
 * - NOT_SUPPORTED: throws an UnsupportedOperationException, disallowing updates.
 */
@FunctionalInterface
public interface KeyedInstanceUpdater {
    /**
     * Always accepts the new value in keyed instance updates.
     * <p>
     * This updater simply returns the new value, replacing any previous value.
     * It is used when the update strategy dictates that the latest value should
     * always be used.
     */
    static KeyedInstanceUpdater NEWER_VALUE = ($1, $2) -> $2;
    /**
     * An instance of KeyedInstanceUpdater that throws an UnsupportedOperationException
     * on every update attempt.
     * <p>
     * This updater is used to disallow updates to keyed instances, ensuring that the
     * existing value cannot be changed. It is typically used in scenarios where
     * immutability of the instance is required or updates are not permitted by design.
     */
    static KeyedInstanceUpdater NOT_SUPPORTED = ($1, $2) -> {
        throw new UnsupportedOperationException();
    };

    /**
     * Updates the instance with a new value based on the provided previous value and new value.
     * <p>
     * This method defines the essential contract for updating an instance when a new value
     * is provided. The behavior of this method depends on the specific implementation of
     * the KeyedInstanceUpdater.
     *
     * @param previousValue the current value stored
     * @param newValue the new value to update with
     * @return the updated value, which can be either the previous value, new value, or a computed result based on the two
     */
    @NonNull
    KeyedInstance update(@NonNull KeyedInstance previousValue, @NonNull KeyedInstance newValue);
}
