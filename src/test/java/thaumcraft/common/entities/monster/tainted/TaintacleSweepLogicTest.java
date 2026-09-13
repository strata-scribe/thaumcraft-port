package thaumcraft.common.entities.monster.tainted;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaintacleSweepLogicTest {

    @Test
    public void testIsWithinSweepReach() {
        // Target well within reach
        assertTrue(TaintacleSweepLogic.isWithinSweepReach(2.0, 1.0, 2.0, 5.0, 2.0));

        // Target exactly at horizontal boundary
        assertTrue(TaintacleSweepLogic.isWithinSweepReach(3.0, 0.0, 4.0, 5.0, 2.0)); // 3^2 + 4^2 = 25

        // Target exactly at vertical boundary
        assertTrue(TaintacleSweepLogic.isWithinSweepReach(2.0, 2.0, 2.0, 5.0, 2.0));
        assertTrue(TaintacleSweepLogic.isWithinSweepReach(2.0, -2.0, 2.0, 5.0, 2.0));

        // Target out of horizontal reach
        assertFalse(TaintacleSweepLogic.isWithinSweepReach(4.0, 0.0, 4.0, 5.0, 2.0)); // 16 + 16 = 32 > 25

        // Target out of vertical reach
        assertFalse(TaintacleSweepLogic.isWithinSweepReach(2.0, 2.1, 2.0, 5.0, 2.0));
    }

    @Test
    public void testIsWithinHorizontalCollisionArc() {
        // Basic arc test (target at 45 degrees)
        assertTrue(TaintacleSweepLogic.isWithinHorizontalCollisionArc(1.0, 1.0, 45.0, 90.0));

        // Target outside arc
        assertFalse(TaintacleSweepLogic.isWithinHorizontalCollisionArc(1.0, 1.0, 180.0, 90.0));

        // Target exactly at boundary (within half arc width)
        assertTrue(TaintacleSweepLogic.isWithinHorizontalCollisionArc(1.0, 1.0, 90.0, 90.0));

        // Target exactly at origin
        assertTrue(TaintacleSweepLogic.isWithinHorizontalCollisionArc(0.0, 0.0, 0.0, 90.0));

        // Edge case: Angle wrap around (350 deg vs 10 deg, diff is 20 deg)
        assertTrue(TaintacleSweepLogic.isWithinHorizontalCollisionArc(Math.cos(Math.toRadians(10)), Math.sin(Math.toRadians(10)), 350.0, 90.0));

        // Negative angles handling
        assertTrue(TaintacleSweepLogic.isWithinHorizontalCollisionArc(0.0, -1.0, -90.0, 90.0));
    }

    @Test
    public void testCalculateSweepKnockback() {
        // dx=3, dz=4 => dist=5. dx/dist = 0.6, dz/dist = 0.8
        double[] knockback = TaintacleSweepLogic.calculateSweepKnockback(3.0, 4.0, 2.0);
        assertEquals(1.2, knockback[0], 1e-4);
        assertEquals(1.0, knockback[1], 1e-4);
        assertEquals(1.6, knockback[2], 1e-4);

        // Zero distance case
        double[] knockbackZero = TaintacleSweepLogic.calculateSweepKnockback(0.0, 0.0, 2.0);
        assertEquals(0.0, knockbackZero[0], 1e-4);
        assertEquals(1.0, knockbackZero[1], 1e-4);
        assertEquals(0.0, knockbackZero[2], 1e-4);
    }
}
