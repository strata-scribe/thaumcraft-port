package thaumcraft.common.entities.monster.boss;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CrimsonPraetorShieldBashLogicTest {

    @Test
    public void testCalculateKnockbackVelocity() {
        // Yaw = 0 (facing +Z) -> knockback should be towards +Z
        double[] vel0 = CrimsonPraetorShieldBashLogic.calculateKnockbackVelocity(0f, 2.0);
        assertEquals(0.0, vel0[0], 0.001);
        assertEquals(2.0, vel0[1], 0.001);

        // Yaw = 90 (facing -X) -> knockback should be towards -X
        double[] vel90 = CrimsonPraetorShieldBashLogic.calculateKnockbackVelocity(90f, 2.0);
        assertEquals(-2.0, vel90[0], 0.001);
        assertEquals(0.0, vel90[1], 0.001);

        // Yaw = 180 (facing -Z) -> knockback should be towards -Z
        double[] vel180 = CrimsonPraetorShieldBashLogic.calculateKnockbackVelocity(180f, 2.0);
        assertEquals(0.0, vel180[0], 0.001);
        assertEquals(-2.0, vel180[1], 0.001);

        // Yaw = 270 (facing +X) -> knockback should be towards +X
        double[] vel270 = CrimsonPraetorShieldBashLogic.calculateKnockbackVelocity(270f, 2.0);
        assertEquals(2.0, vel270[0], 0.001);
        assertEquals(0.0, vel270[1], 0.001);

        // Yaw = 45 -> knockback should be towards -X and +Z
        double[] vel45 = CrimsonPraetorShieldBashLogic.calculateKnockbackVelocity(45f, 2.0);
        assertEquals(-1.414, vel45[0], 0.01);
        assertEquals(1.414, vel45[1], 0.01);
    }

    @Test
    public void testCalculateStunDurationTicks() {
        // 0 damage -> 0 ticks
        assertEquals(0, CrimsonPraetorShieldBashLogic.calculateStunDurationTicks(0f));

        // 2 damage -> 10 ticks
        assertEquals(10, CrimsonPraetorShieldBashLogic.calculateStunDurationTicks(2.0f));

        // 10 damage -> 50 ticks
        assertEquals(50, CrimsonPraetorShieldBashLogic.calculateStunDurationTicks(10.0f));

        // 15 damage -> 75 ticks, capped at 60
        assertEquals(60, CrimsonPraetorShieldBashLogic.calculateStunDurationTicks(15.0f));

        // 100 damage -> 500 ticks, capped at 60
        assertEquals(60, CrimsonPraetorShieldBashLogic.calculateStunDurationTicks(100.0f));

        // negative damage -> 0 ticks (prevent negative duration)
        assertEquals(0, CrimsonPraetorShieldBashLogic.calculateStunDurationTicks(-5.0f));
    }
}
