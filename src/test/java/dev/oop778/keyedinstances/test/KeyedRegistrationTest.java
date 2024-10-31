package dev.oop778.keyedinstances.test;

import dev.oop778.keyedinstances.api.KeyedInstanceUpdater;
import dev.oop778.keyedinstances.api.KeyedReference;
import dev.oop778.keyedinstances.api.KeyedRegistry;
import dev.oop778.keyedinstances.api.annotation.Keyed;
import dev.oop778.keyedinstances.api.instance.KeyedInstance;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KeyedRegistrationTest {
    @Test
    void testNoopUpdate() {
        final KeyedRegistry build = KeyedRegistry.builder()
                .withUpdater(KeyedInstanceUpdater.NOT_SUPPORTED)
                .build();

        build.registerInstance(new Child("a"));
        assertThrows(UnsupportedOperationException.class, () -> build.registerInstance(new Child("a")));
    }

    @Test
    void testNewerUpdate() {
        final KeyedRegistry build = KeyedRegistry.builder()
                .withUpdater(KeyedInstanceUpdater.NEWER_VALUE)
                .build();

        final Child older = new Child("a");
        build.registerInstance(older);

        final Child newer = new Child("a");
        build.registerInstance(newer);

        assertEquals(build.find().single().withInstance("parent.a").firstOrNull(), newer);
    }

    @Test
    void testReferenceUpdate() {
        final KeyedRegistry build = KeyedRegistry.builder()
                .withUpdater(KeyedInstanceUpdater.NEWER_VALUE)
                .build();

        final Child older = new Child("a");
        build.registerInstance(older);

        final KeyedReference<? extends Child> reference = build.<Child>find().singleAsReference().withInstance("parent.a").firstOrNull();

        final Child newer = new Child("a");
        build.registerInstance(newer);

        assertEquals(reference.get(), newer);
    }

    @Test
    void testUnresolvedReference() {
        final KeyedRegistry build = KeyedRegistry.builder()
                .withUpdater(KeyedInstanceUpdater.NEWER_VALUE)
                .build();

        final KeyedReference<? extends Child> reference = build.<Child>find().single().withInstance("parent.a").firstOrCreateUnresolvedReference();
        assertNull(reference.get());

        final Child object = new Child("a");
        build.registerInstance(object);
        assertEquals(object, reference.get());
    }

    @Keyed("parent")
    private static interface Parent extends KeyedInstance {}

    @RequiredArgsConstructor
    @Getter
    private static class Child implements Parent {
        private final String key;
    }
}
