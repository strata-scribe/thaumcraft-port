package thaumcraft.common.entities.monster;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EldritchCrabSpitLogicTest {

    @Test
    public void testTrajectoryCalculation() {
        // Shooting along the X axis
        EldritchCrabSpitLogic.SpitVector vec = EldritchCrabSpitLogic.calculateTrajectory(
            0, 0, 0,
            10, 0, 0,
            1.0
        );

        // The distance is 10. Horizontal distance is 10.
        // dy = 0 + 10 * 0.2 = 2.
        // total distance = sqrt(100 + 4) = sqrt(104) = 10.198
        // dx = 10 / 10.198 * 1.0 = 0.9805
        // dy = 2 / 10.198 * 1.0 = 0.196

        assertTrue(vec.x > 0.9 && vec.x < 1.0, "X velocity should be positive and close to 1.0");
        assertTrue(vec.y > 0.1 && vec.y < 0.3, "Y velocity should have a slight upward arc");
        assertEquals(0.0, vec.z, 0.0001, "Z velocity should be 0");
    }

    @Test
    public void testTrajectoryCalculation_SamePosition() {
        EldritchCrabSpitLogic.SpitVector vec = EldritchCrabSpitLogic.calculateTrajectory(
            0, 0, 0,
            0, 0, 0,
            2.0
        );

        assertEquals(0.0, vec.x, 0.0001, "X velocity should be 0");
        assertEquals(2.0, vec.y, 0.0001, "Y velocity should be equal to initial velocity");
        assertEquals(0.0, vec.z, 0.0001, "Z velocity should be 0");
    }

    @Test
    public void testImpactVelocityCalculation() {
        double initialVx = 10.0;
        double initialVy = 5.0;
        double initialVz = 0.0;
        int ticksInAir = 10;
        double dragMultiplier = 0.9;
        double gravity = 0.5;

        EldritchCrabSpitLogic.SpitVector result = EldritchCrabSpitLogic.calculateImpactVelocity(
            initialVx, initialVy, initialVz, ticksInAir, dragMultiplier, gravity
        );

        // Expected X velocity: 10.0 * (0.9^10) = 10.0 * 0.34867 = 3.4867
        assertEquals(10.0 * Math.pow(0.9, 10), result.x, 0.001);

        // Expected Y velocity will be calculated based on drag and gravity per tick
        assertTrue(result.y < 0, "Y velocity should be negative due to gravity");

        assertEquals(0.0, result.z, 0.0001);
    }

    @Test
    public void testArmorBypassDamage() {
        EldritchCrabSpitLogic.DamageResult result = EldritchCrabSpitLogic.calculateArmorBypassDamage(10.0, 5.0, 0.4);

        assertEquals(6.0, result.standardDamage, 0.0001, "Standard damage should be 60% of 10");
        assertEquals(4.0, result.bypassDamage, 0.0001, "Bypass damage should be 40% of 10");

        // Test clamping
        EldritchCrabSpitLogic.DamageResult resultClampedHigh = EldritchCrabSpitLogic.calculateArmorBypassDamage(10.0, 5.0, 1.5);
        assertEquals(0.0, resultClampedHigh.standardDamage, 0.0001);
        assertEquals(10.0, resultClampedHigh.bypassDamage, 0.0001);

        EldritchCrabSpitLogic.DamageResult resultClampedLow = EldritchCrabSpitLogic.calculateArmorBypassDamage(10.0, 5.0, -0.5);
        assertEquals(10.0, resultClampedLow.standardDamage, 0.0001);
        assertEquals(0.0, resultClampedLow.bypassDamage, 0.0001);
    }
}
