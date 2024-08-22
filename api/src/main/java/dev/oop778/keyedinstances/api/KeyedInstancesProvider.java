package dev.oop778.keyedinstances.api;

import dev.oop778.keyedinstances.api.util.LaterInitializedReference;

public interface KeyedInstancesProvider {
    static LaterInitializedReference<KeyedInstancesProvider> REFERENCE = new LaterInitializedReference<>();

    KeyedInstances get();
}
