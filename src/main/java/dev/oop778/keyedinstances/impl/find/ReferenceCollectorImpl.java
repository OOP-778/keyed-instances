package dev.oop778.keyedinstances.impl.find;

import dev.oop778.keyedinstances.api.KeyedReference;
import dev.oop778.keyedinstances.api.find.KeyedCollector;
import dev.oop778.keyedinstances.api.instance.KeyedInstance;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;

@ApiStatus.Internal
@RequiredArgsConstructor
public class ReferenceCollectorImpl<T extends KeyedInstance> implements KeyedCollector<KeyedReference<? extends T>> {
    private final List<KeyedReference<? extends T>> instances;

    @Override
    public List<? extends KeyedReference<? extends T>> toList() {
        return this.instances;
    }

    @Override
    public Optional<? extends KeyedReference<? extends T>> first() {
        return this.instances.isEmpty() ? Optional.empty() : Optional.of((KeyedReference<T>) this.instances.get(0));
    }

    @Override
    public KeyedReference<? extends T> firstOrThrow(@NonNull Supplier<String> messageSupplier) {
        return this.first().orElseThrow(() -> new NoSuchElementException(messageSupplier.get()));
    }
}
