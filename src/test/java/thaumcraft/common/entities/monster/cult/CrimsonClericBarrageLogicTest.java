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
}
