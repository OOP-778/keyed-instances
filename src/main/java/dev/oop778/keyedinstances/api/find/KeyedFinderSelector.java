package dev.oop778.keyedinstances.api.find;

import dev.oop778.keyedinstances.api.KeyedReference;
import dev.oop778.keyedinstances.api.instance.KeyedInstance;

public interface KeyedFinderSelector<T extends KeyedInstance> {
    KeyedFinder.Single<T, SingleKeyedCollector<T, T>> single();
    KeyedFinder.Single<T, SingleKeyedCollector<KeyedReference<? extends T>, T>> singleAsReference();
    KeyedFinder.Multi<T> multi();
}
