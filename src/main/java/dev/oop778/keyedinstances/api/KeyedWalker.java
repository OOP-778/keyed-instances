package dev.oop778.keyedinstances.api;

import dev.oop778.keyedinstances.api.instance.KeyedInstance;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public interface KeyedWalker {
    VisitResult onTreeVisit(@NonNull Class<?> headClass, @Nullable KeyedInstance instance);

    VisitResult onLeafVisit(@NonNull Class<?> leafClass, @Nullable KeyedInstance instance);

    enum VisitResult {
        CONTINUE,
        ABORT,
        DO_NOT_VISIT_CHILDREN
    }
}
