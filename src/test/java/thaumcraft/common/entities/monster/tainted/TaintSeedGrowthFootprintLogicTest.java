package thaumcraft.common.entities.monster.tainted;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaintSeedGrowthFootprintLogicTest {

    @Test
    public void testCalculateMaturityStage() {
        // Base progression
        assertEquals(0, TaintSeedGrowthFootprintLogic.calculateMaturityStage(0, 100));
        assertEquals(1, TaintSeedGrowthFootprintLogic.calculateMaturityStage(25, 100));
        assertEquals(2, TaintSeedGrowthFootprintLogic.calculateMaturityStage(50, 100));
        assertEquals(3, TaintSeedGrowthFootprintLogic.calculateMaturityStage(75, 100));
        assertEquals(4, TaintSeedGrowthFootprintLogic.calculateMaturityStage(100, 100));

        // Boundaries and overflow
        assertEquals(0, TaintSeedGrowthFootprintLogic.calculateMaturityStage(-10, 100));
        assertEquals(4, TaintSeedGrowthFootprintLogic.calculateMaturityStage(150, 100));

        // Edge cases for maxAge
        assertEquals(4, TaintSeedGrowthFootprintLogic.calculateMaturityStage(10, 0));
        assertEquals(4, TaintSeedGrowthFootprintLogic.calculateMaturityStage(10, -5));
    }

    @Test
    public void testCalculateFootprintRadius() {
        assertEquals(0.0, TaintSeedGrowthFootprintLogic.calculateFootprintRadius(0, 10.0), 0.001);
        assertEquals(2.5, TaintSeedGrowthFootprintLogic.calculateFootprintRadius(1, 10.0), 0.001);
        assertEquals(5.0, TaintSeedGrowthFootprintLogic.calculateFootprintRadius(2, 10.0), 0.001);
        assertEquals(7.5, TaintSeedGrowthFootprintLogic.calculateFootprintRadius(3, 10.0), 0.001);
        assertEquals(10.0, TaintSeedGrowthFootprintLogic.calculateFootprintRadius(4, 10.0), 0.001);

        // Boundaries and overflow
        assertEquals(0.0, TaintSeedGrowthFootprintLogic.calculateFootprintRadius(-2, 10.0), 0.001);
        assertEquals(10.0, TaintSeedGrowthFootprintLogic.calculateFootprintRadius(6, 10.0), 0.001);
    }

    @Test
    public void testIsBlockInFootprint() {
        // Center block
        assertTrue(TaintSeedGrowthFootprintLogic.isBlockInFootprint(0, 0, 0, 0, 5.0));

        // Inside radius
        assertTrue(TaintSeedGrowthFootprintLogic.isBlockInFootprint(0, 0, 3, 4, 6.0)); // distance 5

        // Exactly on boundary
        assertTrue(TaintSeedGrowthFootprintLogic.isBlockInFootprint(0, 0, 3, 4, 5.0)); // distance 5

        // Outside radius
        assertFalse(TaintSeedGrowthFootprintLogic.isBlockInFootprint(0, 0, 4, 4, 5.0)); // distance ~5.65

        // Negative radius edge case
        assertFalse(TaintSeedGrowthFootprintLogic.isBlockInFootprint(0, 0, 0, 0, -1.0));

        // Arbitrary positions
        assertTrue(TaintSeedGrowthFootprintLogic.isBlockInFootprint(10.5, -5.5, 12.0, -4.0, 3.0));
        assertFalse(TaintSeedGrowthFootprintLogic.isBlockInFootprint(10.5, -5.5, 15.0, 0.0, 3.0));
    }
}
