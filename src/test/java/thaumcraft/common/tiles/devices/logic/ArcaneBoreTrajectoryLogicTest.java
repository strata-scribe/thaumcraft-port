package thaumcraft.common.tiles.devices.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArcaneBoreTrajectoryLogicTest {

    private static final double EPSILON = 1e-6;

    @Test
    public void testCalculateStepIncrements() {
        // Yaw 0, Pitch 0 -> +Z
        double[] inc1 = ArcaneBoreTrajectoryLogic.calculateStepIncrements(0, 0);
        assertEquals(0.0, inc1[0], EPSILON);
        assertEquals(0.0, inc1[1], EPSILON);
        assertEquals(1.0, inc1[2], EPSILON);

        // Yaw 90, Pitch 0 -> -X
        double[] inc2 = ArcaneBoreTrajectoryLogic.calculateStepIncrements(90, 0);
        assertEquals(-1.0, inc2[0], EPSILON);
        assertEquals(0.0, inc2[1], EPSILON);
        assertEquals(0.0, inc2[2], EPSILON);

        // Yaw 180, Pitch 0 -> -Z
        double[] inc3 = ArcaneBoreTrajectoryLogic.calculateStepIncrements(180, 0);
        assertEquals(0.0, inc3[0], EPSILON);
        assertEquals(0.0, inc3[1], EPSILON);
        assertEquals(-1.0, inc3[2], EPSILON);

        // Yaw 270, Pitch 0 -> +X
        double[] inc4 = ArcaneBoreTrajectoryLogic.calculateStepIncrements(270, 0);
        assertEquals(1.0, inc4[0], EPSILON);
        assertEquals(0.0, inc4[1], EPSILON);
        assertEquals(0.0, inc4[2], EPSILON);

        // Pitch -90 -> +Y
        double[] inc5 = ArcaneBoreTrajectoryLogic.calculateStepIncrements(0, -90);
        assertEquals(0.0, inc5[0], EPSILON);
        assertEquals(1.0, inc5[1], EPSILON);
        assertEquals(0.0, inc5[2], EPSILON);

        // Pitch 90 -> -Y
        double[] inc6 = ArcaneBoreTrajectoryLogic.calculateStepIncrements(0, 90);
        assertEquals(0.0, inc6[0], EPSILON);
        assertEquals(-1.0, inc6[1], EPSILON);
        assertEquals(0.0, inc6[2], EPSILON);
    }

    @Test
    public void testCalculateDigAngle() {
        double[] result = ArcaneBoreTrajectoryLogic.calculateDigAngle(45.0, 30.0, 10.0);
        assertEquals(55.0, result[0], EPSILON);
        assertEquals(40.0, result[1], EPSILON);
    }

    @Test
    public void testCalculateTargetBlock() {
        // Start (0, 0, 0), Yaw 0 (+Z), Pitch 0, Distance 5 -> Target (0, 0, 5)
        int[] target1 = ArcaneBoreTrajectoryLogic.calculateTargetBlock(0, 0, 0, 0, 0, 5);
        assertArrayEquals(new int[]{0, 0, 5}, target1);

        // Start (10.5, 64.5, -10.5), Yaw 90 (-X), Pitch 0, Distance 10 -> Target (0, 64, -11)
        int[] target2 = ArcaneBoreTrajectoryLogic.calculateTargetBlock(10.5, 64.5, -10.5, 90, 0, 10);
        assertArrayEquals(new int[]{0, 64, -11}, target2);

        // Start (0, 0, 0), Yaw 0, Pitch 90 (-Y), Distance 10 -> Target (0, -10, 0)
        int[] target3 = ArcaneBoreTrajectoryLogic.calculateTargetBlock(0, 0, 0, 0, 90, 10);
        assertArrayEquals(new int[]{0, -10, 0}, target3);
    }

    @Test
    public void testCalculateTargetBlockBounds() {
        int[] bounds = ArcaneBoreTrajectoryLogic.calculateTargetBlockBounds(5, 10, -5, 2);
        assertArrayEquals(new int[]{3, 8, -7, 7, 12, -3}, bounds);
    }
}
