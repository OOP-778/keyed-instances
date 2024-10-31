package dev.oop778.keyedinstances.api.find;

import dev.oop778.keyedinstances.api.KeyedReference;
import dev.oop778.keyedinstances.api.instance.KeyedInstance;

public interface SingleKeyedCollector<T, ACTUAL_VALUE extends KeyedInstance> extends KeyedCollector<T> {
    KeyedReference<? extends ACTUAL_VALUE> firstOrCreateUnresolvedReference();
}
