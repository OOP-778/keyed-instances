package dev.oop778.keyedinstances.api.annotation;

import org.intellij.lang.annotations.Pattern;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to provide a unique key for a group, usually representing a specific type
 * of instance. The key follows a specific pattern consisting of lowercase letters,
 * digits, underscores, and periods.
 * <p>
 * The key provided by this annotation can be used to identify or retrieve instances
 * in a structured hierarchy or path-based lookup system.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface KeyedGroupId {
    @Pattern("[a-z_0-9.]+") String value();
}
