package thaumcraft.common.items.curios;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ItemCloudRingTest {

    @Test
    public void testSlowFallSpeedConstant() {
        assertEquals(-0.05F, CloudRingLogic.SLOW_FALL_SPEED_Y, 1e-6);
    }

    @Test
    public void testShouldApplySlowFallTrueWhenSneakingInAirWithRing() {
        assertTrue(CloudRingLogic.shouldApplySlowFall(true, false, true),
                "Slow fall should apply when sneaking, in air, and wearing cloud ring");
    }

    @Test
    public void testShouldApplySlowFallFalseWhenNotSneaking() {
        assertFalse(CloudRingLogic.shouldApplySlowFall(false, false, true),
                "Slow fall should not apply when not sneaking");
    }

    @Test
    public void testShouldApplySlowFallFalseWhenOnGround() {
        assertFalse(CloudRingLogic.shouldApplySlowFall(true, true, true),
                "Slow fall should not apply when on ground");
    }

    @Test
    public void testShouldApplySlowFallFalseWhenNoCloudRing() {
        assertFalse(CloudRingLogic.shouldApplySlowFall(true, false, false),
                "Slow fall should not apply when player does not have cloud ring");
    }
}
