package thaumcraft.common.tiles.essentia.logic;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TubeParticleFlowLogicTest {

    private static final double EPSILON = 1e-6;

    @Test
    public void testGetPositionAtProgress_EmptyList() {
        assertNull(TubeParticleFlowLogic.getPositionAtProgress(null, 0.5));
        assertNull(TubeParticleFlowLogic.getPositionAtProgress(new ArrayList<>(), 0.5));
    }

    @Test
    public void testGetPositionAtProgress_SingleNode() {
        List<double[]> nodes = Collections.singletonList(new double[]{1.0, 2.0, 3.0});
        double[] pos = TubeParticleFlowLogic.getPositionAtProgress(nodes, 0.5);
        assertNotNull(pos);
        assertArrayEquals(new double[]{1.0, 2.0, 3.0}, pos, EPSILON);
    }

    @Test
    public void testGetPositionAtProgress_TwoNodes() {
        List<double[]> nodes = Arrays.asList(
                new double[]{0.0, 0.0, 0.0},
                new double[]{10.0, 10.0, 10.0}
        );

        // Start (0%)
        double[] pos0 = TubeParticleFlowLogic.getPositionAtProgress(nodes, 0.0);
        assertArrayEquals(new double[]{0.0, 0.0, 0.0}, pos0, EPSILON);

        // Middle (50%)
        double[] pos50 = TubeParticleFlowLogic.getPositionAtProgress(nodes, 0.5);
        assertArrayEquals(new double[]{5.0, 5.0, 5.0}, pos50, EPSILON);

        // End (100%)
        double[] pos100 = TubeParticleFlowLogic.getPositionAtProgress(nodes, 1.0);
        assertArrayEquals(new double[]{10.0, 10.0, 10.0}, pos100, EPSILON);

        // Clamped below 0
        double[] posNeg = TubeParticleFlowLogic.getPositionAtProgress(nodes, -0.5);
        assertArrayEquals(new double[]{0.0, 0.0, 0.0}, posNeg, EPSILON);

        // Clamped above 1
        double[] posOver = TubeParticleFlowLogic.getPositionAtProgress(nodes, 1.5);
        assertArrayEquals(new double[]{10.0, 10.0, 10.0}, posOver, EPSILON);
    }

    @Test
    public void testGetPositionAtProgress_ThreeNodes() {
        List<double[]> nodes = Arrays.asList(
                new double[]{0.0, 0.0, 0.0},
                new double[]{10.0, 0.0, 0.0},
                new double[]{10.0, 10.0, 0.0}
        );

        // First segment (25% overall -> 50% through first segment)
        double[] pos25 = TubeParticleFlowLogic.getPositionAtProgress(nodes, 0.25);
        assertArrayEquals(new double[]{5.0, 0.0, 0.0}, pos25, EPSILON);

        // Middle node (50%)
        double[] pos50 = TubeParticleFlowLogic.getPositionAtProgress(nodes, 0.5);
        assertArrayEquals(new double[]{10.0, 0.0, 0.0}, pos50, EPSILON);

        // Second segment (75% overall -> 50% through second segment)
        double[] pos75 = TubeParticleFlowLogic.getPositionAtProgress(nodes, 0.75);
        assertArrayEquals(new double[]{10.0, 5.0, 0.0}, pos75, EPSILON);

        // End (100%)
        double[] pos100 = TubeParticleFlowLogic.getPositionAtProgress(nodes, 1.0);
        assertArrayEquals(new double[]{10.0, 10.0, 0.0}, pos100, EPSILON);
    }

    @Test
    public void testComputeTrajectory_EmptyList() {
        assertTrue(TubeParticleFlowLogic.computeTrajectory(null, 5).isEmpty());
        assertTrue(TubeParticleFlowLogic.computeTrajectory(new ArrayList<>(), 5).isEmpty());
    }

    @Test
    public void testComputeTrajectory_SingleNode() {
        List<double[]> nodes = Collections.singletonList(new double[]{1.0, 2.0, 3.0});
        List<double[]> traj = TubeParticleFlowLogic.computeTrajectory(nodes, 5);
        assertEquals(1, traj.size());
        assertArrayEquals(new double[]{1.0, 2.0, 3.0}, traj.get(0), EPSILON);
    }

    @Test
    public void testComputeTrajectory_TwoNodes() {
        List<double[]> nodes = Arrays.asList(
                new double[]{0.0, 0.0, 0.0},
                new double[]{10.0, 0.0, 0.0}
        );

        List<double[]> traj = TubeParticleFlowLogic.computeTrajectory(nodes, 4);

        // Should have 4 steps for the segment + 1 for the final node = 5 nodes
        assertEquals(5, traj.size());

        assertArrayEquals(new double[]{0.0, 0.0, 0.0}, traj.get(0), EPSILON);
        assertArrayEquals(new double[]{2.5, 0.0, 0.0}, traj.get(1), EPSILON);
        assertArrayEquals(new double[]{5.0, 0.0, 0.0}, traj.get(2), EPSILON);
        assertArrayEquals(new double[]{7.5, 0.0, 0.0}, traj.get(3), EPSILON);
        assertArrayEquals(new double[]{10.0, 0.0, 0.0}, traj.get(4), EPSILON);
    }

    @Test
    public void testComputeTrajectory_ThreeNodes() {
        List<double[]> nodes = Arrays.asList(
                new double[]{0.0, 0.0, 0.0},
                new double[]{10.0, 0.0, 0.0},
                new double[]{10.0, 10.0, 0.0}
        );

        List<double[]> traj = TubeParticleFlowLogic.computeTrajectory(nodes, 2);

        // 2 segments, 2 steps each + 1 final node = 5 nodes total
        assertEquals(5, traj.size());

        assertArrayEquals(new double[]{0.0, 0.0, 0.0}, traj.get(0), EPSILON);
        assertArrayEquals(new double[]{5.0, 0.0, 0.0}, traj.get(1), EPSILON);

        assertArrayEquals(new double[]{10.0, 0.0, 0.0}, traj.get(2), EPSILON);
        assertArrayEquals(new double[]{10.0, 5.0, 0.0}, traj.get(3), EPSILON);

        assertArrayEquals(new double[]{10.0, 10.0, 0.0}, traj.get(4), EPSILON);
    }
}
