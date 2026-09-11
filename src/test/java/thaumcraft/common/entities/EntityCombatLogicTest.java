package thaumcraft.common.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive JUnit 5 test suite verifying EntityCombatLogic pure mathematical models,
 * state machine transitions, boundary conditions, and edge cases.
 */
public class EntityCombatLogicTest {

    // ==========================================
    // Pech Item Valuation Tests
    // ==========================================

    @Test
    @DisplayName("Ender Pearls always evaluate to fixed value of 15")
    public void testPechItemValuationEnderPearl() {
        assertEquals(15, EntityCombatLogic.calculatePechItemValue(0, true), "Ender Pearl must evaluate to 15");
        assertEquals(15, EntityCombatLogic.calculatePechItemValue(10, true), "Ender Pearl should override desire aspect");
        assertEquals(15, EntityCombatLogic.calculatePechItemValue(0, 1), "1 Ender Pearl overload must evaluate to 15");
        assertEquals(15, EntityCombatLogic.calculatePechItemValue(5, 2), "Multiple Ender Pearls overload must evaluate to 15");
    }

    @Test
    @DisplayName("Desire aspect evaluates to min(32, desire / 2) when desire > 1, otherwise 0")
    public void testPechItemValuationDesireAspect() {
        int[][] testCases = {
                {0, 0},
                {1, 0},
                {2, 1},
                {3, 1},
                {4, 2},
                {10, 5},
                {20, 10},
                {32, 16},
                {64, 32},
                {100, 32},
                {-5, 0}
        };
        for (int[] tc : testCases) {
            int desire = tc[0];
            int expected = tc[1];
            assertEquals(expected, EntityCombatLogic.calculatePechItemValue(desire, false),
                    "Desire aspect " + desire + " valuation mismatch");
            assertEquals(expected, EntityCombatLogic.calculatePechItemValue(desire, 0),
                    "Desire aspect " + desire + " overload valuation mismatch");
        }
    }


    // ==========================================
    // Pech Taming Probability Tests
    // ==========================================

    @Test
    @DisplayName("Pech taming roll: roll10 < value with 100% guarantee at value >= 10")
    public void testPechTamingProbability() {
        // Value 0: never tames
        for (int roll = 0; roll < 10; roll++) {
            assertFalse(EntityCombatLogic.isTamingSuccessful(0, roll), "Value 0 should never tame");
            assertFalse(EntityCombatLogic.rollPechTaming(0, roll), "Alias should return false for value 0");
        }

        // Value 5: rolls 0..4 succeed, rolls 5..9 fail
        for (int roll = 0; roll < 5; roll++) {
            assertTrue(EntityCombatLogic.isTamingSuccessful(5, roll), "Value 5 should tame for roll " + roll);
            assertTrue(EntityCombatLogic.rollPechTaming(5, roll), "Alias should tame for roll " + roll);
        }
        for (int roll = 5; roll < 10; roll++) {
            assertFalse(EntityCombatLogic.isTamingSuccessful(5, roll), "Value 5 should fail taming for roll " + roll);
        }

        // Value 10: 100% success for any roll in [0, 9]
        for (int roll = 0; roll < 10; roll++) {
            assertTrue(EntityCombatLogic.isTamingSuccessful(10, roll), "Value 10 should guarantee taming for roll " + roll);
        }

        // Value 15 (Ender Pearl): 100% success
        for (int roll = 0; roll < 10; roll++) {
            assertTrue(EntityCombatLogic.isTamingSuccessful(15, roll), "Value 15 should guarantee taming for roll " + roll);
        }

        // Negative values
        assertFalse(EntityCombatLogic.isTamingSuccessful(-5, 0), "Negative value should not tame");
    }


    // ==========================================
    // Pech Untame Risk Tests
    // ==========================================

    @Test
    @DisplayName("Pech untame risk roll: roll100 <= (value / 2)")
    public void testPechUntameRiskRoll() {
        // Value 10: risk is 5%
        assertTrue(EntityCombatLogic.shouldPechUntameAfterTrade(10, 5), "Roll 5 <= 5 should untame");
        assertTrue(EntityCombatLogic.shouldPechUntame(10, 5), "Alias should untame");
        assertFalse(EntityCombatLogic.shouldPechUntameAfterTrade(10, 6), "Roll 6 > 5 should not untame");

        // Value 32: risk is 16%
        assertTrue(EntityCombatLogic.shouldPechUntameAfterTrade(32, 16), "Roll 16 <= 16 should untame");
        assertFalse(EntityCombatLogic.shouldPechUntameAfterTrade(32, 17), "Roll 17 > 16 should not untame");

        // Value 0 or negative: 0% risk
        assertFalse(EntityCombatLogic.shouldPechUntameAfterTrade(0, 0), "Value 0 should have zero untame risk");
        assertFalse(EntityCombatLogic.shouldPechUntameAfterTrade(-5, 0), "Negative value should have zero untame risk");
    }


    // ==========================================
    // Pech Barter Chunk Size / Tier Tests
    // ==========================================

    @Test
    @DisplayName("Pech barter chunk size: Math.min(5, Math.max((value + 1) / 2, rollValue + 1))")
    public void testPechBarterChunkSizeAndTier() {
        // Value 1, roll 0: minChoice = 1, rollChoice = 1 => tier 1
        assertEquals(1, EntityCombatLogic.calculateBarterChunkSize(1, 0), "Value 1 should produce tier 1");
        assertEquals(1, EntityCombatLogic.calculateTradeChunkTier(1, 0), "Trade chunk tier for value 1 should be 1");

        // Value 3: minChoice = (3+1)/2 = 2
        assertEquals(2, EntityCombatLogic.calculateBarterChunkSize(3, 0), "Value 3 with roll 0 should produce tier 2");
        assertEquals(3, EntityCombatLogic.calculateBarterChunkSize(3, 2), "Value 3 with roll 2 should produce tier 3");

        // Value 10: minChoice = 5 => clamped to 5
        assertEquals(5, EntityCombatLogic.calculateBarterChunkSize(10, 0), "Value 10 should produce tier 5");
        assertEquals(5, EntityCombatLogic.calculateBarterChunkSize(32, 10), "Value 32 should clamp to tier 5");

        // Boundary / invalid values: returns 1
        assertEquals(1, EntityCombatLogic.calculateBarterChunkSize(0, 0), "Value 0 should return tier 1");
        assertEquals(1, EntityCombatLogic.calculateBarterChunkSize(-10, 0), "Negative value should return tier 1");
        assertEquals(1, EntityCombatLogic.calculateTradeChunkTier(0, 5), "Value 0 should return tier 1");
    }


    // ==========================================
    // Eldritch Guardian Combat Tests
    // ==========================================

    @Test
    @DisplayName("Eldritch Guardian halves magic damage (damage * 0.5f) while physical is untouched")
    public void testEldritchGuardianMagicDamageMitigation() {
        // Magic damage
        assertEquals(5.0f, EntityCombatLogic.calculateGuardianMagicDamage(10.0f, true), 0.001f,
                "10.0 magic damage should be halved to 5.0");
        assertEquals(3.75f, EntityCombatLogic.calculateGuardianDamage(7.5f, true), 0.001f,
                "7.5 magic damage should be halved to 3.75");
        assertEquals(0.0f, EntityCombatLogic.calculateGuardianMagicDamage(0.0f, true), 0.001f,
                "0 damage remains 0");
        assertEquals(0.0f, EntityCombatLogic.calculateGuardianMagicDamage(-5.0f, true), 0.001f,
                "Negative damage handled as 0");

        // Physical damage
        assertEquals(10.0f, EntityCombatLogic.calculateGuardianMagicDamage(10.0f, false), 0.001f,
                "Physical damage must remain unmitigated");
        assertEquals(25.0f, EntityCombatLogic.calculateGuardianDamage(25.0f, false), 0.001f,
                "Physical damage must remain unmitigated");
    }

    @Test
    @DisplayName("Eldritch Orb projectile splash damage: attackDamage * 0.666f")
    public void testEldritchOrbDamageCalculation() {
        // Base Guardian attack damage = 7.0f => 7.0 * 0.666 = 4.662f
        assertEquals(4.662f, EntityCombatLogic.calculateEldritchOrbDamage(7.0f), 0.001f,
                "Orb damage for 7.0 attack damage must be 4.662");
        assertEquals(6.66f, EntityCombatLogic.calculateEldritchOrbDamage(10.0f), 0.001f,
                "Orb damage for 10.0 attack damage must be 6.66");
        assertEquals(0.0f, EntityCombatLogic.calculateEldritchOrbDamage(0.0f), 0.001f,
                "Zero attack damage produces 0.0 orb damage");
        assertEquals(0.0f, EntityCombatLogic.calculateEldritchOrbDamage(-10.0f), 0.001f,
                "Negative attack damage produces 0.0 orb damage");
    }

    @Test
    @DisplayName("Eldritch Guardian initial outer dimension absorption (+25.0f)")
    public void testEldritchGuardianOuterDimensionAbsorption() {
        assertEquals(25.0f, EntityCombatLogic.calculateGuardianAbsorption(true), 0.001f,
                "Guardian in outer dimension must receive +25.0f absorption");
        assertEquals(0.0f, EntityCombatLogic.calculateGuardianAbsorption(false), 0.001f,
                "Guardian outside outer dimension must receive 0.0f absorption");
        assertEquals(25.0f, EntityCombatLogic.calculateGuardianAbsorption(50.0f, true), 0.001f,
                "Overload with baseHealth must return 25.0f in outer dimension");
        assertEquals(0.0f, EntityCombatLogic.calculateGuardianAbsorption(50.0f, false), 0.001f,
                "Overload with baseHealth must return 0.0f outside outer dimension");
    }

    @Test
    @DisplayName("Eldritch Guardian sonic screech: 15% trigger chance and 1..3 Temporary Warp")
    public void testEldritchGuardianSonicScreechAndWarp() {
        assertTrue(EntityCombatLogic.shouldGuardianScreech(0.10f), "Roll 0.10 < 0.15 should trigger screech");
        assertTrue(EntityCombatLogic.shouldGuardianScreech(0.00f), "Roll 0.00 < 0.15 should trigger screech");
        assertFalse(EntityCombatLogic.shouldGuardianScreech(0.15f), "Roll 0.15 should not trigger screech");
        assertFalse(EntityCombatLogic.shouldGuardianScreech(0.50f), "Roll 0.50 should not trigger screech");

        assertEquals(1, EntityCombatLogic.calculateScreechTemporaryWarp(0), "rand 0 should yield 1 warp");
        assertEquals(2, EntityCombatLogic.calculateScreechTemporaryWarp(1), "rand 1 should yield 2 warp");
        assertEquals(3, EntityCombatLogic.calculateScreechTemporaryWarp(2), "rand 2 should yield 3 warp");
        assertEquals(1, EntityCombatLogic.calculateScreechTemporaryWarp(3), "rand 3 should wrap to 1 warp");
    }


    // ==========================================
    // Mind Spider Hallucination Tests
    // ==========================================

    @Test
    @DisplayName("Mind Spider lifespan expiration at 1200 ticks (60 seconds)")
    public void testMindSpiderHarmlessLifespanDespawn() {
        int lifespan = 1200;

        assertFalse(EntityCombatLogic.isHarmlessExpired(0, lifespan), "Tick 0 should not be expired");
        assertFalse(EntityCombatLogic.isHarmlessExpired(600, lifespan), "Tick 600 should not be expired");
        assertFalse(EntityCombatLogic.isHarmlessExpired(1199, lifespan), "Tick 1199 should not be expired");
        assertFalse(EntityCombatLogic.shouldMindSpiderDespawn(1199, lifespan), "Alias should not be expired at 1199");

        assertTrue(EntityCombatLogic.isHarmlessExpired(1200, lifespan), "Tick 1200 should be expired");
        assertTrue(EntityCombatLogic.isMindSpiderExpired(1200, lifespan), "Alias should be expired at 1200");
        assertTrue(EntityCombatLogic.shouldMindSpiderDespawn(1200, lifespan), "Alias should despawn at 1200");
        assertTrue(EntityCombatLogic.isHarmlessExpired(1201, lifespan), "Tick 1201 should be expired");
    }

    @Test
    @DisplayName("Mind Spider harmless state suppresses attacks and zeroes damage")
    public void testMindSpiderHarmlessCombatSuppression() {
        // Harmless state
        assertFalse(EntityCombatLogic.canMindSpiderAttack(true), "Harmless spider must not attack");
        assertEquals(0.0f, EntityCombatLogic.calculateMindSpiderDamage(2.0f, true), 0.001f,
                "Harmless spider must deal 0.0 damage");

        // Hostile state
        assertTrue(EntityCombatLogic.canMindSpiderAttack(false), "Non-harmless spider can attack");
        assertEquals(2.0f, EntityCombatLogic.calculateMindSpiderDamage(2.0f, false), 0.001f,
                "Non-harmless spider deals normal base damage");
    }
}
