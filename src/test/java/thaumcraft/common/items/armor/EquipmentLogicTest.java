package thaumcraft.common.items.armor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EquipmentLogic Comprehensive Unit Tests")
class EquipmentLogicTest {

    private static final double EPSILON = 1e-6;

    @Nested
    @DisplayName("Boots of the Traveller Tests")
    class BootsTravellerTests {

        @Test
        @DisplayName("Zero and negative damage return zero")
        void testZeroAndNegativeFallDamage() {
            assertEquals(0.0f, EquipmentLogic.calculateFallDamage(0.0f, true), EPSILON);
            assertEquals(0.0f, EquipmentLogic.calculateFallDamage(-5.0f, true), EPSILON);
            assertEquals(0.0f, EquipmentLogic.calculateFallDamage(0.0f, false), EPSILON);
            assertEquals(0.0f, EquipmentLogic.calculateFallDamage(-10.0f, false), EPSILON);
        }

        @Test
        @DisplayName("Uncharged boots do not reduce fall damage")
        void testUnchargedFallDamage() {
            assertEquals(10.0f, EquipmentLogic.calculateFallDamage(10.0f, false), EPSILON);
            assertEquals(25.5f, EquipmentLogic.calculateFallDamage(25.5f, false), EPSILON);
        }

        @Test
        @DisplayName("Piecewise fall damage formula with charge")
        void testFallDamageFormula() {
            // 1.0 / 2 - 1 = -0.5 < 1.0 -> 0.0
            assertEquals(0.0f, EquipmentLogic.calculateFallDamage(1.0f, true), EPSILON);
            // 2.5 / 2 - 1 = 0.25 < 1.0 -> 0.0
            assertEquals(0.0f, EquipmentLogic.calculateFallDamage(2.5f, true), EPSILON);
            // 3.9 / 2 - 1 = 0.95 < 1.0 -> 0.0
            assertEquals(0.0f, EquipmentLogic.calculateFallDamage(3.9f, true), EPSILON);
            // 4.0 / 2 - 1 = 1.0 >= 1.0 -> 1.0
            assertEquals(1.0f, EquipmentLogic.calculateFallDamage(4.0f, true), EPSILON);
            // 10.0 / 2 - 1 = 4.0 >= 1.0 -> 4.0
            assertEquals(4.0f, EquipmentLogic.calculateFallDamage(10.0f, true), EPSILON);
            // 20.0 / 2 - 1 = 9.0 (fatal fall survival)
            assertEquals(9.0f, EquipmentLogic.calculateFallDamage(20.0f, true), EPSILON);
            // 50.0 / 2 - 1 = 24.0
            assertEquals(24.0f, EquipmentLogic.calculateFallDamage(50.0f, true), EPSILON);
        }

        @Test
        @DisplayName("Extended fall damage with Boots and Ring of the Cloud")
        void testExtendedFallDamage() {
            // Both inactive
            assertEquals(15.0f, EquipmentLogic.calculateFallDamage(15.0f, false, false), EPSILON);

            // Boots only: 15 / 2 - 1 = 6.5
            assertEquals(6.5f, EquipmentLogic.calculateFallDamage(15.0f, true, false), EPSILON);

            // Cloud ring only: 15 / 3 - 2 = 3.0
            assertEquals(3.0f, EquipmentLogic.calculateFallDamage(15.0f, false, true), EPSILON);

            // Both boots and cloud ring: boots reduce to 6.5, ring reduces 6.5 / 3 - 2 = 0.166 < 1.0 -> 0.0
            assertEquals(0.0f, EquipmentLogic.calculateFallDamage(15.0f, true, true), EPSILON);

            // Fatal fall (30.0f): boots = 14.0f; ring on 14.0 = 14/3 - 2 = 2.666f
            assertEquals(14.0f / 3.0f - 2.0f, EquipmentLogic.calculateFallDamage(30.0f, true, true), 1e-4);
        }

        @Test
        @DisplayName("Jump boost velocity modification")
        void testJumpBoost() {
            assertEquals(0.2750000059604645, EquipmentLogic.calculateJumpBoost(true), EPSILON);
            assertEquals(0.0, EquipmentLogic.calculateJumpBoost(false), EPSILON);

            double baseMotion = 0.42;
            assertEquals(baseMotion + 0.2750000059604645, EquipmentLogic.calculateJumpVelocity(baseMotion, true, true), EPSILON);
            assertEquals(baseMotion, EquipmentLogic.calculateJumpVelocity(baseMotion, false, true), EPSILON);
            assertEquals(baseMotion, EquipmentLogic.calculateJumpVelocity(baseMotion, true, false), EPSILON);
        }

        @Test
        @DisplayName("Step height resolution under varied movement states")
        void testStepHeight() {
            float baseStep = 0.6f;

            // Active forward walking with charge -> 1.0f
            assertEquals(1.0f, EquipmentLogic.calculateStepHeight(baseStep, true, true, false, true), EPSILON);

            // Sneaking reverts step height to base to prevent accidental falls
            assertEquals(baseStep, EquipmentLogic.calculateStepHeight(baseStep, true, true, true, true), EPSILON);

            // Stationary (not moving forward) maintains base step height
            assertEquals(baseStep, EquipmentLogic.calculateStepHeight(baseStep, true, true, false, false), EPSILON);

            // Depleted charge maintains base step height
            assertEquals(baseStep, EquipmentLogic.calculateStepHeight(baseStep, true, false, false, true), EPSILON);

            // No boots maintains base step height
            assertEquals(baseStep, EquipmentLogic.calculateStepHeight(baseStep, false, true, false, true), EPSILON);

            // Active flag overload
            assertEquals(1.0f, EquipmentLogic.calculateStepHeight(baseStep, true), EPSILON);
            assertEquals(baseStep, EquipmentLogic.calculateStepHeight(baseStep, false), EPSILON);
        }

        @Test
        @DisplayName("Ground, water, and air speed modifiers")
        void testSpeedModifiers() {
            // Ground normal bonus = 0.05f
            assertEquals(0.05f, EquipmentLogic.calculateGroundSpeedBonus(true, false, true, true), EPSILON);

            // Ground water bonus = 0.0125f (0.05 / 4.0)
            assertEquals(0.0125f, EquipmentLogic.calculateGroundSpeedBonus(true, true, true, true), EPSILON);

            // In air -> 0.0f
            assertEquals(0.0f, EquipmentLogic.calculateGroundSpeedBonus(false, false, true, true), EPSILON);

            // No charge -> 0.0f
            assertEquals(0.0f, EquipmentLogic.calculateGroundSpeedBonus(true, false, true, false), EPSILON);

            // Air jump factors
            assertEquals(0.05f, EquipmentLogic.calculateAirJumpFactor(true, true), EPSILON);
            assertEquals(0.02f, EquipmentLogic.calculateAirJumpFactor(false, true), EPSILON);
            assertEquals(0.02f, EquipmentLogic.calculateAirJumpFactor(true, false), EPSILON);

            // Water movement bonus
            assertEquals(0.025f, EquipmentLogic.calculateWaterMovementBonus(true, true), EPSILON);
            assertEquals(0.0f, EquipmentLogic.calculateWaterMovementBonus(true, false), EPSILON);
        }
    }

    @Nested
    @DisplayName("Thaumostatic Harness Tests")
    class ThaumostaticHarnessTests {

        @Test
        @DisplayName("Vis drainage rates for flying, hovering, and inactive states")
        void testVisDrainRates() {
            // Flying: 2.5 charges / second
            assertEquals(2.5f, EquipmentLogic.calculateHarnessVisDrain(true, false), EPSILON);
            assertEquals(2.5f, EquipmentLogic.calculateHarnessVisDrain(true, true), EPSILON);

            // Hovering: 1.0 charge / second
            assertEquals(1.0f, EquipmentLogic.calculateHarnessVisDrain(false, true), EPSILON);

            // Inactive / Grounded: 0.0
            assertEquals(0.0f, EquipmentLogic.calculateHarnessVisDrain(false, false), EPSILON);

            // Per-tick rates (20 ticks/sec)
            assertEquals(2.5f / 20.0f, EquipmentLogic.calculateHarnessDrainPerTick(true, false), EPSILON);
            assertEquals(1.0f / 20.0f, EquipmentLogic.calculateHarnessDrainPerTick(false, true), EPSILON);
            assertEquals(0.0f, EquipmentLogic.calculateHarnessDrainPerTick(false, false), EPSILON);
        }

        @Test
        @DisplayName("Velocity acceleration and damping toward target speed")
        void testHarnessSpeedDamping() {
            // Acceleration from 0 toward 1.0: 0 + (1 - 0) * 0.15 = 0.15
            assertEquals(0.15, EquipmentLogic.calculateHarnessSpeed(0.0, 1.0), EPSILON);

            // Close to target snaps to target
            assertEquals(1.0, EquipmentLogic.calculateHarnessSpeed(0.9995, 1.0), EPSILON);

            // Deceleration from 1.0 toward 0.0: 1.0 - 0.15 = 0.85
            assertEquals(0.85, EquipmentLogic.calculateHarnessSpeed(1.0, 0.0), EPSILON);
        }

        @Test
        @DisplayName("Vertical flight thrust and descent limits")
        void testVerticalFlightThrust() {
            // Jump thrust (+0.15 up to 0.60)
            assertEquals(0.15, EquipmentLogic.calculateHarnessVerticalMotion(0.0, true, false, false), EPSILON);
            assertEquals(0.60, EquipmentLogic.calculateHarnessVerticalMotion(0.55, true, false, false), EPSILON);

            // Sneak descent (-0.15 down to -0.40)
            assertEquals(-0.15, EquipmentLogic.calculateHarnessVerticalMotion(0.0, false, true, false), EPSILON);
            assertEquals(-0.40, EquipmentLogic.calculateHarnessVerticalMotion(-0.35, false, true, false), EPSILON);

            // Jump + Sneak cancel to hover
            assertEquals(0.0, EquipmentLogic.calculateHarnessVerticalMotion(0.0, true, true, true), EPSILON);
            assertEquals(0.17, EquipmentLogic.calculateHarnessVerticalMotion(0.20, true, true, true), EPSILON);

            // Hover motion damping (* 0.85, snaps to 0 when < 0.01)
            assertEquals(0.085, EquipmentLogic.calculateHarnessVerticalMotion(0.10, false, false, true), EPSILON);
            assertEquals(0.0, EquipmentLogic.calculateHarnessVerticalMotion(0.008, false, false, true), EPSILON);
        }

        @Test
        @DisplayName("Parachute descent damping upon fuel depletion")
        void testFuelDepletedDescentDamping() {
            assertEquals(-0.30, EquipmentLogic.calculateHarnessDescentDamping(-0.80), EPSILON);
            assertEquals(-0.30, EquipmentLogic.calculateHarnessDescentDamping(-0.30), EPSILON);
            assertEquals(-0.10, EquipmentLogic.calculateHarnessDescentDamping(-0.10), EPSILON);
            assertEquals(0.20, EquipmentLogic.calculateHarnessDescentDamping(0.20), EPSILON);
        }
    }

    @Nested
    @DisplayName("Void Armor and Robes Tests")
    class VoidArmorTests {

        @Test
        @DisplayName("Passive tick-based self-repair every 20 ticks")
        void testPassiveRepair() {
            // Repairs 1 damage on tick 20, 40, 60
            assertEquals(9, EquipmentLogic.calculateVoidRepair(10, 20));
            assertEquals(8, EquipmentLogic.calculateVoidRepair(9, 40));

            // No repair on intermediate ticks
            assertEquals(10, EquipmentLogic.calculateVoidRepair(10, 15));
            assertEquals(10, EquipmentLogic.calculateVoidRepair(10, 21));

            // No repair on tick 0
            assertEquals(10, EquipmentLogic.calculateVoidRepair(10, 0));

            // Does not repair undamaged items (clamped at 0)
            assertEquals(0, EquipmentLogic.calculateVoidRepair(0, 20));
            assertEquals(0, EquipmentLogic.calculateVoidRepair(-5, 20));
        }

        @Test
        @DisplayName("Warping gear values")
        void testWarpingGear() {
            assertEquals(1, EquipmentLogic.getVoidArmorWarp());
            assertEquals(3, EquipmentLogic.getVoidRobeWarp());

            // Full Void Armor set (4 pieces) = 4 Warp
            assertEquals(4, EquipmentLogic.calculateWarp(false, 4));

            // Full Void Robe set (3 pieces) = 9 Warp
            assertEquals(9, EquipmentLogic.calculateWarp(true, 3));

            assertEquals(0, EquipmentLogic.calculateWarp(false, 0));
            assertEquals(0, EquipmentLogic.calculateWarp(true, -1));
        }

        @Test
        @DisplayName("Vis discount scaling per piece")
        void testVisDiscount() {
            assertEquals(5, EquipmentLogic.calculateVisDiscount(1));
            assertEquals(10, EquipmentLogic.calculateVisDiscount(2));
            assertEquals(15, EquipmentLogic.calculateVisDiscount(3));
            assertEquals(0, EquipmentLogic.calculateVisDiscount(0));
            assertEquals(0, EquipmentLogic.calculateVisDiscount(-2));

            // Capped at 100%
            assertEquals(100, EquipmentLogic.calculateVisDiscount(25));
        }
    }

    @Nested
    @DisplayName("Thaumium Fortress Armor and Masks Tests")
    class FortressArmorTests {

        @Test
        @DisplayName("Armor absorption ratios across damage types")
        void testAbsorptionRatios() {
            int totalArmor = 20;

            // NORMAL: 20 / 25.0 = 0.80
            assertEquals(0.80, EquipmentLogic.calculateArmorAbsorptionRatio(EquipmentLogic.DamageSourceType.NORMAL, totalArmor), EPSILON);

            // MAGIC: 20 / 35.0 = ~0.5714
            assertEquals(20.0 / 35.0, EquipmentLogic.calculateArmorAbsorptionRatio(EquipmentLogic.DamageSourceType.MAGIC, totalArmor), EPSILON);

            // FIRE & EXPLOSION: 20 / 20.0 = 1.0
            assertEquals(1.0, EquipmentLogic.calculateArmorAbsorptionRatio(EquipmentLogic.DamageSourceType.FIRE, totalArmor), EPSILON);
            assertEquals(1.0, EquipmentLogic.calculateArmorAbsorptionRatio(EquipmentLogic.DamageSourceType.EXPLOSION, totalArmor), EPSILON);

            // UNBLOCKABLE: 0.0
            assertEquals(0.0, EquipmentLogic.calculateArmorAbsorptionRatio(EquipmentLogic.DamageSourceType.UNBLOCKABLE, totalArmor), EPSILON);

            // Zero or negative armor yields zero ratio
            assertEquals(0.0, EquipmentLogic.calculateArmorAbsorptionRatio(EquipmentLogic.DamageSourceType.NORMAL, 0), EPSILON);
            assertEquals(0.0, EquipmentLogic.calculateArmorAbsorptionRatio(EquipmentLogic.DamageSourceType.NORMAL, -5), EPSILON);
        }

        @Test
        @DisplayName("Calculated absorption and penetrating damage")
        void testAbsorptionAndPenetration() {
            float incomingDamage = 10.0f;
            int totalArmor = 25; // ratio = 25 / 25 = 1.0 for NORMAL

            float absorbed = EquipmentLogic.calculateArmorAbsorption(EquipmentLogic.DamageSourceType.NORMAL, totalArmor, incomingDamage);
            assertEquals(10.0f, absorbed, EPSILON);

            float penetrating = EquipmentLogic.calculatePenetratingDamage(EquipmentLogic.DamageSourceType.NORMAL, totalArmor, incomingDamage);
            assertEquals(0.0f, penetrating, EPSILON);

            // Unblockable penetrates fully
            assertEquals(0.0f, EquipmentLogic.calculateArmorAbsorption(EquipmentLogic.DamageSourceType.UNBLOCKABLE, totalArmor, incomingDamage), EPSILON);
            assertEquals(10.0f, EquipmentLogic.calculatePenetratingDamage(EquipmentLogic.DamageSourceType.UNBLOCKABLE, totalArmor, incomingDamage), EPSILON);

            // Zero or negative damage
            assertEquals(0.0f, EquipmentLogic.calculateArmorAbsorption(EquipmentLogic.DamageSourceType.NORMAL, totalArmor, 0.0f), EPSILON);
            assertEquals(0.0f, EquipmentLogic.calculateArmorAbsorption(EquipmentLogic.DamageSourceType.NORMAL, totalArmor, -5.0f), EPSILON);
        }

        @Test
        @DisplayName("Fortress Armor set bonuses")
        void testFortressSetBonuses() {
            // 0 or 1 piece: no bonus
            assertEquals(0, EquipmentLogic.calculateFortressBonusArmor(0));
            assertEquals(0, EquipmentLogic.calculateFortressBonusArmor(1));
            assertEquals(0, EquipmentLogic.calculateFortressBonusToughness(1));

            // 2 pieces: +1 Armor, +1 Toughness
            assertEquals(1, EquipmentLogic.calculateFortressBonusArmor(2));
            assertEquals(1, EquipmentLogic.calculateFortressBonusToughness(2));

            // 3 pieces (full set): +2 Armor, +2 Toughness
            assertEquals(2, EquipmentLogic.calculateFortressBonusArmor(3));
            assertEquals(2, EquipmentLogic.calculateFortressBonusToughness(3));
        }

        @Test
        @DisplayName("Mask 0 (Grinning Devil) Warp dampening")
        void testMaskGrinningDevil() {
            // baseRoll 10, rand4 in [0..3] -> reduction in [2..5]
            assertEquals(8, EquipmentLogic.calculateMaskWarpReduction(10, 0)); // 10 - 2
            assertEquals(7, EquipmentLogic.calculateMaskWarpReduction(10, 1)); // 10 - 3
            assertEquals(6, EquipmentLogic.calculateMaskWarpReduction(10, 2)); // 10 - 4
            assertEquals(5, EquipmentLogic.calculateMaskWarpReduction(10, 3)); // 10 - 5

            // Clamped at minimum 0 (no negative effect rolls)
            assertEquals(0, EquipmentLogic.calculateMaskWarpReduction(3, 3)); // 3 - 5 = -2 -> 0
        }

        @Test
        @DisplayName("Mask 1 (Angry Ghost) Wither retaliation trigger")
        void testMaskAngryGhost() {
            // 5.0 damage: threshold = 5.0 / 10.0 = 0.50
            assertTrue(EquipmentLogic.shouldTriggerMaskWither(5.0f, 0.49f));
            assertFalse(EquipmentLogic.shouldTriggerMaskWither(5.0f, 0.51f));

            // 10.0+ damage guarantees 100% trigger
            assertTrue(EquipmentLogic.shouldTriggerMaskWither(10.0f, 0.99f));
            assertTrue(EquipmentLogic.shouldTriggerMaskWither(25.0f, 0.99f));

            // Zero or negative damage never triggers
            assertFalse(EquipmentLogic.shouldTriggerMaskWither(0.0f, 0.0f));
            assertFalse(EquipmentLogic.shouldTriggerMaskWither(-2.0f, 0.0f));
        }

        @Test
        @DisplayName("Mask 2 (Sipping Fiend) Lifesteal trigger and heal amount")
        void testMaskSippingFiend() {
            // 6.0 damage: threshold = 6.0 / 12.0 = 0.50
            assertTrue(EquipmentLogic.shouldTriggerMaskLifesteal(6.0f, 0.49f));
            assertFalse(EquipmentLogic.shouldTriggerMaskLifesteal(6.0f, 0.51f));

            // 12.0+ damage guarantees 100% trigger
            assertTrue(EquipmentLogic.shouldTriggerMaskLifesteal(12.0f, 0.99f));

            // Zero or negative damage never triggers
            assertFalse(EquipmentLogic.shouldTriggerMaskLifesteal(0.0f, 0.0f));
            assertFalse(EquipmentLogic.shouldTriggerMaskLifesteal(-1.0f, 0.0f));

            // Lifesteal heal amount is exactly 1.0 HP (half heart)
            assertEquals(1.0f, EquipmentLogic.calculateMaskLifestealAmount(), EPSILON);
        }
    }

    @Nested
    @DisplayName("Runic Shielding Tests")
    class RunicShieldingTests {

        @Test
        @DisplayName("Absorption when shield exceeds damage")
        void testShieldExceedsDamage() {
            EquipmentLogic.ShieldAbsorptionResult res = EquipmentLogic.calculateRunicAbsorption(10, 4.2f);
            // ceil(4.2) = 5 absorbed
            assertEquals(5, res.newShield());
            assertEquals(0.0f, res.penetratingDamage(), EPSILON);
        }

        @Test
        @DisplayName("Absorption when shield equals damage")
        void testShieldEqualsDamage() {
            EquipmentLogic.ShieldAbsorptionResult res = EquipmentLogic.calculateRunicAbsorption(5, 5.0f);
            assertEquals(0, res.newShield());
            assertEquals(0.0f, res.penetratingDamage(), EPSILON);
        }

        @Test
        @DisplayName("Spillover when damage exceeds shield")
        void testDamageExceedsShield() {
            EquipmentLogic.ShieldAbsorptionResult res = EquipmentLogic.calculateRunicAbsorption(5, 12.0f);
            assertEquals(0, res.newShield());
            assertEquals(7.0f, res.penetratingDamage(), EPSILON);
        }

        @Test
        @DisplayName("Absorption edge cases: zero shield, zero damage, negative values")
        void testShieldEdgeCases() {
            // Zero shield: all damage penetrates
            EquipmentLogic.ShieldAbsorptionResult resZeroShield = EquipmentLogic.calculateRunicAbsorption(0, 8.0f);
            assertEquals(0, resZeroShield.newShield());
            assertEquals(8.0f, resZeroShield.penetratingDamage(), EPSILON);

            // Zero damage: shield unchanged, 0 penetrating
            EquipmentLogic.ShieldAbsorptionResult resZeroDamage = EquipmentLogic.calculateRunicAbsorption(10, 0.0f);
            assertEquals(10, resZeroDamage.newShield());
            assertEquals(0.0f, resZeroDamage.penetratingDamage(), EPSILON);

            // Negative damage: ignored
            EquipmentLogic.ShieldAbsorptionResult resNegativeDamage = EquipmentLogic.calculateRunicAbsorption(10, -5.0f);
            assertEquals(10, resNegativeDamage.newShield());
            assertEquals(0.0f, resNegativeDamage.penetratingDamage(), EPSILON);
        }
    }
}
