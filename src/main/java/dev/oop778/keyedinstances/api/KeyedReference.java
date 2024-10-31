package dev.oop778.keyedinstances.api;

import dev.oop778.keyedinstances.api.instance.KeyedInstance;

/**
 * This interface represents a reference to a keyed instance.
 * <p>
 * The reference doesn't get updated even if the value gets updated this way you can always have up-to-date value
 *
 * @param <T> the type of the keyed instance that this reference points to
 */
public interface KeyedReference<T extends KeyedInstance> {
    T get();
}
