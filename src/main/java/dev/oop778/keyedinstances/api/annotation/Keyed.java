package dev.oop778.keyedinstances.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Keyed {
    @org.intellij.lang.annotations.Pattern("[a-z_0-9.]+") String value();
}
