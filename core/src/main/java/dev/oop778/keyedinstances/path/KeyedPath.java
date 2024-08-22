package dev.oop778.keyedinstances.path;

import dev.oop778.keyedinstances.api.instance.KeyedInstance;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class KeyedPath<T> {
    private List<Class<?>> hierarchy;
    private Class<? extends KeyedInstance> rootClass;
    private Class<T> keyedClass;
}
