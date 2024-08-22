package dev.oop778.keyedinstances.path;

import dev.oop778.keyedinstances.api.instance.KeyedInstance;

import java.util.List;
import java.util.Map;

public class MultiRootKeyedPath<T> extends KeyedPath<T> {

    public MultiRootKeyedPath(List<Class<?>> hierarchy, Class<? extends KeyedInstance> rootClass, Class<T> keyedClass) {
        super(hierarchy, rootClass, keyedClass);
    }
}
