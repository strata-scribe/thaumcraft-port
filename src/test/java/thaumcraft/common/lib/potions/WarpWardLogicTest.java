package thaumcraft.common.lib.potions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WarpWardLogicTest {

    @Test
    public void testCalculateWarpEventMitigationPercentage() {
        // Test base amplifier (0) - should be 50%
        assertEquals(0.5, WarpWardLogic.calculateWarpEventMitigationPercentage(0), 0.001);

        // Test amplifier 1 - should be 75%
        assertEquals(0.75, WarpWardLogic.calculateWarpEventMitigationPercentage(1), 0.001);

        // Test amplifier 2 - should be 100% (capped)
        assertEquals(1.0, WarpWardLogic.calculateWarpEventMitigationPercentage(2), 0.001);

        // Test high amplifier - should stay capped at 100%
        assertEquals(1.0, WarpWardLogic.calculateWarpEventMitigationPercentage(5), 0.001);

        // Test negative amplifier - should handle gracefully
        assertEquals(0.0, WarpWardLogic.calculateWarpEventMitigationPercentage(-1), 0.001);
    }

    @Test
    public void testCalculateWarpWardDecay() {
        // Normal decay
        assertEquals(100, WarpWardLogic.calculateWarpWardDecay(200, 100));

        // No ticks elapsed
        assertEquals(200, WarpWardLogic.calculateWarpWardDecay(200, 0));

        // Ticks elapsed exactly equals initial duration
        assertEquals(0, WarpWardLogic.calculateWarpWardDecay(200, 200));

        // Ticks elapsed exceeds initial duration (should not drop below 0)
        assertEquals(0, WarpWardLogic.calculateWarpWardDecay(200, 300));

        // Initial duration is negative
        assertEquals(0, WarpWardLogic.calculateWarpWardDecay(-100, 50));

        // Ticks elapsed is negative
        assertEquals(200, WarpWardLogic.calculateWarpWardDecay(200, -50));
    }
}
