package dev.oop778.keyedinstances.test;

import dev.oop778.keyedinstances.api.KeyedReference;
import dev.oop778.keyedinstances.api.KeyedRegistry;
import dev.oop778.keyedinstances.api.KeyedRegistryBuilder;
import dev.oop778.keyedinstances.api.annotation.Keyed;
import dev.oop778.keyedinstances.api.instance.KeyedInstance;
import dev.oop778.keyedinstances.impl.path.IKeyedPath;
import dev.oop778.keyedinstances.impl.path.KeyedPathFactory;
import dev.oop778.keyedinstances.impl.path.MultiRootKeyedPath;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.oop778.keyedinstances.test.AssertHelper.assertEqualsToOneOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KeyedInstancesTest {
    private KeyedRegistry keyedRegistry;

    @BeforeEach
    void setup() {
        this.keyedRegistry = KeyedRegistryBuilder.create().build();
    }

    @Test
    void testMultiParentAB() {
        final IKeyedPath<SubjectAB> result = KeyedPathFactory.create(SubjectAB.class);
        assertEquals(result.getClass(), MultiRootKeyedPath.class);

        final MultiRootKeyedPath<SubjectAB> multi = ((MultiRootKeyedPath) result);
        assertEquals(multi.getRootToPath().keySet().size(), 2);

        this.keyedRegistry.registerInstance(new SubjectAB());
        assertTrue(this.keyedRegistry.find().single().withInstance("a.subjectab").first().isPresent());
        assertTrue(this.keyedRegistry.find().single().withInstance("b.subjectab").first().isPresent());
        assertTrue(this.keyedRegistry.find().single().withInstance(IB.class, "subjectab").first().isPresent());
        assertTrue(this.keyedRegistry.find().single().withInstance(SubjectAB.class).first().isPresent());
    }

    @Test
    void testMultiParentABC() {
        final IKeyedPath<SubjectABC> result = KeyedPathFactory.create(SubjectABC.class);
        assertEquals(result.getClass(), MultiRootKeyedPath.class);

        final MultiRootKeyedPath<SubjectABC> multi = ((MultiRootKeyedPath) result);
        assertEquals(multi.getRootToPath().keySet().size(), 2);

        final SubjectABC subjectABC = new SubjectABC();
        this.keyedRegistry.registerInstance(subjectABC);
        assertEqualsToOneOf(subjectABC.getPath(), "b.c.subjectabc", "a.subjectabc");
        assertEquals("subjectabc", subjectABC.getPathFrom(IA.class));
        assertEquals("c.subjectabc", subjectABC.getPathFrom(IB.class));
        assertEquals("subjectabc", subjectABC.getPathFrom(CIB.class));

        assertTrue(this.keyedRegistry.find().single().withInstance("a.subjectabc").first().isPresent());
        assertTrue(this.keyedRegistry.find().single().withInstance("b.c.subjectabc").first().isPresent());
        assertTrue(this.keyedRegistry.find().single().withInstance(IB.class, "c.subjectabc").first().isPresent());
        assertTrue(this.keyedRegistry.find().single().withInstance(CIB.class, "subjectabc").first().isPresent());
        assertTrue(this.keyedRegistry.find().single().withInstance(SubjectABC.class).first().isPresent());
    }

    @Test
    void testGeneralInstances() {
        final SubjectAB subjectAB = new SubjectAB();
        final SubjectABC subjectABC = new SubjectABC();
        this.keyedRegistry.registerInstance(subjectAB);
        this.keyedRegistry.registerInstance(subjectABC);

        final List<? extends IA> instancesOfRoot = this.keyedRegistry.<IA>find().multi().withInstances(IA.class).collect().toList();
        assertEquals(instancesOfRoot.size(), 2);
        assertEquals(instancesOfRoot.get(0).getKey(), "subjectab");
        assertEquals(instancesOfRoot.get(1).getKey(), "subjectabc");
    }

    @Keyed("a")
    interface IA extends KeyedInstance {
    }

    @Keyed("b")
    interface IB extends KeyedInstance {
    }

    @Keyed("c")
    interface CIB extends IB {}

    class SubjectAB implements IA, IB, KeyedInstance.WithRegistry {
        @Override
        public @NonNull KeyedRegistry getRegistry() {
            return KeyedInstancesTest.this.keyedRegistry;
        }

        @Override
        public @NonNull String getKey() {
            return "subjectab";
        }
    }

    class SubjectABC implements IA, CIB, KeyedInstance.WithRegistry {
        @Override
        public @NonNull String getKey() {
            return "subjectabc";
        }

        @Override
        public @NonNull KeyedRegistry getRegistry() {
            return KeyedInstancesTest.this.keyedRegistry;
        }
    }
}
