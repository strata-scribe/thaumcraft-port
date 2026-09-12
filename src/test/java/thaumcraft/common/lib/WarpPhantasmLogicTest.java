package thaumcraft.common.lib;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WarpPhantasmLogicTest {

    @Test
    public void testCalculateMobCount_ZeroWarp() {
        assertEquals(0, WarpPhantasmLogic.calculateMobCount(0, 0.5));
        assertEquals(0, WarpPhantasmLogic.calculateMobCount(-10, 0.5));
    }

    @Test
    public void testCalculateMobCount_Warp15() {
        // totalWarp = 15. baseCount = 1 + 15/30 = 1. extraChance = 15/30.0 = 0.5.
        // randomValue = 0.4 < 0.5 (extraChance), so add 1. Result = 2.
        assertEquals(2, WarpPhantasmLogic.calculateMobCount(15, 0.4));

        // randomValue = 0.6 >= 0.5 (extraChance), so don't add 1. Result = 1.
        assertEquals(1, WarpPhantasmLogic.calculateMobCount(15, 0.6));
    }

    @Test
    public void testCalculateMobCount_Warp30() {
        // totalWarp = 30. baseCount = 1 + 30/30 = 2. extraChance = 0/30.0 = 0.0.
        // randomValue = 0.5 >= 0.0, so don't add 1. Result = 2.
        assertEquals(2, WarpPhantasmLogic.calculateMobCount(30, 0.5));
        assertEquals(2, WarpPhantasmLogic.calculateMobCount(30, 0.0)); // even at 0.0, 0.0 is not < 0.0
    }

    @Test
    public void testCalculateMobCount_Warp45() {
        // totalWarp = 45. baseCount = 1 + 45/30 = 2. extraChance = 15/30.0 = 0.5.
        assertEquals(3, WarpPhantasmLogic.calculateMobCount(45, 0.4));
        assertEquals(2, WarpPhantasmLogic.calculateMobCount(45, 0.6));
    }

    @Test
    public void testCalculateMobCount_Warp100() {
        // totalWarp = 100. baseCount = 1 + 100/30 = 4. extraChance = 10/30.0 = 0.333...
        assertEquals(5, WarpPhantasmLogic.calculateMobCount(100, 0.3));
        assertEquals(4, WarpPhantasmLogic.calculateMobCount(100, 0.4));
    }

    @Test
    public void testCalculateDespawnTimeoutTicks_ZeroWarp() {
        assertEquals(1200, WarpPhantasmLogic.calculateDespawnTimeoutTicks(0));
        assertEquals(1200, WarpPhantasmLogic.calculateDespawnTimeoutTicks(-10));
    }

    @Test
    public void testCalculateDespawnTimeoutTicks_Warp50() {
        // totalWarp = 50. timeout = 1200 + (50 * 12) = 1200 + 600 = 1800
        assertEquals(1800, WarpPhantasmLogic.calculateDespawnTimeoutTicks(50));
    }

    @Test
    public void testCalculateDespawnTimeoutTicks_Warp100() {
        // totalWarp = 100. timeout = 1200 + (100 * 12) = 1200 + 1200 = 2400
        assertEquals(2400, WarpPhantasmLogic.calculateDespawnTimeoutTicks(100));
    }

    @Test
    public void testCalculateDespawnTimeoutTicks_Warp150() {
        // totalWarp = 150. timeout = 1200 + (150 * 12) = 1200 + 1800 = 3000. Cap = 2400.
        assertEquals(2400, WarpPhantasmLogic.calculateDespawnTimeoutTicks(150));
    }
}
