package dev.oop778.keyedinstances.impl.find;

import dev.oop778.keyedinstances.api.KeyedReference;
import dev.oop778.keyedinstances.api.find.SingleKeyedCollector;
import dev.oop778.keyedinstances.api.instance.KeyedInstance;

import java.util.List;
import java.util.function.Supplier;

public class SingleCollectorImpl<T extends KeyedInstance> extends InstanceCollectorImpl<T> implements SingleKeyedCollector<T, T> {
    private final Supplier<KeyedReference<? extends T>> unresolvedReferenceSupplier;

    public SingleCollectorImpl(List<KeyedReference<? extends T>> instances, Supplier<KeyedReference<? extends T>> unresolvedReferenceSupplier) {
        super(instances);
        this.unresolvedReferenceSupplier = unresolvedReferenceSupplier;
    }

    @Override
    public KeyedReference<? extends T> firstOrCreateUnresolvedReference() {
        return this.unresolvedReferenceSupplier.get();
    }
}
