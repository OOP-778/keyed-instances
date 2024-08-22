package dev.oop778.keyedinstances.api.util;

import lombok.NonNull;

public class LaterInitializedReference<T> {
    private volatile T value;

    public T get() {
        if (this.value == null) {
            throw new IllegalStateException("Value has not been initialized");
        }

        return this.value;
    }

    public void set(@NonNull T value) {
        if (this.value != null) {
            throw new IllegalStateException("Value has been initialized already");
        }

        synchronized (this) {
            if (this.value != null) {
                throw new IllegalStateException("Value has been initialized already");
            }

            this.value = value;
        }
    }
}
