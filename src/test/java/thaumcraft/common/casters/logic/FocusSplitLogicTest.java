package thaumcraft.common.casters.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FocusSplitLogic Unit Tests")
class FocusSplitLogicTest {

    private static final double EPSILON = 1.0E-5;

    @Test
    @DisplayName("Test 1 fork (no split)")
    void testOneFork() {
        double[] result = FocusSplitLogic.calculateSplitVector(1, 0, 0, 60, 0, 1);
        assertEquals(1.0, result[0], EPSILON);
        assertEquals(0.0, result[1], EPSILON);
        assertEquals(0.0, result[2], EPSILON);
    }

    @Test
    @DisplayName("Test 2 forks spread horizontally by 60 degrees")
    void testTwoForks() {
        // Looking directly along Z
        double dirX = 0, dirY = 0, dirZ = 1;
        int angle = 60;

        // Index 0 (-30 degrees from center)
        double[] r0 = FocusSplitLogic.calculateSplitVector(dirX, dirY, dirZ, angle, 0, 2);
        // Using Rodrigues' formula: axis is X (since dir=Z, up=Y, dir x up = -X, but logic normalizes it and finds an orthogonal axis).
        // Actually, cross( (0,0,1), (0,1,0) ) = (-1, 0, 0).
        // Rotation by -30 around the axis.
        // Let's just check the dot products (angle between original and new).
        double dot0 = r0[0]*dirX + r0[1]*dirY + r0[2]*dirZ;
        assertEquals(Math.cos(Math.toRadians(30)), dot0, EPSILON);

        // Index 1 (+30 degrees from center)
        double[] r1 = FocusSplitLogic.calculateSplitVector(dirX, dirY, dirZ, angle, 1, 2);
        double dot1 = r1[0]*dirX + r1[1]*dirY + r1[2]*dirZ;
        assertEquals(Math.cos(Math.toRadians(30)), dot1, EPSILON);

        // Ensure length is 1
        assertEquals(1.0, Math.sqrt(r0[0]*r0[0] + r0[1]*r0[1] + r0[2]*r0[2]), EPSILON);
        assertEquals(1.0, Math.sqrt(r1[0]*r1[0] + r1[1]*r1[1] + r1[2]*r1[2]), EPSILON);
    }

    @Test
    @DisplayName("Test 3 forks spread horizontally by 90 degrees")
    void testThreeForks() {
        // Looking along X
        double dirX = 1, dirY = 0, dirZ = 0;
        int angle = 90;

        // Index 0 (-45 degrees)
        double[] r0 = FocusSplitLogic.calculateSplitVector(dirX, dirY, dirZ, angle, 0, 3);
        double dot0 = r0[0]*dirX + r0[1]*dirY + r0[2]*dirZ;
        assertEquals(Math.cos(Math.toRadians(45)), dot0, EPSILON);

        // Index 1 (0 degrees, center)
        double[] r1 = FocusSplitLogic.calculateSplitVector(dirX, dirY, dirZ, angle, 1, 3);
        assertEquals(Math.cos(Math.toRadians(0)), r1[0], EPSILON);
        assertEquals(0.0, r1[1], EPSILON);
        assertEquals(0.0, r1[2], EPSILON);

        // Index 2 (+45 degrees)
        double[] r2 = FocusSplitLogic.calculateSplitVector(dirX, dirY, dirZ, angle, 2, 3);
        double dot2 = r2[0]*dirX + r2[1]*dirY + r2[2]*dirZ;
        assertEquals(Math.cos(Math.toRadians(45)), dot2, EPSILON);
    }

    @Test
    @DisplayName("Test vertical direction edge case")
    void testVerticalDirection() {
        // Looking straight up (0, 1, 0)
        double dirX = 0, dirY = 1, dirZ = 0;
        int angle = 60;

        // 2 forks
        double[] r0 = FocusSplitLogic.calculateSplitVector(dirX, dirY, dirZ, angle, 0, 2);
        double dot0 = r0[0]*dirX + r0[1]*dirY + r0[2]*dirZ;
        assertEquals(Math.cos(Math.toRadians(30)), dot0, EPSILON);

        double[] r1 = FocusSplitLogic.calculateSplitVector(dirX, dirY, dirZ, angle, 1, 2);
        double dot1 = r1[0]*dirX + r1[1]*dirY + r1[2]*dirZ;
        assertEquals(Math.cos(Math.toRadians(30)), dot1, EPSILON);
    }
}
