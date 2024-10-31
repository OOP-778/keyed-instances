package dev.oop778.keyedinstances.impl.find;

import dev.oop778.keyedinstances.api.KeyedReference;
import dev.oop778.keyedinstances.api.find.SingleKeyedCollector;
import dev.oop778.keyedinstances.api.instance.KeyedInstance;

import java.util.List;
import java.util.function.Supplier;

public class SingleReferenceCollectorImpl<T extends KeyedInstance> extends ReferenceCollectorImpl<T> implements SingleKeyedCollector<KeyedReference<? extends T>, T> {
    private final Supplier<KeyedReference<? extends T>> unresolvedReferenceSupplier;

    public SingleReferenceCollectorImpl(List<KeyedReference<? extends T>> instances, Supplier<KeyedReference<? extends T>> unresolvedReferenceSupplier) {
        super(instances);
        this.unresolvedReferenceSupplier = unresolvedReferenceSupplier;
    }

    @Override
    public KeyedReference<? extends T> firstOrCreateUnresolvedReference() {
        return this.unresolvedReferenceSupplier.get();
    }
}
