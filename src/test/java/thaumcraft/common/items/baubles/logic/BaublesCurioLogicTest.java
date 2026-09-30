package thaumcraft.common.items.baubles.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BaublesCurioLogic Domain Logic Tests")
public class BaublesCurioLogicTest {

    @Test
    @DisplayName("Verify Vis Amulet max charge is 250")
    public void testCalculateVisAmuletMaxCharge() {
        assertEquals(250, BaublesCurioLogic.calculateVisAmuletMaxCharge());
    }

    @Test
    @DisplayName("Verify calculateRechargeTransfer with various charges and limits")
    public void testCalculateRechargeTransfer() {
        // Full charge, transfer rate limits
        assertEquals(5, BaublesCurioLogic.calculateRechargeTransfer(250, 50, 5));

        // Partial charge, amulet charge limits
        assertEquals(2, BaublesCurioLogic.calculateRechargeTransfer(2, 50, 5));

        // Excess needed, item needed charge limits
        assertEquals(3, BaublesCurioLogic.calculateRechargeTransfer(50, 3, 5));

        // Needed > charge, but rate is large
        assertEquals(10, BaublesCurioLogic.calculateRechargeTransfer(10, 100, 20));

        // Zero charge
        assertEquals(0, BaublesCurioLogic.calculateRechargeTransfer(0, 50, 5));

        // Zero needed charge
        assertEquals(0, BaublesCurioLogic.calculateRechargeTransfer(50, 0, 5));

        // Zero transfer rate
        assertEquals(0, BaublesCurioLogic.calculateRechargeTransfer(50, 50, 0));

        // Negative parameters
        assertEquals(0, BaublesCurioLogic.calculateRechargeTransfer(-10, 50, 5));
        assertEquals(0, BaublesCurioLogic.calculateRechargeTransfer(50, -5, 5));
        assertEquals(0, BaublesCurioLogic.calculateRechargeTransfer(50, 50, -1));
        assertEquals(0, BaublesCurioLogic.calculateRechargeTransfer(-1, -1, -1));
    }

    @Test
    @DisplayName("Verify calculateCuriosityBonusExp with base, zero, negative, and bonus factors")
    public void testCalculateCuriosityBonusExp() {
        // Base xp and positive bonus factor: 10 xp + 25% bonus = round(12.5) = 13 (or >= 10)
        assertEquals(13, BaublesCurioLogic.calculateCuriosityBonusExp(10, 0.25f));

        // 100% bonus
        assertEquals(200, BaublesCurioLogic.calculateCuriosityBonusExp(100, 1.0f));

        // Zero xp
        assertEquals(0, BaublesCurioLogic.calculateCuriosityBonusExp(0, 0.5f));

        // Negative xp
        assertEquals(0, BaublesCurioLogic.calculateCuriosityBonusExp(-10, 0.5f));

        // Zero bonus factor
        assertEquals(10, BaublesCurioLogic.calculateCuriosityBonusExp(10, 0.0f));

        // Negative bonus factor (clamped to 0.0f bonus)
        assertEquals(10, BaublesCurioLogic.calculateCuriosityBonusExp(10, -0.5f));
    }

    @Test
    @DisplayName("Verify shouldGrantCuriosityKnowledge roll and chance thresholds")
    public void testShouldGrantCuriosityKnowledge() {
        // Roll strictly less than chance
        assertTrue(BaublesCurioLogic.shouldGrantCuriosityKnowledge(0.05f, 0.1f));
        assertTrue(BaublesCurioLogic.shouldGrantCuriosityKnowledge(0.0f, 0.01f));

        // Roll equals chance (false)
        assertFalse(BaublesCurioLogic.shouldGrantCuriosityKnowledge(0.1f, 0.1f));

        // Roll greater than chance
        assertFalse(BaublesCurioLogic.shouldGrantCuriosityKnowledge(0.5f, 0.1f));
        assertFalse(BaublesCurioLogic.shouldGrantCuriosityKnowledge(1.0f, 0.5f));
        assertFalse(BaublesCurioLogic.shouldGrantCuriosityKnowledge(0.0f, 0.0f));
    }

    @Test
    @DisplayName("Verify shouldPreventFatalDamage with fatal, exact, and non-fatal damage")
    public void testShouldPreventFatalDamage() {
        // Non-fatal damage
        assertFalse(BaublesCurioLogic.shouldPreventFatalDamage(20.0f, 5.0f));
        assertFalse(BaublesCurioLogic.shouldPreventFatalDamage(1.5f, 1.0f));

        // Exact fatal damage (health - damage == 0)
        assertTrue(BaublesCurioLogic.shouldPreventFatalDamage(10.0f, 10.0f));

        // Overkill damage (health - damage < 0)
        assertTrue(BaublesCurioLogic.shouldPreventFatalDamage(5.0f, 50.0f));

        // Zero or negative initial health
        assertTrue(BaublesCurioLogic.shouldPreventFatalDamage(0.0f, 1.0f));
        assertTrue(BaublesCurioLogic.shouldPreventFatalDamage(-1.0f, 0.0f));
    }

    @Test
    @DisplayName("Verify calculateUndyingReviveHealth with 20.0f, low health, zero, and negative")
    public void testCalculateUndyingReviveHealth() {
        // Normal max health (20.0f -> 2.0f)
        assertEquals(2.0f, BaublesCurioLogic.calculateUndyingReviveHealth(20.0f), 1e-4);

        // High max health (40.0f -> 4.0f)
        assertEquals(4.0f, BaublesCurioLogic.calculateUndyingReviveHealth(40.0f), 1e-4);

        // Low max health (5.0f * 0.1f = 0.5f -> clamped to min 1.0f)
        assertEquals(1.0f, BaublesCurioLogic.calculateUndyingReviveHealth(5.0f), 1e-4);

        // Exact 10.0f max health (10.0f * 0.1f = 1.0f)
        assertEquals(1.0f, BaublesCurioLogic.calculateUndyingReviveHealth(10.0f), 1e-4);

        // Zero max health (fallback 1.0f)
        assertEquals(1.0f, BaublesCurioLogic.calculateUndyingReviveHealth(0.0f), 1e-4);

        // Negative max health (fallback 1.0f)
        assertEquals(1.0f, BaublesCurioLogic.calculateUndyingReviveHealth(-10.0f), 1e-4);
    }

    @Test
    @DisplayName("Verify Voidseer warp bonus is 1")
    public void testGetVoidseerWarpBonus() {
        assertEquals(1, BaublesCurioLogic.getVoidseerWarpBonus());
    }

    @Test
    @DisplayName("Verify Undying regen and absorption durations")
    public void testUndyingDurationGetters() {
        assertEquals(900, BaublesCurioLogic.getUndyingRegenDurationTicks());
        assertEquals(200, BaublesCurioLogic.getUndyingAbsorptionDurationTicks());
    }
}
