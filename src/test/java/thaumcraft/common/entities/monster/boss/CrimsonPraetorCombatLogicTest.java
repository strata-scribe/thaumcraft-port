package thaumcraft.common.entities.monster.boss;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CrimsonPraetorCombatLogicTest {

    @Test
    public void testCalculatePhase() {
        // Max health 100
        assertEquals(1, CrimsonPraetorCombatLogic.calculatePhase(100f, 100f));
        assertEquals(1, CrimsonPraetorCombatLogic.calculatePhase(61f, 100f));

        assertEquals(2, CrimsonPraetorCombatLogic.calculatePhase(60f, 100f));
        assertEquals(2, CrimsonPraetorCombatLogic.calculatePhase(31f, 100f));

        assertEquals(3, CrimsonPraetorCombatLogic.calculatePhase(30f, 100f));
        assertEquals(3, CrimsonPraetorCombatLogic.calculatePhase(1f, 100f));

        // Edge cases
        assertEquals(1, CrimsonPraetorCombatLogic.calculatePhase(100f, 0f));
    }

    @Test
    public void testGetWhirlwindRadius() {
        assertEquals(3.0f, CrimsonPraetorCombatLogic.getWhirlwindRadius(1), 0.01f);
        assertEquals(4.5f, CrimsonPraetorCombatLogic.getWhirlwindRadius(2), 0.01f);
        assertEquals(6.0f, CrimsonPraetorCombatLogic.getWhirlwindRadius(3), 0.01f);
    }

    @Test
    public void testWhirlwindRange() {
        assertTrue(CrimsonPraetorCombatLogic.isTargetInWhirlwindRange(2.0, 0, 2.0, 3.0f)); // dist ~2.82
        assertFalse(CrimsonPraetorCombatLogic.isTargetInWhirlwindRange(3.0, 0, 3.0, 3.0f)); // dist ~4.24

        assertTrue(CrimsonPraetorCombatLogic.isTargetInWhirlwindRange(3.0, 3.0, 0, 4.5f)); // dist ~4.24
        assertFalse(CrimsonPraetorCombatLogic.isTargetInWhirlwindRange(4.0, 3.0, 0, 4.5f)); // dist 5.0
    }

    @Test
    public void testWhirlwindDamageAndKnockback() {
        assertEquals(10.0f, CrimsonPraetorCombatLogic.calculateWhirlwindDamage(1, 10.0f), 0.01f);
        assertEquals(12.5f, CrimsonPraetorCombatLogic.calculateWhirlwindDamage(2, 10.0f), 0.01f);
        assertEquals(15.0f, CrimsonPraetorCombatLogic.calculateWhirlwindDamage(3, 10.0f), 0.01f);

        assertEquals(1.0f, CrimsonPraetorCombatLogic.getWhirlwindKnockback(1), 0.01f);
        assertEquals(1.5f, CrimsonPraetorCombatLogic.getWhirlwindKnockback(2), 0.01f);
        assertEquals(2.0f, CrimsonPraetorCombatLogic.getWhirlwindKnockback(3), 0.01f);
    }

    @Test
    public void testBattleRoarRangeAndBuffs() {
        assertTrue(CrimsonPraetorCombatLogic.isCultistInRangeForRoar(10.0, 10.0, 10.0)); // dist ~17.32
        assertTrue(CrimsonPraetorCombatLogic.isCultistInRangeForRoar(24.0, 0.0, 0.0));
        assertFalse(CrimsonPraetorCombatLogic.isCultistInRangeForRoar(20.0, 20.0, 0.0)); // dist ~28.28

        assertEquals(1, CrimsonPraetorCombatLogic.getRoarStrengthAmplifier());
        assertEquals(0, CrimsonPraetorCombatLogic.getRoarSpeedAmplifier());
    }
}
