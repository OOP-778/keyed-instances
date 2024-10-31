package dev.oop778.keyedinstances.impl.find;

import dev.oop778.keyedinstances.api.KeyedReference;
import dev.oop778.keyedinstances.api.find.KeyedFinder;
import dev.oop778.keyedinstances.api.find.KeyedFinderSelector;
import dev.oop778.keyedinstances.api.find.SingleKeyedCollector;
import dev.oop778.keyedinstances.api.instance.KeyedInstance;
import dev.oop778.keyedinstances.impl.InstancesTree;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FinderSelectorImpl<T extends KeyedInstance> implements KeyedFinderSelector<T> {
    private final InstancesTree tree;

    @Override
    public KeyedFinder.Single<T, SingleKeyedCollector<T, T>> single() {
        return new FinderImpl.Single<>(this.tree);
    }

    @Override
    public KeyedFinder.Single<T, SingleKeyedCollector<KeyedReference<? extends T>, T>> singleAsReference() {
        return new FinderImpl.SingleAsReference<>(this.tree);
    }

    @Override
    public KeyedFinder.Multi<T> multi() {
        return new FinderImpl.Multi<>(this.tree);
    }
}
