package dev.oop778.keyedinstances.impl.path;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Data
public class MultiRootKeyedPath<T> implements IKeyedPath {
    private final Class<T> keyedClass;
    private final Map<Class<?>, List<Class<?>>> rootToPath;

    public Map.Entry<Class<?>, List<Class<?>>> getFirstPath() {
        return this.rootToPath.entrySet().stream().findFirst().get();
    }
}
