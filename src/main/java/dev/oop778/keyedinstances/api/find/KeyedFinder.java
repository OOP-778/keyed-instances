package dev.oop778.keyedinstances.api.find;

import dev.oop778.keyedinstances.api.KeyedReference;
import dev.oop778.keyedinstances.api.instance.KeyedInstance;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

/**
 * The KeyedFinder interface provides methods to locate and collect instances of objects that
 * implement the KeyedInstance interface. Instances can be located based on their path,
 * parent class, or specific class type.
 *
 * @param <T> the type of the KeyedInstance to be found
 */
public interface KeyedFinder<T extends KeyedInstance> {

    interface Single<T extends KeyedInstance, NEXT_STAGE> extends KeyedFinder<T> {
        /**
         * Specifies the path to locate a single instance of the type T.
         *
         * @param path the path to the instance to be located
         * @return a KeyedFinder object with the specified instance path
         */
        default NEXT_STAGE withInstance(@NonNull String path) {
            return this.withInstance(null, path);
        }

        /**
         * Specifies the parent class and the full path to locate a single instance of the type T.
         *
         * @param parent the parent class of the instance to be located, can be null
         * @param fullPath the full path to the instance to be located, cannot be null
         * @return a KeyedFinder object with the specified instance path
         */
        NEXT_STAGE withInstance(@Nullable Class<? extends T> parent, @NonNull String fullPath);

        /**
         * Specifies the instance class to locate a single instance of the given type.
         *
         * @param <Z> the type of the instance to be located, which extends T
         * @param instance the class of the instance to be located, cannot be null
         * @return a KeyedFinder object with the specified instance class
         */
        <Z extends T> NEXT_STAGE withInstance(@NonNull Class<Z> instance);
    }

    interface Multi<T extends KeyedInstance> extends Single<T, Multi<T>> {
        /**
         * Specifies the path to locate multiple instances of the type T.
         *
         * @param path the path to the instances to be located
         * @return a KeyedFinder object with the specified instances path
         */
        Multi<T> withInstances(String path);

        /**
         * Specifies the parent class to locate multiple instances of the given type.
         *
         * @param parent the parent class of the instances to be located, cannot be null
         * @return a KeyedFinder object with the specified parent class
         */
        Multi<T> withInstances(@NonNull Class<? extends T> parent);

        /**
         * Collects instances specified by the KeyedFinder criteria into a KeyedCollector.
         *
         * @return a KeyedCollector containing all instances that match the specified criteria
         */
        KeyedCollector<T> collect();

        /**
         * Collects references to instances specified by the KeyedFinder criteria into a KeyedCollector.
         *
         * @return a KeyedCollector containing references to all instances that match the specified criteria
         */
        KeyedCollector<KeyedReference<? extends T>> collectReferences();
    }
}
