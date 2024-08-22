package dev.oop778.keyedinstances.api.instance;

import lombok.NonNull;

/**
 * This interface represents a keyed instance.
 * A keyed instance is an object that has a unique key from the parent associated with it.
 * This key can be used to identify or retrieve the instance knowing the full path or parent and key
 */
public interface KeyedInstance {

    /**
     * Returns the unique key associated with this instance.
     *
     * @return the unique key of this instance
     */
    @NonNull
    String key();
}