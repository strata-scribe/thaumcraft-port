package thaumcraft.common.tiles.devices.logic;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class BoreMiningConeLogicTest {

    @Test
    public void testCalculateExcavationDepth() {
        assertEquals(64, BoreMiningConeLogic.calculateExcavationDepth(64, 0));
        assertEquals(80, BoreMiningConeLogic.calculateExcavationDepth(64, 16));
        assertEquals(96, BoreMiningConeLogic.calculateExcavationDepth(64, 32));
    }

    @Test
    public void testCalculateEnergyConsumption() {
        // hardness 1.5, multiplier 1.0 -> 1.5 * 10 / 1.0 = 15
        assertEquals(15, BoreMiningConeLogic.calculateEnergyConsumption(1.5f, 1.0));
        // hardness 50.0, multiplier 2.0 -> 50.0 * 10 / 2.0 = 250
        assertEquals(250, BoreMiningConeLogic.calculateEnergyConsumption(50.0f, 2.0));
        // hardness 0.05, multiplier 1.0 -> 0.05 * 10 / 1.0 = 0 -> min 1
        assertEquals(1, BoreMiningConeLogic.calculateEnergyConsumption(0.05f, 1.0));
    }

    @Test
    public void testCalculateMiningCone_OriginIncludes() {
        List<int[]> cone = BoreMiningConeLogic.calculateMiningCone(0, 0, 0, 0, 0, 10, 5);
        boolean hasOrigin = false;
        for (int[] pos : cone) {
            if (pos[0] == 0 && pos[1] == 0 && pos[2] == 0) {
                hasOrigin = true;
                break;
            }
        }
        assertTrue(hasOrigin, "Cone should include the origin");
    }

    @Test
    public void testCalculateMiningCone_ZDirection() {
        // yaw 0, pitch 0 -> +Z direction
        // dx = 0, dy = 0, dz = 1
        List<int[]> cone = BoreMiningConeLogic.calculateMiningCone(0, 0, 0, 0, 0, 10, 5);

        // Point (0, 0, 10) is on the ray at depth 10. Radius should be 5.
        // Point (5, 0, 10) is exactly at max radius.
        assertTrue(listContains(cone, 0, 0, 10));
        assertTrue(listContains(cone, 5, 0, 10));
        assertTrue(listContains(cone, -5, 0, 10));
        assertTrue(listContains(cone, 0, 5, 10));
        assertTrue(listContains(cone, 0, -5, 10));

        // Point (6, 0, 10) is outside the max radius.
        assertFalse(listContains(cone, 6, 0, 10));

        // Point (0, 0, 5) is on the ray at depth 5. Radius should be 5 * (5/10) = 2.5
        // (2, 0, 5) distance sq 4 <= 6.25 -> inside
        assertTrue(listContains(cone, 2, 0, 5));
        // (3, 0, 5) distance sq 9 > 6.25 -> outside
        assertFalse(listContains(cone, 3, 0, 5));
    }

    @Test
    public void testCalculateMiningCone_XDirection() {
        // yaw 270, pitch 0 -> +X direction
        List<int[]> cone = BoreMiningConeLogic.calculateMiningCone(0, 0, 0, 270, 0, 10, 5);

        // Point (10, 0, 0) is on the ray at depth 10.
        assertTrue(listContains(cone, 10, 0, 0));
        assertTrue(listContains(cone, 10, 5, 0));
        assertFalse(listContains(cone, 10, 6, 0));

        // Point (5, 0, 0) is on the ray at depth 5. Radius 2.5.
        assertTrue(listContains(cone, 5, 2, 0));
        assertFalse(listContains(cone, 5, 3, 0));
    }

    @Test
    public void testCalculateMiningCone_YDirection() {
        // yaw 0, pitch -90 -> +Y direction
        List<int[]> cone = BoreMiningConeLogic.calculateMiningCone(0, 0, 0, 0, -90, 10, 5);

        assertTrue(listContains(cone, 0, 10, 0));
        assertTrue(listContains(cone, 5, 10, 0));
        assertFalse(listContains(cone, 6, 10, 0));

        assertTrue(listContains(cone, 2, 5, 0));
        assertFalse(listContains(cone, 3, 5, 0));
    }

    private boolean listContains(List<int[]> list, int x, int y, int z) {
        for (int[] pos : list) {
            if (pos[0] == x && pos[1] == y && pos[2] == z) {
                return true;
            }
        }
        return false;
    }
}
