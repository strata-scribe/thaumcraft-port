package thaumcraft.common.lib.crafting;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InfusionDisplacementLogicTest {

    @Test
    public void testShouldKnockoffZeroInstability() {
        // At 0 or below, it should never knock off
        assertFalse(InfusionDisplacementLogic.shouldKnockoff(0, 0.0));
        assertFalse(InfusionDisplacementLogic.shouldKnockoff(0, 0.99));
        assertFalse(InfusionDisplacementLogic.shouldKnockoff(-5, 0.0));
    }

    @Test
    public void testShouldKnockoffProbabilityScaling() {
        // Base probability is math.min(0.75, instability * 0.02)
        // For instability 5, probability = 0.1
        assertTrue(InfusionDisplacementLogic.shouldKnockoff(5, 0.05)); // 0.05 < 0.1
        assertFalse(InfusionDisplacementLogic.shouldKnockoff(5, 0.15)); // 0.15 > 0.1

        // For instability 25, probability = 0.5
        assertTrue(InfusionDisplacementLogic.shouldKnockoff(25, 0.49));
        assertFalse(InfusionDisplacementLogic.shouldKnockoff(25, 0.51));

        // For instability 100, probability caps at 0.75
        assertTrue(InfusionDisplacementLogic.shouldKnockoff(100, 0.74));
        assertFalse(InfusionDisplacementLogic.shouldKnockoff(100, 0.76));
    }

    @Test
    public void testGenerateLaunchTrajectoryDirection() {
        // dx = 5, dz = 0 -> pedestal is +x from matrix
        // The item should fly further in +x
        double[] vel1 = InfusionDisplacementLogic.generateLaunchTrajectory(5, 0, 0, 0, 0);
        assertTrue(vel1[0] > 0); // vx > 0
        assertEquals(0.2, vel1[1], 0.01); // vy = 0.2 (upward kick without random)
        assertEquals(0, vel1[2], 0.01); // vz = 0

        // dx = -5, dz = -5 -> pedestal is -x, -z
        double[] vel2 = InfusionDisplacementLogic.generateLaunchTrajectory(-5, -5, 0, 0, 0);
        assertTrue(vel2[0] < 0); // vx < 0
        assertEquals(0.2, vel2[1], 0.01); // vy = 0.2
        assertTrue(vel2[2] < 0); // vz < 0
    }

    @Test
    public void testGenerateLaunchTrajectoryRandomness() {
        // Test with random variance added
        double[] vel = InfusionDisplacementLogic.generateLaunchTrajectory(0, 5, 0.5, 0.5, -0.5);

        // vx = dirX(0) * baseVel + randX(0.5) * 0.1 = 0.05
        assertEquals(0.05, vel[0], 0.01);

        // vy = 0.2 + randY(0.5) * 0.2 = 0.3
        assertEquals(0.3, vel[1], 0.01);

        // vz = dirZ(1) * baseVel(0.2) + randZ(-0.5) * 0.1 = 0.2 - 0.05 = 0.15
        assertEquals(0.15, vel[2], 0.01);
    }
}
