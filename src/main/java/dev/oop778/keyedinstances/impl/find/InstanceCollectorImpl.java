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
import java.util.stream.Collectors;

@ApiStatus.Internal
@RequiredArgsConstructor
public class InstanceCollectorImpl<T extends KeyedInstance> implements KeyedCollector<T> {
    private final List<KeyedReference<? extends T>> instances;

    @Override
    public List<? extends T> toList() {
        return this.instances.stream()
                .map(KeyedReference::get)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<? extends T> first() {
        return this.instances.isEmpty() ? Optional.empty() : Optional.of(this.instances.get(0).get());
    }

    @Override
    public T firstOrThrow(@NonNull Supplier<String> messageSupplier) {
        return this.first().orElseThrow(() -> new NoSuchElementException(messageSupplier.get()));
    }
}
