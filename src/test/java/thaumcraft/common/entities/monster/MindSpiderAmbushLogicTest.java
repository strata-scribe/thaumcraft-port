package thaumcraft.common.entities.monster;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MindSpiderAmbushLogicTest {

    @Test
    @DisplayName("Detection radius should be base radius when not sneaking or invisible")
    public void testDetectionRadiusNormal() {
        assertEquals(10.0, MindSpiderAmbushLogic.calculateStealthDetectionRadius(10.0, false, false), 0.001);
    }

    @Test
    @DisplayName("Detection radius should be reduced when sneaking")
    public void testDetectionRadiusSneaking() {
        assertEquals(8.0, MindSpiderAmbushLogic.calculateStealthDetectionRadius(10.0, true, false), 0.001);
    }

    @Test
    @DisplayName("Detection radius should be reduced when invisible")
    public void testDetectionRadiusInvisible() {
        assertEquals(5.0, MindSpiderAmbushLogic.calculateStealthDetectionRadius(10.0, false, true), 0.001);
    }

    @Test
    @DisplayName("Detection radius should be reduced further when both sneaking and invisible")
    public void testDetectionRadiusSneakingAndInvisible() {
        assertEquals(4.0, MindSpiderAmbushLogic.calculateStealthDetectionRadius(10.0, true, true), 0.001);
    }

    @Test
    @DisplayName("Leap trajectory should compute correctly for standard leap")
    public void testLeapTrajectoryNormal() {
        double[] trajectory = MindSpiderAmbushLogic.calculateAmbushLeapTrajectory(0, 0, 0, 10, 0, 0, 1.0);
        assertEquals(1.0, trajectory[0], 0.001); // vx
        assertEquals(0.5, trajectory[1], 0.001); // vy (1.0 * 0.5)
        assertEquals(0.0, trajectory[2], 0.001); // vz
    }

    @Test
    @DisplayName("Leap trajectory should compute correctly when target is directly above/below (same X/Z)")
    public void testLeapTrajectoryVerticalOnly() {
        double[] trajectory = MindSpiderAmbushLogic.calculateAmbushLeapTrajectory(0, 0, 0, 0, 10, 0, 1.0);
        assertEquals(0.0, trajectory[0], 0.001); // vx
        assertEquals(0.5, trajectory[1], 0.001); // vy
        assertEquals(0.0, trajectory[2], 0.001); // vz
    }

    @Test
    @DisplayName("Leap trajectory should have extra upward velocity when jumping up")
    public void testLeapTrajectoryUpward() {
        double[] trajectory = MindSpiderAmbushLogic.calculateAmbushLeapTrajectory(0, 0, 0, 10, 5, 0, 1.0);
        assertEquals(1.0, trajectory[0], 0.001); // vx
        assertEquals(1.0, trajectory[1], 0.001); // vy (0.5 + 5 * 0.1)
        assertEquals(0.0, trajectory[2], 0.001); // vz
    }

    @Test
    @DisplayName("Leap trajectory should not add extra upward velocity when jumping down")
    public void testLeapTrajectoryDownward() {
        double[] trajectory = MindSpiderAmbushLogic.calculateAmbushLeapTrajectory(0, 5, 0, 10, 0, 0, 1.0);
        assertEquals(1.0, trajectory[0], 0.001); // vx
        assertEquals(0.5, trajectory[1], 0.001); // vy (just base vy, dy is -5)
        assertEquals(0.0, trajectory[2], 0.001); // vz
    }

    @Test
    @DisplayName("Leap trajectory components should correctly split along X and Z")
    public void testLeapTrajectoryDiagonal() {
        double[] trajectory = MindSpiderAmbushLogic.calculateAmbushLeapTrajectory(0, 0, 0, 10, 0, 10, 1.0);
        double expectedVx = 1.0 * (10.0 / Math.sqrt(200));
        double expectedVz = 1.0 * (10.0 / Math.sqrt(200));
        assertEquals(expectedVx, trajectory[0], 0.001); // vx
        assertEquals(0.5, trajectory[1], 0.001); // vy
        assertEquals(expectedVz, trajectory[2], 0.001); // vz
    }
}
