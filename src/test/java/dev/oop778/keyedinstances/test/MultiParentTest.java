package dev.oop778.keyedinstances.test;

import dev.oop778.keyedinstances.impl.KeyedInstancesImpl;
import dev.oop778.keyedinstances.api.KeyedInstances;
import dev.oop778.keyedinstances.api.KeyedInstancesProvider;
import dev.oop778.keyedinstances.api.annotation.Keyed;
import dev.oop778.keyedinstances.api.instance.KeyedInstance;
import dev.oop778.keyedinstances.impl.path.IKeyedPath;
import dev.oop778.keyedinstances.impl.path.KeyedPathFactory;
import dev.oop778.keyedinstances.impl.path.MultiRootKeyedPath;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// This test should ensure that SubjectAB is available by a.subjectA and b.subjectA
public class MultiParentTest {
    @BeforeAll
    static void setup() {
        final KeyedInstancesImpl keyedInstances = new KeyedInstancesImpl();
        KeyedInstancesProvider.REFERENCE.set(() -> keyedInstances);
    }

    @Test
    void testMultiParentAB() {
        final IKeyedPath<SubjectAB> result = KeyedPathFactory.create(SubjectAB.class);
        assertEquals(result.getClass(), MultiRootKeyedPath.class);

        final MultiRootKeyedPath<SubjectAB> multi = ((MultiRootKeyedPath) result);
        assertEquals(multi.getRootToPath().keySet().size(), 2);

        KeyedInstances.get().registerInstance(new SubjectAB());
        assertNotNull(KeyedInstances.get().getInstance("a.subjectab"));
        assertNotNull(KeyedInstances.get().getInstance("b.subjectab"));
        assertNotNull(KeyedInstances.get().getInstance(IB.class, "subjectab"));
        assertNotNull(KeyedInstances.get().getInstanceOfClass(SubjectAB.class));
    }

    @Test
    void testMultiParentABC() {
        final IKeyedPath<SubjectABC> result = KeyedPathFactory.create(SubjectABC.class);
        assertEquals(result.getClass(), MultiRootKeyedPath.class);

        final MultiRootKeyedPath<SubjectABC> multi = ((MultiRootKeyedPath) result);
        assertEquals(multi.getRootToPath().keySet().size(), 2);

        KeyedInstances.get().registerInstance(new SubjectABC());
        assertNotNull(KeyedInstances.get().getInstance("a.subjectabc"));
        assertNotNull(KeyedInstances.get().getInstance("b.c.subjectabc"));
        assertNotNull(KeyedInstances.get().getInstance(IB.class, "c.subjectabc"));
        assertNotNull(KeyedInstances.get().getInstance(CIB.class, "subjectabc"));
        assertNotNull(KeyedInstances.get().getInstanceOfClass(SubjectABC.class));
    }

    @Test
    void testGeneralInstances() {
        final SubjectAB subjectAB = new SubjectAB();
        final SubjectABC subjectABC = new SubjectABC();
        KeyedInstances.get().registerInstance(subjectAB);
        KeyedInstances.get().registerInstance(subjectABC);

        final List<? extends IA> instancesOfRoot = KeyedInstances.get().getInstancesOfRoot(IA.class);
        assertEquals(instancesOfRoot.size(), 2);
        assertEquals(instancesOfRoot.get(0).key(), "subjectab");
        assertEquals(instancesOfRoot.get(1).key(), "subjectabc");
    }

    @Keyed("a")
    interface IA extends KeyedInstance {
    }

    @Keyed("b")
    interface IB extends KeyedInstance {
    }

    @Keyed("c")
    interface CIB extends IB {}

    class SubjectAB implements IA, IB {
        @Override
        public @NonNull String key() {
            return "subjectab";
        }
    }

    class SubjectABC implements IA, CIB {
        @Override
        public @NonNull String key() {
            return "subjectabc";
        }
    }
}
