package dev.oop778.keyedinstances.test;

import dev.oop778.keyedinstances.api.annotation.Keyed;
import dev.oop778.keyedinstances.api.instance.KeyedInstance;
import lombok.NonNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MultiParentTest {

    @Test
    void testMultiParent() {

    }

    class SubjectA implements IA {

        @Override
        public @NonNull String key() {
            return "a";
        }
    }

    @Keyed("a")
    class A {}

    @Keyed("b")
    class B {}

    interface IA extends KeyedInstance {}

    interface IB extends KeyedInstance {}
}
