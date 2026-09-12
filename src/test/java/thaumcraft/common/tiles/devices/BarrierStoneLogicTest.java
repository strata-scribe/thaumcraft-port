package thaumcraft.common.tiles.devices;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BarrierStoneLogicTest {

    @Test
    void testIsInsideExclusionZone_Inside() {
        // Entity is 3 units away on X axis, radius is 5
        assertTrue(BarrierStoneLogic.isInsideExclusionZone(0, 0, 0, 3, 0, 0, 5));
    }

    @Test
    void testIsInsideExclusionZone_OnBoundary() {
        // Entity is exactly 5 units away on X axis, radius is 5
        assertTrue(BarrierStoneLogic.isInsideExclusionZone(0, 0, 0, 5, 0, 0, 5));
    }

    @Test
    void testIsInsideExclusionZone_Outside() {
        // Entity is 6 units away on X axis, radius is 5
        assertFalse(BarrierStoneLogic.isInsideExclusionZone(0, 0, 0, 6, 0, 0, 5));
    }

    @Test
    void testCalculateRepulsionVector_Inside() {
        // Entity is at (3, 0, 0), stone at (0, 0, 0), radius is 5, strength 2
        double[] vector = BarrierStoneLogic.calculateRepulsionVector(0, 0, 0, 3, 0, 0, 5, 2.0);
        assertEquals(2.0, vector[0], 0.001);
        assertEquals(0.0, vector[1], 0.001);
        assertEquals(0.0, vector[2], 0.001);
    }

    @Test
    void testCalculateRepulsionVector_Outside() {
        // Entity is at (6, 0, 0), stone at (0, 0, 0), radius is 5, strength 2
        double[] vector = BarrierStoneLogic.calculateRepulsionVector(0, 0, 0, 6, 0, 0, 5, 2.0);
        assertEquals(0.0, vector[0], 0.001);
        assertEquals(0.0, vector[1], 0.001);
        assertEquals(0.0, vector[2], 0.001);
    }

    @Test
    void testCalculateRepulsionVector_ExactCenter() {
        // Entity is at (0, 0, 0), stone at (0, 0, 0), radius is 5, strength 2
        double[] vector = BarrierStoneLogic.calculateRepulsionVector(0, 0, 0, 0, 0, 0, 5, 2.0);
        // Expect push out in +X direction based on implementation
        assertEquals(2.0, vector[0], 0.001);
        assertEquals(0.0, vector[1], 0.001);
        assertEquals(0.0, vector[2], 0.001);
    }

    @Test
    void testCalculateRepulsionVector_Diagonal() {
        // Entity is at (3, 4, 0), stone at (0, 0, 0), distance is 5, radius is 10, strength 2
        double[] vector = BarrierStoneLogic.calculateRepulsionVector(0, 0, 0, 3, 4, 0, 10, 2.0);
        // Normalized vector is (3/5, 4/5, 0) = (0.6, 0.8, 0)
        // Multiplied by strength 2.0 -> (1.2, 1.6, 0)
        assertEquals(1.2, vector[0], 0.001);
        assertEquals(1.6, vector[1], 0.001);
        assertEquals(0.0, vector[2], 0.001);
    }
}
