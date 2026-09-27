package thaumcraft.common.tiles.essentia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.essentia.logic.AlembicLogic;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Alembic logic decoupled from AlembicBlockEntity to avoid Forge dependency errors.
 */
class AlembicBlockEntityTest {

    private AlembicLogic logic;

    @BeforeEach
    void setUp() {
        logic = new AlembicLogic();
    }

    @Test
    void testCollectsUpTo32Essentia() {
        int remainder = logic.addToContainer(Aspect.FIRE, 10);
        assertEquals(0, remainder);
        assertEquals(Aspect.FIRE, logic.getAspect());
        assertEquals(10, logic.getAmount());

        remainder = logic.addToContainer(Aspect.FIRE, 30);
        assertEquals(8, remainder); // 40 total, 32 cap -> 8 remainder
        assertEquals(32, logic.getAmount());

        remainder = logic.addToContainer(Aspect.FIRE, 5);
        assertEquals(5, remainder);
        assertEquals(32, logic.getAmount());
    }

    @Test
    void testSingleAspectTypeEnforced() {
        logic.addToContainer(Aspect.WATER, 10);
        int remainder = logic.addToContainer(Aspect.EARTH, 10);
        assertEquals(10, remainder); // Rejected
        assertEquals(Aspect.WATER, logic.getAspect());
        assertEquals(10, logic.getAmount());
    }

    @Test
    void testExtractionViaPhials() {
        logic.addToContainer(Aspect.AIR, 20);

        // Phial takes exactly 8
        boolean success = logic.takeFromContainer(Aspect.AIR, 8);
        assertTrue(success);
        assertEquals(12, logic.getAmount());

        // Phial takes exactly 8
        success = logic.takeFromContainer(Aspect.AIR, 8);
        assertTrue(success);
        assertEquals(4, logic.getAmount());

        // Phial cannot take 8 if less than 8 available
        success = logic.takeFromContainer(Aspect.AIR, 8);
        assertFalse(success);
        assertEquals(4, logic.getAmount());
    }

    @Test
    void testSuctionFromEssentiaTubes() {
        logic.addToContainer(Aspect.ORDER, 20);

        // Tube extracts 10
        boolean success = logic.takeFromContainer(Aspect.ORDER, 10);
        assertTrue(success);
        assertEquals(10, logic.getAmount());

        // Cannot extract wrong aspect
        success = logic.takeFromContainer(Aspect.ENTROPY, 5);
        assertFalse(success);
        assertEquals(10, logic.getAmount());
    }

    @Test
    void testLabelFilter() {
        logic.setAspectFilter(Aspect.MAGIC);

        int remainder = logic.addToContainer(Aspect.FIRE, 10);
        assertEquals(10, remainder);
        assertEquals(0, logic.getAmount());
        assertNull(logic.getAspect());

        remainder = logic.addToContainer(Aspect.MAGIC, 10);
        assertEquals(0, remainder);
        assertEquals(10, logic.getAmount());
        assertEquals(Aspect.MAGIC, logic.getAspect());
    }
}
