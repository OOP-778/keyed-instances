package dev.oop778.keyedinstances.api.instance;

import dev.oop778.keyedinstances.api.KeyedRegistry;
import lombok.NonNull;
import org.intellij.lang.annotations.Pattern;

import java.util.List;

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
    @Pattern("[a-z_0-9.]+")
    String getKey();

    /**
     * The WithRegistry interface extends the KeyedInstance interface and provides
     * additional methods to interact with a KeyedRegistry.
     */
    interface WithRegistry extends KeyedInstance {
        @NonNull
        KeyedRegistry getRegistry();

        /**
         * {@link KeyedRegistry#getInstancePath(KeyedInstance)}
         */
        default String getPath() {
            return this.getRegistry().getInstancePath(this);
        }

        /**
         * {@link KeyedRegistry#getInstancePathFrom(Class, KeyedInstance)}
         */
        default String getPathFrom(Class<? extends KeyedInstance> parent) {
            return this.getRegistry().getInstancePathFrom(parent, this);
        }

        /**
         * {@link KeyedRegistry#registerInstance(KeyedInstance)}
         */
        default boolean register() {
            return this.getRegistry().registerInstance(this);
        }

        /**
         * Retrieves a list of subtypes for the current object.
         * The method finds instances of the current class type in the associated registry
         * and collects them into a list.
         *
         * @param <T> the type of KeyedInstance
         * @return a list of subtypes extending from the type T
         */
        default <T extends KeyedInstance> List<? extends T> getSubTypes() {
            return (List<? extends T>) this.getRegistry().find().multi().withInstances(this.getClass()).collect().toList();
        }
    }
}