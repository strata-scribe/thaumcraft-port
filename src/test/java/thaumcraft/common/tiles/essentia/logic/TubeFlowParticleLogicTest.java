package thaumcraft.common.tiles.essentia.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TubeFlowParticleLogicTest {

    @Test
    public void testComputeSpeedScaling() {
        assertEquals(0.0, TubeFlowParticleLogic.computeSpeedScaling(0), 0.0001);
        assertEquals(0.0, TubeFlowParticleLogic.computeSpeedScaling(-5), 0.0001);
        assertEquals(0.06, TubeFlowParticleLogic.computeSpeedScaling(1), 0.0001);
        assertEquals(0.15, TubeFlowParticleLogic.computeSpeedScaling(10), 0.0001);
    }

    @Test
    public void testCalculateFlowVelocity() {
        assertEquals(0.0, TubeFlowParticleLogic.calculateFlowVelocity(10, 10), 0.0001);
        assertEquals(0.0, TubeFlowParticleLogic.calculateFlowVelocity(5, 10), 0.0001);
        assertEquals(0.12, TubeFlowParticleLogic.calculateFlowVelocity(11, 10), 0.0001);
        assertEquals(0.30, TubeFlowParticleLogic.calculateFlowVelocity(20, 10), 0.0001);
    }

    @Test
    public void testGetWaypointInterpolation() {
        // Start and end coordinates
        double startX = 0.0, startY = 0.0, startZ = 0.0;
        double endX = 10.0, endY = 20.0, endZ = 30.0;

        // Progress 0.0
        double[] waypoints0 = TubeFlowParticleLogic.getWaypointInterpolation(startX, startY, startZ, endX, endY, endZ, 0.0);
        assertArrayEquals(new double[]{0.0, 0.0, 0.0}, waypoints0, 0.0001);

        // Progress 0.5
        double[] waypointsHalf = TubeFlowParticleLogic.getWaypointInterpolation(startX, startY, startZ, endX, endY, endZ, 0.5);
        assertArrayEquals(new double[]{5.0, 10.0, 15.0}, waypointsHalf, 0.0001);

        // Progress 1.0
        double[] waypoints1 = TubeFlowParticleLogic.getWaypointInterpolation(startX, startY, startZ, endX, endY, endZ, 1.0);
        assertArrayEquals(new double[]{10.0, 20.0, 30.0}, waypoints1, 0.0001);

        // Progress > 1.0 (should clamp to 1.0)
        double[] waypointsClampedHigh = TubeFlowParticleLogic.getWaypointInterpolation(startX, startY, startZ, endX, endY, endZ, 1.5);
        assertArrayEquals(new double[]{10.0, 20.0, 30.0}, waypointsClampedHigh, 0.0001);

        // Progress < 0.0 (should clamp to 0.0)
        double[] waypointsClampedLow = TubeFlowParticleLogic.getWaypointInterpolation(startX, startY, startZ, endX, endY, endZ, -0.5);
        assertArrayEquals(new double[]{0.0, 0.0, 0.0}, waypointsClampedLow, 0.0001);
    }
}
