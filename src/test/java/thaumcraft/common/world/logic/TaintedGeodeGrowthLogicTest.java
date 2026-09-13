package thaumcraft.common.world.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaintedGeodeGrowthLogicTest {

    @Test
    public void testCalculateGrowthTicks() {
        // Test standard flux
        assertEquals(6000, TaintedGeodeGrowthLogic.calculateGrowthTicks(50.0));

        // Test low flux (slower growth)
        assertEquals(12000, TaintedGeodeGrowthLogic.calculateGrowthTicks(25.0));

        // Test very low flux (capped at max ticks)
        assertEquals(24000, TaintedGeodeGrowthLogic.calculateGrowthTicks(10.0));
        assertEquals(24000, TaintedGeodeGrowthLogic.calculateGrowthTicks(1.0));

        // Test high flux (faster growth)
        assertEquals(3000, TaintedGeodeGrowthLogic.calculateGrowthTicks(100.0));

        // Test very high flux (capped at min ticks)
        assertEquals(1200, TaintedGeodeGrowthLogic.calculateGrowthTicks(300.0));
        assertEquals(1200, TaintedGeodeGrowthLogic.calculateGrowthTicks(1000.0));

        // Test zero and negative flux (stalled)
        assertEquals(-1, TaintedGeodeGrowthLogic.calculateGrowthTicks(0.0));
        assertEquals(-1, TaintedGeodeGrowthLogic.calculateGrowthTicks(-10.0));
    }

    @Test
    public void testCanSpawnNewCluster() {
        // Not enough flux
        assertFalse(TaintedGeodeGrowthLogic.canSpawnNewCluster(9.9, 0, 10));
        assertFalse(TaintedGeodeGrowthLogic.canSpawnNewCluster(0.0, 0, 10));

        // Enough flux, not at max clusters
        assertTrue(TaintedGeodeGrowthLogic.canSpawnNewCluster(10.0, 5, 10));
        assertTrue(TaintedGeodeGrowthLogic.canSpawnNewCluster(50.0, 0, 10));

        // Enough flux, but at or above max clusters
        assertFalse(TaintedGeodeGrowthLogic.canSpawnNewCluster(50.0, 10, 10));
        assertFalse(TaintedGeodeGrowthLogic.canSpawnNewCluster(50.0, 15, 10));
    }
}
