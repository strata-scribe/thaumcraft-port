package thaumcraft.common.items.casters.foci;

import org.junit.jupiter.api.Test;
import thaumcraft.common.casters.FocusChainLogic;
import thaumcraft.common.casters.FocusLogic;
import thaumcraft.common.casters.logic.FocusSplitLogic;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FocusModTest {

    // --- FocusModChain Tests ---

    static class SimpleTarget implements FocusChainLogic.ChainTarget {
        private final int id;
        private final double x, y, z;

        SimpleTarget(int id, double x, double y, double z) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override public int getId() { return id; }
        @Override public double getX() { return x; }
        @Override public double getY() { return y; }
        @Override public double getZ() { return z; }
    }

    @Test
    void testChainSecondaryTargets() {
        SimpleTarget primary = new SimpleTarget(1, 0, 0, 0);
        List<FocusChainLogic.ChainTarget> available = new ArrayList<>();

        // Target 2 is closer than Target 3, Target 4 is out of radius
        available.add(new SimpleTarget(2, 2, 0, 0));
        available.add(new SimpleTarget(3, 4, 0, 0));
        available.add(new SimpleTarget(4, 10, 0, 0));

        List<FocusChainLogic.ChainTarget> secondary = FocusChainLogic.getSecondaryTargets(
                available, primary, 2, 8.0);

        assertEquals(2, secondary.size());
        assertEquals(2, secondary.get(0).getId(), "First target should be the closest one");
        assertEquals(3, secondary.get(1).getId(), "Second target should be the next closest one");
    }

    @Test
    void testChainSecondaryTargets_EmptyAvailable() {
        SimpleTarget primary = new SimpleTarget(1, 0, 0, 0);
        List<FocusChainLogic.ChainTarget> available = new ArrayList<>();
        List<FocusChainLogic.ChainTarget> secondary = FocusChainLogic.getSecondaryTargets(
                available, primary, 2, 8.0);

        assertTrue(secondary.isEmpty());
    }

    @Test
    void testChainSecondaryTargets_NullPrimary() {
        List<FocusChainLogic.ChainTarget> available = new ArrayList<>();
        available.add(new SimpleTarget(2, 2, 0, 0));
        List<FocusChainLogic.ChainTarget> secondary = FocusChainLogic.getSecondaryTargets(
                available, null, 2, 8.0);

        assertTrue(secondary.isEmpty());
    }

    @Test
    void testChainComplexity() {
        assertEquals(3, FocusChainLogic.calculateChainComplexity(2));
        assertEquals(6, FocusChainLogic.calculateChainComplexity(4));
        // Math.max(2, (int)(1.5f * maxTargets))
        assertEquals(2, FocusChainLogic.calculateChainComplexity(1));
    }

    @Test
    void testChainFalloff() {
        assertEquals(1.0f, FocusChainLogic.calculateFalloff(1.0f, 0));
        assertEquals(0.8f, FocusChainLogic.calculateFalloff(1.0f, 1), 0.001f);
        assertEquals(0.64f, FocusChainLogic.calculateFalloff(1.0f, 2), 0.001f);
    }

    // --- FocusModScatter Tests ---

    @Test
    void testScatterVector() {
        double[] dir = {1.0, 0.0, 0.0};
        int coneDegrees = 90;

        // Zero jitter should return original normalized direction
        double[] result1 = FocusLogic.calculateScatterVector(dir[0], dir[1], dir[2], 0, 0, 0, coneDegrees);
        assertEquals(1.0, result1[0], 0.0001);
        assertEquals(0.0, result1[1], 0.0001);
        assertEquals(0.0, result1[2], 0.0001);

        // Positive jitter
        double[] result2 = FocusLogic.calculateScatterVector(dir[0], dir[1], dir[2], 1.0, 1.0, 1.0, coneDegrees);
        // Ensure the vector is normalized
        double len = Math.sqrt(result2[0] * result2[0] + result2[1] * result2[1] + result2[2] * result2[2]);
        assertEquals(1.0, len, 0.0001);

        // Ensure it's different from original
        assertTrue(result2[0] < 1.0);
        assertTrue(result2[1] > 0.0);
        assertTrue(result2[2] > 0.0);
    }

    @Test
    void testScatterComplexity() {
        // forks=2, coneDegrees=30 -> 2.0 * (2 - 30/45) = 2.0 * (2 - 0.666) = 2.0 * 1.333 = 2.666 -> 2
        assertEquals(2, FocusLogic.calculateScatterComplexity(2, 30));

        // forks=10, coneDegrees=360 -> 2.0 * (10 - 360/45) = 2.0 * (10 - 8) = 4
        assertEquals(4, FocusLogic.calculateScatterComplexity(10, 360));
    }

    @Test
    void testScatterPowerMultiplier() {
        assertEquals(1.0f, FocusLogic.calculateScatterPowerMultiplier(0));
        // 1.0 / (2/2) = 1.0
        assertEquals(1.0f, FocusLogic.calculateScatterPowerMultiplier(2), 0.001f);
        // 1.0 / (4/2) = 0.5
        assertEquals(0.5f, FocusLogic.calculateScatterPowerMultiplier(4), 0.001f);
        // 1.0 / (10/2) = 0.2
        assertEquals(0.2f, FocusLogic.calculateScatterPowerMultiplier(10), 0.001f);
    }

    // --- FocusModSplit Tests ---

    @Test
    void testSplitVector_NoForks() {
        double[] dir = {1.0, 0.0, 0.0};
        double[] result = FocusSplitLogic.calculateSplitVector(dir[0], dir[1], dir[2], 90, 0, 1);
        assertEquals(1.0, result[0], 0.0001);
        assertEquals(0.0, result[1], 0.0001);
        assertEquals(0.0, result[2], 0.0001);
    }

    @Test
    void testSplitVector_TwoForks() {
        // Going along Z axis
        double[] dir = {0.0, 0.0, 1.0};
        // 90 degrees total spread, so index 0 = -45 deg, index 1 = +45 deg

        double[] result0 = FocusSplitLogic.calculateSplitVector(dir[0], dir[1], dir[2], 90, 0, 2);
        double[] result1 = FocusSplitLogic.calculateSplitVector(dir[0], dir[1], dir[2], 90, 1, 2);

        // For -45 deg around Y axis (assuming Up is Y=1)
        // newX = sin(-45), newZ = cos(-45)
        // Note: FocusSplitLogic calculates spread horizontally
        double halfSqrt2 = Math.sqrt(2) / 2;

        // Direction is Z, rotation axis is X x (X x Z?) No, up is Y.
        // Cross(Y, Z) = X. So rotation is around X?
        // Let's just check lengths are 1 and they are symmetric.

        double len0 = Math.sqrt(result0[0]*result0[0] + result0[1]*result0[1] + result0[2]*result0[2]);
        assertEquals(1.0, len0, 0.0001);

        double len1 = Math.sqrt(result1[0]*result1[0] + result1[1]*result1[1] + result1[2]*result1[2]);
        assertEquals(1.0, len1, 0.0001);

        // Dot product between them should be cos(90) = 0
        double dot = result0[0]*result1[0] + result0[1]*result1[1] + result0[2]*result1[2];
        assertEquals(0.0, dot, 0.0001);
    }

    @Test
    void testSplitVector_VerticalDirection() {
        // Going straight up
        double[] dir = {0.0, 1.0, 0.0};

        double[] result0 = FocusSplitLogic.calculateSplitVector(dir[0], dir[1], dir[2], 90, 0, 2);
        double[] result1 = FocusSplitLogic.calculateSplitVector(dir[0], dir[1], dir[2], 90, 1, 2);

        double len0 = Math.sqrt(result0[0]*result0[0] + result0[1]*result0[1] + result0[2]*result0[2]);
        assertEquals(1.0, len0, 0.0001);

        double len1 = Math.sqrt(result1[0]*result1[0] + result1[1]*result1[1] + result1[2]*result1[2]);
        assertEquals(1.0, len1, 0.0001);

        // Dot product between them should be cos(90) = 0
        double dot = result0[0]*result1[0] + result0[1]*result1[1] + result0[2]*result1[2];
        assertEquals(0.0, dot, 0.0001);
    }

    @Test
    void testSplitTargetComplexity() {
        assertEquals(4, FocusLogic.calculateSplitTargetComplexity());
    }

    @Test
    void testSplitTrajectoryComplexity() {
        assertEquals(5, FocusLogic.calculateSplitTrajectoryComplexity());
    }

    @Test
    void testSplitPowerMultiplier() {
        assertEquals(0.75f, FocusLogic.calculateSplitPowerMultiplier(), 0.001f);
    }
}
