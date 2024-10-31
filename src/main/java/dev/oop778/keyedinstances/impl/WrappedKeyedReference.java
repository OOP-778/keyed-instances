package dev.oop778.keyedinstances.impl;

import dev.oop778.keyedinstances.api.KeyedReference;
import dev.oop778.keyedinstances.api.instance.KeyedInstance;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@Setter
@Getter
public class WrappedKeyedReference<T extends KeyedInstance> implements KeyedReference<T> {
    private @Nullable KeyedReference<T> reference;

    @Override
    public T get() {
        return this.reference == null ? null : this.reference.get();
    }
}
