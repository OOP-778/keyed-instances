package dev.oop778.keyedinstances.test;

import org.junit.jupiter.api.Assertions;

import java.util.Arrays;

public class AssertHelper {
    public static void assertEqualsToOneOf(Object what, Object... whatElse) {
        for (final Object o : whatElse) {
            if (o.equals(what)) {
                return;
            }
        }

        Assertions.fail(String.format("%s does not equal any of %s", what, Arrays.toString(whatElse)));
    }
}
