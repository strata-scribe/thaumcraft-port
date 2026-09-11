package thaumcraft.common.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EldritchMobLogicTest {

    @Test
    @DisplayName("calculateCrabLeapTrajectory computes correct normalized vector")
    public void testCalculateCrabLeapTrajectory() {
        double dx = 3.0;
        double dy = 0.0;
        double dz = 4.0;
        double distance = 5.0; // Math.sqrt(3^2 + 4^2)
        double leapVelocity = 1.0;

        double[] trajectory = EldritchMobLogic.calculateCrabLeapTrajectory(dx, dy, dz, distance, leapVelocity);

        assertEquals(0.6, trajectory[0], 0.001); // (3/5) * 1
        assertEquals(0.5, trajectory[1], 0.001); // 1 * 0.5
        assertEquals(0.8, trajectory[2], 0.001); // (4/5) * 1

        // Zero distance
        double[] trajZero = EldritchMobLogic.calculateCrabLeapTrajectory(0, 0, 0, 0, 1.0);
        assertArrayEquals(new double[]{0, 0, 0}, trajZero, 0.001);
    }

    @Test
    @DisplayName("canPossess accurately checks logic")
    public void testCanPossess() {
        assertTrue(EldritchMobLogic.canPossess(true, false, 1.5, 2.0)); // Valid case
        assertFalse(EldritchMobLogic.canPossess(false, false, 1.5, 2.0)); // Not humanoid
        assertFalse(EldritchMobLogic.canPossess(true, true, 1.5, 2.0)); // Already possessed
        assertFalse(EldritchMobLogic.canPossess(true, false, 2.5, 2.0)); // Out of range
    }

    @Test
    @DisplayName("calculateAugmentedArmor boosts armor correctly")
    public void testCalculateAugmentedArmor() {
        assertEquals(8.0, EldritchMobLogic.calculateAugmentedArmor(2.0), 0.001);
        assertEquals(6.0, EldritchMobLogic.calculateAugmentedArmor(0.0), 0.001);
    }

    @Test
    @DisplayName("calculateAugmentedSpeed boosts speed correctly")
    public void testCalculateAugmentedSpeed() {
        assertEquals(0.3, EldritchMobLogic.calculateAugmentedSpeed(0.2), 0.001);
        assertEquals(0.0, EldritchMobLogic.calculateAugmentedSpeed(0.0), 0.001);
    }
}
