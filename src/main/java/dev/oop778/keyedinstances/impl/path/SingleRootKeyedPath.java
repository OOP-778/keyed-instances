package dev.oop778.keyedinstances.impl.path;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Data
public class SingleRootKeyedPath<T> implements IKeyedPath {
    private final Class<T> keyedClass;
    private final Class<?> rootClass;
    private final List<Class<?>> path;
}

