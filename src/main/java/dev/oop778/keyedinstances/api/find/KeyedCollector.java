package dev.oop778.keyedinstances.api.find;

import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * The KeyedCollector interface defines a contract for collecting and retrieving a collection of objects
 * (of type T) that can be identified with a unique key.
 *
 * @param <T> the type of objects to be collected
 */
public interface KeyedCollector<T> {
    /**
     * Converts the collected instances to a list.
     *
     * @return a list containing all collected instances of type T
     */
    List<? extends T> toList();

    /**
     * Retrieves the first collected instance of type T.
     *
     * @return an Optional containing the first collected instance of type T, or an empty Optional
     * if there are no collected instances
     */
    Optional<? extends T> first();

    default T firstOrNull() {
        return this.first().orElse(null);
    }

    /**
     * Retrieves the first collected instance of type T or throws an exception with a custom message if no instances are found.
     *
     * @param messageSupplier a supplier that provides the exception message if no instances are found
     * @return the first collected instance of type T
     * @throws IllegalStateException if there are no collected instances
     */
    T firstOrThrow(@NonNull Supplier<String> messageSupplier);

    /**
     * Retrieves the first collected instance of type T or throws an exception if no instances are found.
     *
     * @return the first collected instance of type T
     * @throws IllegalStateException if there are no collected instances
     */
    default T firstOrThrow() {
        return this.firstOrThrow(() -> "No instances found");
    }
}
