package thaumcraft.common.entities.monster.cult;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CrimsonClericBarrageLogicTest {

    @Test
    public void testCalculateTrajectoryOffset_SingleShot() {
        double[] offset = CrimsonClericBarrageLogic.calculateTrajectoryOffset(0, 1, 1.0);
        assertArrayEquals(new double[]{0.0, 0.0, 0.0}, offset, 0.001);
    }

    @Test
    public void testCalculateTrajectoryOffset_TwoShots() {
        double[] offset1 = CrimsonClericBarrageLogic.calculateTrajectoryOffset(0, 2, 1.0);
        assertArrayEquals(new double[]{-1.0, 0.0, 0.0}, offset1, 0.001);

        double[] offset2 = CrimsonClericBarrageLogic.calculateTrajectoryOffset(1, 2, 1.0);
        assertArrayEquals(new double[]{1.0, 0.0, 0.0}, offset2, 0.001);
    }

    @Test
    public void testCalculateTrajectoryOffset_ThreeShots() {
        double[] offset1 = CrimsonClericBarrageLogic.calculateTrajectoryOffset(0, 3, 2.0);
        assertArrayEquals(new double[]{-2.0, 0.0, 0.0}, offset1, 0.001);

        double[] offset2 = CrimsonClericBarrageLogic.calculateTrajectoryOffset(1, 3, 2.0);
        assertArrayEquals(new double[]{0.0, 0.0, 0.0}, offset2, 0.001);

        double[] offset3 = CrimsonClericBarrageLogic.calculateTrajectoryOffset(2, 3, 2.0);
        assertArrayEquals(new double[]{2.0, 0.0, 0.0}, offset3, 0.001);
    }

    @Test
    public void testCalculateTrajectoryOffset_ZeroSizeOrNegative() {
        double[] offset = CrimsonClericBarrageLogic.calculateTrajectoryOffset(0, 0, 1.0);
        assertArrayEquals(new double[]{0.0, 0.0, 0.0}, offset, 0.001);

        double[] offsetNeg = CrimsonClericBarrageLogic.calculateTrajectoryOffset(0, -1, 1.0);
        assertArrayEquals(new double[]{0.0, 0.0, 0.0}, offsetNeg, 0.001);
    }

    @Test
    public void testCalculateCooldownInterval() {
        // Base cooldown: 40 + (3 * 10) = 70. Modifier 1.0 -> 70
        assertEquals(70, CrimsonClericBarrageLogic.calculateCooldownInterval(3, 1.0));

        // Base cooldown: 40 + (5 * 10) = 90. Modifier 2.0 -> 45
        assertEquals(45, CrimsonClericBarrageLogic.calculateCooldownInterval(5, 2.0));

        // Base cooldown: 40 + (1 * 10) = 50. Modifier 0.5 -> 100
        assertEquals(100, CrimsonClericBarrageLogic.calculateCooldownInterval(1, 0.5));

        // Ensure minimum 10 ticks cooldown
        assertEquals(10, CrimsonClericBarrageLogic.calculateCooldownInterval(1, 10.0));

        // Ensure minimum 0.1 modifier prevents division by zero
        assertEquals(700, CrimsonClericBarrageLogic.calculateCooldownInterval(3, 0.0));
        assertEquals(700, CrimsonClericBarrageLogic.calculateCooldownInterval(3, -1.0));
    }

    @Test
    public void testCalculateStaggerInterval() {
        // Base interval: 5. Modifier 1.0 -> 5
        assertEquals(5, CrimsonClericBarrageLogic.calculateStaggerInterval(1.0));

        // Base interval: 5. Modifier 2.0 -> Math.round(2.5) = 3
        assertEquals(3, CrimsonClericBarrageLogic.calculateStaggerInterval(2.0));

        // Base interval: 5. Modifier 0.5 -> 10
        assertEquals(10, CrimsonClericBarrageLogic.calculateStaggerInterval(0.5));

        // Ensure minimum 1 tick interval
        assertEquals(1, CrimsonClericBarrageLogic.calculateStaggerInterval(10.0));

        // Ensure minimum 0.1 modifier prevents division by zero
        assertEquals(50, CrimsonClericBarrageLogic.calculateStaggerInterval(0.0));
        assertEquals(50, CrimsonClericBarrageLogic.calculateStaggerInterval(-1.0));
    }

    @Test
    public void testCalculateAimLeading_MovingTarget() {
        // Target is at (10, 0, 0), moving away at (1, 0, 0)
        // Distance is 10, projectile speed is 5
        // time = 10 / 5 = 2.0
        // Expected X = 10 + 1 * 2 = 12
        double[] pos = CrimsonClericBarrageLogic.calculateAimLeading(
            10.0, 0.0, 0.0,
            1.0, 0.0, 0.0,
            10.0, 5.0
        );
        assertArrayEquals(new double[]{12.0, 0.0, 0.0}, pos, 0.001);
    }

    @Test
    public void testCalculateAimLeading_StationaryTarget() {
        double[] pos = CrimsonClericBarrageLogic.calculateAimLeading(
            10.0, 0.0, 0.0,
            0.0, 0.0, 0.0,
            10.0, 5.0
        );
        assertArrayEquals(new double[]{10.0, 0.0, 0.0}, pos, 0.001);
    }

    @Test
    public void testCalculateAimLeading_ZeroOrNegativeProjectileSpeed() {
        double[] posZero = CrimsonClericBarrageLogic.calculateAimLeading(
            10.0, 0.0, 0.0,
            1.0, 0.0, 0.0,
            10.0, 0.0
        );
        assertArrayEquals(new double[]{10.0, 0.0, 0.0}, posZero, 0.001);

        double[] posNeg = CrimsonClericBarrageLogic.calculateAimLeading(
            10.0, 0.0, 0.0,
            1.0, 0.0, 0.0,
            10.0, -5.0
        );
        assertArrayEquals(new double[]{10.0, 0.0, 0.0}, posNeg, 0.001);
    }

    @Test
    public void testCalculateSpawnTimer() {
        // Base timer 1000, diff 1.0, warp 0 -> 1000
        assertEquals(1000, CrimsonPortalSpawningLogic.calculateSpawnTimer(1000, 1.0, 0));

        // Base timer 1000, diff 2.0, warp 0 -> 500
        assertEquals(500, CrimsonPortalSpawningLogic.calculateSpawnTimer(1000, 2.0, 0));

        // Base timer 1000, diff 1.0, warp 50 -> 1000 - min(100, 500) = 900
        assertEquals(900, CrimsonPortalSpawningLogic.calculateSpawnTimer(1000, 1.0, 50));

        // Base timer 1000, diff 1.0, warp 500 -> 1000 - min(1000, 500) = 500
        assertEquals(500, CrimsonPortalSpawningLogic.calculateSpawnTimer(1000, 1.0, 500));

        // Base timer 10, diff 10.0, warp 100 -> min 20 ticks
        assertEquals(20, CrimsonPortalSpawningLogic.calculateSpawnTimer(10, 10.0, 100));
    }

    @Test
    public void testPortalWaveMobCountScaling() {
        long ONE_DAY = 24000L;
        // Early game (1 day), low warp (10)
        CrimsonPortalSpawningLogic.CultistWaveMix mix1 = CrimsonPortalSpawningLogic.calculateWaveMix(10, ONE_DAY);
        assertEquals(2, mix1.knights);
        assertEquals(1, mix1.clerics);
        assertEquals(0, mix1.praetors);

        // Mid game (60 days), medium warp (60)
        CrimsonPortalSpawningLogic.CultistWaveMix mix2 = CrimsonPortalSpawningLogic.calculateWaveMix(60, 60 * ONE_DAY);
        assertEquals(2, mix2.knights);
        assertEquals(2, mix2.clerics);
        assertEquals(1, mix2.praetors);

        // Late game (150 days), high warp (100)
        CrimsonPortalSpawningLogic.CultistWaveMix mix3 = CrimsonPortalSpawningLogic.calculateWaveMix(100, 150 * ONE_DAY);
        assertEquals(3, mix3.knights);
        assertEquals(3, mix3.clerics);
        assertEquals(2, mix3.praetors);
    }
}
