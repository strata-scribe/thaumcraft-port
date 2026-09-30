package thaumcraft.common.lib.research.theorycraft.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TheorycraftCardBonusLogicTest {

    @Test
    public void testApplyFocusBonus() {
        assertEquals(15, TheorycraftCardBonusLogic.applyFocusBonus(0));
        assertEquals(25, TheorycraftCardBonusLogic.applyFocusBonus(10));
        assertEquals(10, TheorycraftCardBonusLogic.applyFocusBonus(-5));
        assertEquals(115, TheorycraftCardBonusLogic.applyFocusBonus(100));
    }

    @Test
    public void testApplyCalibrateBonus() {
        assertEquals(15, TheorycraftCardBonusLogic.applyCalibrateBonus(0));
        assertEquals(35, TheorycraftCardBonusLogic.applyCalibrateBonus(20));
        assertEquals(0, TheorycraftCardBonusLogic.applyCalibrateBonus(-15));
        assertEquals(65, TheorycraftCardBonusLogic.applyCalibrateBonus(50));
    }

    @Test
    public void testApplyMeasureBonus() {
        assertEquals(15, TheorycraftCardBonusLogic.applyMeasureBonus(0));
        assertEquals(115, TheorycraftCardBonusLogic.applyMeasureBonus(100));
        assertEquals(5, TheorycraftCardBonusLogic.applyMeasureBonus(-10));
        assertEquals(45, TheorycraftCardBonusLogic.applyMeasureBonus(30));
    }

    @Test
    public void testApplySculptingBonus() {
        assertEquals(15, TheorycraftCardBonusLogic.applySculptingBonus(0));
        assertEquals(65, TheorycraftCardBonusLogic.applySculptingBonus(50));
        assertEquals(-5, TheorycraftCardBonusLogic.applySculptingBonus(-20));
        assertEquals(90, TheorycraftCardBonusLogic.applySculptingBonus(75));
    }

    @Test
    public void testCalculateBeaconPenalty() {
        assertEquals(1, TheorycraftCardBonusLogic.calculateBeaconPenalty(0));
        assertEquals(4, TheorycraftCardBonusLogic.calculateBeaconPenalty(3));
        assertEquals(0, TheorycraftCardBonusLogic.calculateBeaconPenalty(-1));
    }

    @Test
    public void testCalculateBeaconBonusDraws() {
        assertEquals(1, TheorycraftCardBonusLogic.calculateBeaconBonusDraws(0));
        assertEquals(3, TheorycraftCardBonusLogic.calculateBeaconBonusDraws(2));
        assertEquals(0, TheorycraftCardBonusLogic.calculateBeaconBonusDraws(-1));
    }

    @Test
    public void testCalculatePortalBonusDraws() {
        assertEquals(2, TheorycraftCardBonusLogic.calculatePortalBonusDraws(0));
        assertEquals(3, TheorycraftCardBonusLogic.calculatePortalBonusDraws(1));
        assertEquals(0, TheorycraftCardBonusLogic.calculatePortalBonusDraws(-2));
        assertEquals(5, TheorycraftCardBonusLogic.calculatePortalBonusDraws(3));
    }

    @Test
    public void testShouldGrantInspirationRefund() {
        // roll < chance -> true
        assertTrue(TheorycraftCardBonusLogic.shouldGrantInspirationRefund(0.1f, 0.33f));
        assertTrue(TheorycraftCardBonusLogic.shouldGrantInspirationRefund(0.0f, 0.5f));
        assertTrue(TheorycraftCardBonusLogic.shouldGrantInspirationRefund(0.3299f, 0.33f));

        // roll == chance -> false
        assertFalse(TheorycraftCardBonusLogic.shouldGrantInspirationRefund(0.33f, 0.33f));
        assertFalse(TheorycraftCardBonusLogic.shouldGrantInspirationRefund(0.5f, 0.5f));
        assertFalse(TheorycraftCardBonusLogic.shouldGrantInspirationRefund(0.0f, 0.0f));

        // roll > chance -> false
        assertFalse(TheorycraftCardBonusLogic.shouldGrantInspirationRefund(0.5f, 0.33f));
        assertFalse(TheorycraftCardBonusLogic.shouldGrantInspirationRefund(1.0f, 0.5f));
        assertFalse(TheorycraftCardBonusLogic.shouldGrantInspirationRefund(0.3301f, 0.33f));
    }

    @Test
    public void testCalculateTruthBonus() {
        // Zero values
        assertEquals(0, TheorycraftCardBonusLogic.calculateTruthBonus(0, 0));

        // Normal values
        assertEquals(25, TheorycraftCardBonusLogic.calculateTruthBonus(10, 15));
        assertEquals(18, TheorycraftCardBonusLogic.calculateTruthBonus(0, 18));

        // Negative rolled bonus clamped to 0
        assertEquals(10, TheorycraftCardBonusLogic.calculateTruthBonus(10, -5));
        assertEquals(0, TheorycraftCardBonusLogic.calculateTruthBonus(0, -20));
    }

    @Test
    public void testCalculatePortalEldritchBonus() {
        // Zero values
        assertEquals(0, TheorycraftCardBonusLogic.calculatePortalEldritchBonus(0, 0));

        // Normal values
        assertEquals(13, TheorycraftCardBonusLogic.calculatePortalEldritchBonus(5, 8));
        assertEquals(30, TheorycraftCardBonusLogic.calculatePortalEldritchBonus(20, 10));

        // Negative rolled bonus clamped to 0
        assertEquals(5, TheorycraftCardBonusLogic.calculatePortalEldritchBonus(5, -10));
        assertEquals(0, TheorycraftCardBonusLogic.calculatePortalEldritchBonus(0, -1));
    }
}
