package thaumcraft.common.tiles.essentia;

import net.minecraft.core.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thaumcraft.api.aspects.Aspect;

import static org.junit.jupiter.api.Assertions.*;

public class CentrifugeBlockEntityTest {
    private CentrifugeLogic logic;
    private boolean changed;

    private static class DummyAspect extends Aspect {
        private String tag;
        private Aspect[] components;

        public DummyAspect(String tag, Aspect[] components) {
            super(tag, 0, components, null, 0);
            this.tag = tag;
            this.components = components;
        }

        @Override
        public String getTag() { return tag; }

        @Override
        public boolean isPrimal() { return components == null || components.length != 2; }

        @Override
        public Aspect[] getComponents() { return components; }
    }

    private Aspect p1;
    private Aspect p2;
    private Aspect compound;

    @BeforeEach
    public void setUp() {
        changed = false;
        logic = new CentrifugeLogic(() -> changed = true);

        p1 = new DummyAspect("test_primal1_" + System.nanoTime(), null);
        p2 = new DummyAspect("test_primal2_" + System.nanoTime(), null);
        compound = new DummyAspect("test_compound_" + System.nanoTime(), new Aspect[]{p1, p2});
    }

    @Test
    public void testAddEssentia() {
        // Test rejecting invalid inputs
        assertEquals(0, logic.addEssentia(p1, 1, Direction.DOWN)); // Primal rejected
        assertEquals(0, logic.addEssentia(compound, 1, Direction.UP)); // Wrong side

        // Test accepting valid input
        assertEquals(5, logic.addEssentia(compound, 5, Direction.DOWN));
        assertEquals(compound, logic.aspectIn);
        assertEquals(5, logic.amountIn);
        assertTrue(changed);

        // Test rejecting different aspect
        Aspect compound2 = new DummyAspect("test_compound2_" + System.nanoTime(), new Aspect[]{p1, p2});
        assertEquals(0, logic.addEssentia(compound2, 5, Direction.DOWN));

        // Test hitting max capacity
        assertEquals(11, logic.addEssentia(compound, 20, Direction.DOWN));
        assertEquals(16, logic.amountIn);
    }

    @Test
    public void testBreakdownProcess() {
        logic.addEssentia(compound, 1, Direction.DOWN);

        assertFalse(logic.working);

        // Tick up to just before completion
        for (int i = 0; i < logic.maxProcessTime - 1; i++) {
            logic.tick();
            assertTrue(logic.working);
            assertEquals(1, logic.amountIn);
            assertEquals(0, logic.amountOut1);
            assertEquals(0, logic.amountOut2);
        }

        // Final tick
        logic.tick();

        assertEquals(0, logic.amountIn);
        assertNull(logic.aspectIn);

        assertEquals(1, logic.amountOut1);
        assertEquals(p1, logic.aspectOut1);

        assertEquals(1, logic.amountOut2);
        assertEquals(p2, logic.aspectOut2);
    }

    @Test
    public void testOutputIsBlockedIfFull() {
        logic.addEssentia(compound, 2, Direction.DOWN);

        logic.amountOut1 = logic.maxOut;
        logic.aspectOut1 = p1;

        for (int i = 0; i < logic.maxProcessTime + 5; i++) {
            logic.tick();
        }

        // Should not have processed because output is full
        assertEquals(2, logic.amountIn);
        assertFalse(logic.working);
    }

    @Test
    public void testTakeEssentia() {
        logic.aspectOut1 = p1;
        logic.amountOut1 = 5;

        logic.aspectOut2 = p2;
        logic.amountOut2 = 3;

        // Try to take from wrong face
        assertEquals(0, logic.takeEssentia(p1, 5, Direction.DOWN));

        // Try to take aspect that isn't there
        Aspect p3 = new DummyAspect("test_primal3_" + System.nanoTime(), null);
        assertEquals(0, logic.takeEssentia(p3, 5, Direction.UP));

        // Take from output 1
        assertEquals(2, logic.takeEssentia(p1, 2, Direction.UP));
        assertEquals(3, logic.amountOut1);
        assertEquals(p1, logic.aspectOut1);
        assertTrue(changed);

        // Take remaining from output 1
        assertEquals(3, logic.takeEssentia(p1, 10, Direction.UP));
        assertEquals(0, logic.amountOut1);
        assertNull(logic.aspectOut1);

        // Take from output 2
        assertEquals(3, logic.takeEssentia(p2, 10, Direction.UP));
        assertEquals(0, logic.amountOut2);
        assertNull(logic.aspectOut2);
    }

    @Test
    public void testGetEssentiaTypeAndAmount() {
        logic.aspectOut1 = p1;
        logic.amountOut1 = 2;
        logic.aspectOut2 = p2;
        logic.amountOut2 = 3;

        assertEquals(p1, logic.getEssentiaType(Direction.UP));
        assertEquals(5, logic.getEssentiaAmount(Direction.UP));

        assertNull(logic.getEssentiaType(Direction.DOWN));
        assertEquals(0, logic.getEssentiaAmount(Direction.DOWN));
    }
}
