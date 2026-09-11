package thaumcraft.common.tiles.essentia;

import net.minecraft.core.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thaumcraft.api.aspects.Aspect;

import static org.junit.jupiter.api.Assertions.*;

class CentrifugeLogicTest {

    private CentrifugeLogic logic;
    private boolean isDirty = false;

    @BeforeEach
    void setUp() {
        logic = new CentrifugeLogic(() -> isDirty = true);
        isDirty = false;
    }

    @Test
    void testInputOutputFaces() {
        assertTrue(logic.canInputFrom(Direction.DOWN));
        assertFalse(logic.canInputFrom(Direction.UP));
        assertTrue(logic.canOutputTo(Direction.UP));
        assertFalse(logic.canOutputTo(Direction.DOWN));
    }

    @Test
    void testAddCompoundEssentia() {
        int added = logic.addEssentia(Aspect.LIFE, 5, Direction.DOWN); // LIFE (Victus) is compound
        assertEquals(5, added);
        assertEquals(Aspect.LIFE, logic.aspectIn);
        assertEquals(5, logic.amountIn);
        assertTrue(isDirty);
    }

    @Test
    void testAddPrimalEssentiaRejected() {
        int added = logic.addEssentia(Aspect.EARTH, 5, Direction.DOWN); // EARTH is primal
        assertEquals(0, added);
        assertNull(logic.aspectIn);
        assertEquals(0, logic.amountIn);
        assertFalse(isDirty);
    }

    @Test
    void testCentrifugeProcessing() {
        logic.addEssentia(Aspect.LIFE, 1, Direction.DOWN);

        // Advance ticks
        for (int i = 0; i < logic.maxProcessTime - 1; i++) {
            logic.tick();
            assertEquals(1, logic.amountIn); // not processed yet
        }

        // Final tick
        logic.tick();

        assertEquals(0, logic.amountIn);
        assertNull(logic.aspectIn);

        // LIFE (Victus) -> EARTH (Terra) + WATER (Aqua)
        assertEquals(Aspect.EARTH, logic.aspectOut1);
        assertEquals(1, logic.amountOut1);
        assertEquals(Aspect.WATER, logic.aspectOut2);
        assertEquals(1, logic.amountOut2);
    }

    @Test
    void testBufferLimits() {
        logic.addEssentia(Aspect.LIFE, logic.maxIn + 5, Direction.DOWN);
        assertEquals(logic.maxIn, logic.amountIn);

        // Simulate output buffer getting full
        logic.amountOut1 = logic.maxOut;
        logic.aspectOut1 = Aspect.EARTH;
        logic.amountOut2 = logic.maxOut;
        logic.aspectOut2 = Aspect.WATER;

        logic.tick();
        // Should not process if output is full
        assertEquals(logic.maxIn, logic.amountIn);
    }

    @Test
    void testTakeEssentia() {
        logic.aspectOut1 = Aspect.EARTH;
        logic.amountOut1 = 5;

        int taken = logic.takeEssentia(Aspect.EARTH, 3, Direction.UP);
        assertEquals(3, taken);
        assertEquals(2, logic.amountOut1);
        assertEquals(Aspect.EARTH, logic.aspectOut1);

        taken = logic.takeEssentia(Aspect.EARTH, 5, Direction.UP);
        assertEquals(2, taken);
        assertEquals(0, logic.amountOut1);
        assertNull(logic.aspectOut1);
    }
}
