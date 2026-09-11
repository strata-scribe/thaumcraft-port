package thaumcraft.common.casters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FocusLogic Unit Tests")
class FocusLogicTest {

    private static final float EPSILON = 1.0e-5f;

    @Nested
    @DisplayName("Vis Cost & Economics Tests")
    class VisCostTests {

        @Test
        @DisplayName("Zero or negative complexity costs zero vis")
        void testZeroOrNegativeComplexity() {
            assertEquals(0.0f, FocusLogic.calculateVisCost(0), EPSILON);
            assertEquals(0.0f, FocusLogic.calculateVisCost(-5), EPSILON);
            assertEquals(0.0f, FocusLogic.calculateVisCost(0, 0.8f), EPSILON);
            assertEquals(0.0f, FocusLogic.calculateVisCost(-10, 0.5f), EPSILON);
        }

        @Test
        @DisplayName("Standard complexity vis cost calculations")
        void testStandardVisCost() {
            assertEquals(1.0f, FocusLogic.calculateVisCost(5), EPSILON);
            assertEquals(2.0f, FocusLogic.calculateVisCost(10), EPSILON);
            assertEquals(3.0f, FocusLogic.calculateVisCost(15), EPSILON);
            assertEquals(5.0f, FocusLogic.calculateVisCost(25), EPSILON);
            assertEquals(8.4f, FocusLogic.calculateVisCost(42), EPSILON);
        }

        @Test
        @DisplayName("Consumption discount modifier reduces vis cost")
        void testConsumptionModifier() {
            // 20 complexity = 4.0 base vis; 10% discount (0.9x) = 3.6 vis
            assertEquals(3.6f, FocusLogic.calculateVisCost(20, 0.9f), EPSILON);
            // 20% discount (0.8x) = 3.2 vis
            assertEquals(3.2f, FocusLogic.calculateVisCost(20, 0.8f), EPSILON);
            // Negative consumption modifier clamped to 0
            assertEquals(0.0f, FocusLogic.calculateVisCost(20, -0.5f), EPSILON);
        }
    }

    @Nested
    @DisplayName("Cooldown & Activation Time Tests")
    class CooldownTests {

        @Test
        @DisplayName("Zero or negative complexity gives minimum cooldown of 5 ticks")
        void testZeroOrNegativeComplexity() {
            assertEquals(5, FocusLogic.calculateActivationTime(0));
            assertEquals(5, FocusLogic.calculateActivationTime(-10));
            assertEquals(5, FocusLogic.calculateCooldownTicks(0));
            assertEquals(5, FocusLogic.calculateCooldownTicks(-1));
        }

        @Test
        @DisplayName("Activation time scaling with complexity")
        void testActivationTimeScaling() {
            // 4 -> 5 (0 * 1 = 0 -> max(5, 0))
            assertEquals(5, FocusLogic.calculateActivationTime(4));
            // 5 -> 5 (1 * 1 = 1 -> max(5, 1))
            assertEquals(5, FocusLogic.calculateActivationTime(5));
            // 8 -> 5 (1 * 2 = 2 -> max(5, 2))
            assertEquals(5, FocusLogic.calculateActivationTime(8));
            // 10 -> 5 (2 * 2 = 4 -> max(5, 4))
            assertEquals(5, FocusLogic.calculateActivationTime(10));
            // 12 -> 6 (2 * 3 = 6)
            assertEquals(6, FocusLogic.calculateActivationTime(12));
            // 15 -> 9 (3 * 3 = 9)
            assertEquals(9, FocusLogic.calculateActivationTime(15));
            // 20 -> 20 (4 * 5 = 20)
            assertEquals(20, FocusLogic.calculateActivationTime(20));
            // 25 -> 30 (5 * 6 = 30)
            assertEquals(30, FocusLogic.calculateActivationTime(25));
            // 40 -> 80 (8 * 10 = 80)
            assertEquals(80, FocusLogic.calculateActivationTime(40));

            // Cooldown ticks alias returns identical values
            assertEquals(9, FocusLogic.calculateCooldownTicks(15));
            assertEquals(30, FocusLogic.calculateCooldownTicks(25));
        }
    }

    @Nested
    @DisplayName("Complexity Aggregation & Color Blending Tests")
    class AggregationAndColorTests {

        @Test
        @DisplayName("Total complexity aggregates parent and child branches")
        void testTotalComplexity() {
            assertEquals(15, FocusLogic.calculateTotalComplexity(4, List.of(2, 5, 4)));
            assertEquals(4, FocusLogic.calculateTotalComplexity(4, null));
            assertEquals(4, FocusLogic.calculateTotalComplexity(4, Collections.emptyList()));
            // Negative or null child complexities are safely skipped
            assertEquals(7, FocusLogic.calculateTotalComplexity(4, java.util.Arrays.asList(3, -2, null)));
        }

        @Test
        @DisplayName("Color blending calculates arithmetic RGB mean")
        void testBlendEffectColors() {
            assertEquals(0xFFFFFF, FocusLogic.blendEffectColors(null));
            assertEquals(0xFFFFFF, FocusLogic.blendEffectColors(Collections.emptyList()));

            // Single color returns itself
            assertEquals(0xE05010, FocusLogic.blendEffectColors(List.of(0xE05010)));

            // Pure Red (0xFF0000) and Pure Blue (0x0000FF) blend to 0x7F007F
            int blended = FocusLogic.blendEffectColors(List.of(0xFF0000, 0x0000FF));
            int r = (blended >> 16) & 0xFF;
            int g = (blended >> 8) & 0xFF;
            int b = blended & 0xFF;
            assertEquals(127, r);
            assertEquals(0, g);
            assertEquals(127, b);
        }
    }

    @Nested
    @DisplayName("Medium Mechanics & Complexities Tests")
    class MediumTests {

        @Test
        @DisplayName("Static medium complexities match specification")
        void testStaticMediumComplexities() {
            assertEquals(2, FocusLogic.calculateTouchComplexity());
            assertEquals(5, FocusLogic.calculateBoltComplexity());
            assertEquals(4, FocusLogic.calculatePlanarComplexity());
            assertEquals(4, FocusLogic.calculateMineComplexity());
            assertEquals(4, FocusLogic.calculateSplitTargetComplexity());
            assertEquals(5, FocusLogic.calculateSplitTrajectoryComplexity());
            assertEquals(0.75f, FocusLogic.calculateSplitPowerMultiplier(), EPSILON);
        }

        @Test
        @DisplayName("Projectile complexity across speeds and options")
        void testProjectileComplexity() {
            // Option 0 (None)
            assertEquals(4, FocusLogic.calculateProjectileComplexity(1, 0));
            assertEquals(4, FocusLogic.calculateProjectileComplexity(2, 0));
            assertEquals(5, FocusLogic.calculateProjectileComplexity(3, 0));
            assertEquals(6, FocusLogic.calculateProjectileComplexity(5, 0));

            // Option 1 (Bouncy +3)
            assertEquals(7, FocusLogic.calculateProjectileComplexity(1, 1));
            assertEquals(8, FocusLogic.calculateProjectileComplexity(3, 1));

            // Option 2 (Seeking Hostile +5)
            assertEquals(9, FocusLogic.calculateProjectileComplexity(1, 2));

            // Option 3 (Seeking Friendly +5)
            assertEquals(11, FocusLogic.calculateProjectileComplexity(5, 3));
        }

        @Test
        @DisplayName("Projectile speed calculation")
        void testProjectileSpeed() {
            assertEquals(1.0f / 3.0f, FocusLogic.calculateProjectileSpeed(1), EPSILON);
            assertEquals(1.0f, FocusLogic.calculateProjectileSpeed(3), EPSILON);
            assertEquals(5.0f / 3.0f, FocusLogic.calculateProjectileSpeed(5), EPSILON);
            assertEquals(1.0f / 3.0f, FocusLogic.calculateProjectileSpeed(0), EPSILON); // clamped min 1
        }

        @Test
        @DisplayName("Scatter complexity across fork counts and cone angles")
        void testScatterComplexity() {
            // 2 * (2 - 10/45) = 3.55 -> 3
            assertEquals(3, FocusLogic.calculateScatterComplexity(2, 10));
            // 2 * (5 - 90/45) = 2 * 3 = 6
            assertEquals(6, FocusLogic.calculateScatterComplexity(5, 90));
            // 2 * (10 - 360/45) = 2 * 2 = 4
            assertEquals(4, FocusLogic.calculateScatterComplexity(10, 360));
            // clamped to min 2
            assertEquals(2, FocusLogic.calculateScatterComplexity(2, 180));
        }

        @Test
        @DisplayName("Scatter power multiplier calculation")
        void testScatterPowerMultiplier() {
            assertEquals(1.0f, FocusLogic.calculateScatterPowerMultiplier(2), EPSILON);
            assertEquals(0.5f, FocusLogic.calculateScatterPowerMultiplier(4), EPSILON);
            assertEquals(0.2f, FocusLogic.calculateScatterPowerMultiplier(10), EPSILON);
            assertEquals(1.0f, FocusLogic.calculateScatterPowerMultiplier(0), EPSILON);
        }
    }

    @Nested
    @DisplayName("Focus Effect Formulas Tests")
    class EffectTests {

        @Test
        @DisplayName("Fire Effect: complexity, damage, and burn duration")
        void testFireEffect() {
            assertEquals(8, FocusLogic.calculateFireComplexity(3, 2));
            assertEquals(0, FocusLogic.calculateFireComplexity(-1, -1));

            // Damage: (3 + power) * finalPower
            assertEquals(6.0f, FocusLogic.calculateFireDamage(3, 1.0f), EPSILON);
            assertEquals(9.0f, FocusLogic.calculateFireDamage(3, 1.5f), EPSILON);
            assertEquals(0.0f, FocusLogic.calculateFireDamage(3, 0.0f), EPSILON);

            // Burn: (1 + dur^2) * finalPower
            assertEquals(10.0f, FocusLogic.calculateFireBurnDuration(3, 1.0f), EPSILON);
            assertEquals(15.0f, FocusLogic.calculateFireBurnDuration(3, 1.5f), EPSILON);
            assertEquals(1.0f, FocusLogic.calculateFireBurnDuration(0, 1.0f), EPSILON);
        }

        @Test
        @DisplayName("Frost Effect: complexity, damage, slowness duration/potency, and freeze radius")
        void testFrostEffect() {
            assertEquals(8, FocusLogic.calculateFrostComplexity(2, 4));

            // Damage: (3 + power) * finalPower
            assertEquals(5.0f, FocusLogic.calculateFrostDamage(2, 1.0f), EPSILON);
            assertEquals(7.5f, FocusLogic.calculateFrostDamage(2, 1.5f), EPSILON);

            // Slowness Duration: 20 * dur
            assertEquals(60, FocusLogic.calculateFrostSlownessDuration(3));

            // Slowness Potency: 1.0 + (power * finalPower / 3.0)
            assertEquals(2, FocusLogic.calculateFrostSlownessPotency(3, 1.0f));
            assertEquals(3, FocusLogic.calculateFrostSlownessPotency(5, 1.5f));

            // Freeze Radius: min(16, 2 * power * finalPower)
            assertEquals(6.0f, FocusLogic.calculateFrostFreezeRadius(2, 1.5f), EPSILON);
            assertEquals(16.0f, FocusLogic.calculateFrostFreezeRadius(10, 2.0f), EPSILON);
            assertEquals(16.0f, FocusLogic.calculateFrostedIceRadius(10, 2.0f), EPSILON);
        }

        @Test
        @DisplayName("Earth Effect: complexity, damage, and block break hardness threshold")
        void testEarthEffect() {
            assertEquals(9, FocusLogic.calculateEarthComplexity(3));

            // Damage: 2 * power * finalPower
            assertEquals(6.0f, FocusLogic.calculateEarthDamage(3, 1.0f), EPSILON);
            assertEquals(9.0f, FocusLogic.calculateEarthDamage(3, 1.5f), EPSILON);

            // Max Break Hardness: damage / 25.0
            assertEquals(9.0f / 25.0f, FocusLogic.calculateEarthMaxBreakHardness(3, 1.5f), EPSILON);
        }

        @Test
        @DisplayName("Air Effect: complexity, damage, and knockback")
        void testAirEffect() {
            assertEquals(6, FocusLogic.calculateAirComplexity(3));

            // Damage: (1 + power) * finalPower
            assertEquals(4.0f, FocusLogic.calculateAirDamage(3, 1.0f), EPSILON);
            assertEquals(8.0f, FocusLogic.calculateAirDamage(3, 2.0f), EPSILON);

            // Knockback: damage * 0.25
            assertEquals(2.0f, FocusLogic.calculateAirKnockback(3, 2.0f), EPSILON);
        }

        @Test
        @DisplayName("Curse Effect: complexity, damage, debuff duration/potency, and sap radius")
        void testCurseEffect() {
            assertEquals(11, FocusLogic.calculateCurseComplexity(2, 5));

            // Damage: (1 + power) * finalPower
            assertEquals(3.0f, FocusLogic.calculateCurseDamage(2, 1.0f), EPSILON);

            // Debuff Duration: 20 * duration
            assertEquals(80, FocusLogic.calculateCurseDebuffDuration(4));

            // Debuff Potency: power * finalPower / 2.0
            assertEquals(2, FocusLogic.calculateCurseDebuffPotency(4, 1.0f));

            // Sap Radius: min(8, 1.5 * power * finalPower)
            assertEquals(6.0f, FocusLogic.calculateCurseSapRadius(2, 2.0f), EPSILON);
            assertEquals(8.0f, FocusLogic.calculateCurseSapRadius(5, 2.0f), EPSILON); // capped at 8.0
        }

        @Test
        @DisplayName("Flux Effect: complexity and pure magic damage")
        void testFluxEffect() {
            assertEquals(12, FocusLogic.calculateFluxComplexity(4));

            // Damage: (3 + power) * finalPower
            assertEquals(7.0f, FocusLogic.calculateFluxDamage(4, 1.0f), EPSILON);
            assertEquals(10.5f, FocusLogic.calculateFluxDamage(4, 1.5f), EPSILON);
        }

        @Test
        @DisplayName("Heal Effect: complexity, heal amount, and undead damage bonus")
        void testHealEffect() {
            assertEquals(12, FocusLogic.calculateHealComplexity(3));

            // Heal: power * finalPower
            assertEquals(4.5f, FocusLogic.calculateHealAmount(3, 1.5f), EPSILON);

            // Undead damage: power * finalPower * 1.5
            assertEquals(6.75f, FocusLogic.calculateHealUndeadDamage(3, 1.5f), EPSILON);
        }

        @Test
        @DisplayName("Break Effect: complexity, hardness durability, delay ticks, and vis factor")
        void testBreakEffect() {
            // Complexity: power * 3 + silk * 4 + ((fort > 0) ? (fort + 1) * 3 : 0)
            assertEquals(9, FocusLogic.calculateBreakComplexity(3, 0, 0));
            assertEquals(13, FocusLogic.calculateBreakComplexity(3, 1, 0));
            assertEquals(18, FocusLogic.calculateBreakComplexity(3, 0, 2)); // 9 + (3 * 3) = 18
            assertEquals(25, FocusLogic.calculateBreakComplexity(3, 1, 3)); // 9 + 4 + (4 * 3) = 25

            // Durability: sqrt(hardness * 100)
            assertEquals(10.0f, FocusLogic.calculateBreakDurability(1.0f), EPSILON);
            assertEquals(0.0f, FocusLogic.calculateBreakDurability(0.0f), EPSILON);
            assertEquals(20.0f, FocusLogic.calculateBreakDurability(4.0f), EPSILON);

            // Delay ticks: (int)(durability / strength / 3.0 * num)
            assertEquals(2, FocusLogic.calculateBreakDelayTicks(18.0f, 3.0f, 1));
            assertEquals(4, FocusLogic.calculateBreakDelayTicks(18.0f, 3.0f, 2));
            assertEquals(0, FocusLogic.calculateBreakDelayTicks(18.0f, 0.0f, 1)); // zero strength
            assertEquals(2, FocusLogic.calculateBreakDelay(1.0f, 3.0f, 2)); // durability = 10 -> (10/3/3 * 2) = 2

            // Vis factor: 0.25 + (silk ? 0.25 : 0) + fortune * 0.1
            assertEquals(0.25f, FocusLogic.calculateBreakVisFactor(0, false), EPSILON);
            assertEquals(0.50f, FocusLogic.calculateBreakVisFactor(0, true), EPSILON);
            assertEquals(0.55f, FocusLogic.calculateBreakVisFactor(3, false), EPSILON);
            assertEquals(0.80f, FocusLogic.calculateBreakVisFactor(3, true), EPSILON);
        }

        @Test
        @DisplayName("Generic calculateEffectDamage contract method")
        void testGenericEffectDamage() {
            assertEquals(7.0f, FocusLogic.calculateEffectDamage("FIRE", 4, 1.0f), EPSILON);
            assertEquals(7.0f, FocusLogic.calculateEffectDamage("frost", 4, 1.0f), EPSILON);
            assertEquals(8.0f, FocusLogic.calculateEffectDamage("EARTH", 4, 1.0f), EPSILON);
            assertEquals(5.0f, FocusLogic.calculateEffectDamage("air", 4, 1.0f), EPSILON);
            assertEquals(5.0f, FocusLogic.calculateEffectDamage("CURSE", 4, 1.0f), EPSILON);
            assertEquals(7.0f, FocusLogic.calculateEffectDamage("flux", 4, 1.0f), EPSILON);
            assertEquals(4.0f, FocusLogic.calculateEffectDamage("heal", 4, 1.0f), EPSILON);
            assertEquals(6.0f, FocusLogic.calculateEffectDamage("heal_undead", 4, 1.0f), EPSILON);
            assertEquals(0.0f, FocusLogic.calculateEffectDamage("UNKNOWN", 4, 1.0f), EPSILON);
            assertEquals(0.0f, FocusLogic.calculateEffectDamage(null, 4, 1.0f), EPSILON);
        }
    }

    @Nested
    @DisplayName("Trajectory Dispersion Math Tests")
    class DispersionTests {

        @Test
        @DisplayName("calculateScatterVector preserves normalization and produces valid directions")
        void testScatterVectorNormalization() {
            double[] zeroPerturb = FocusLogic.calculateScatterVector(0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 45);
            assertEquals(0.0, zeroPerturb[0], 1.0e-4);
            assertEquals(1.0, zeroPerturb[1], 1.0e-4);
            assertEquals(0.0, zeroPerturb[2], 1.0e-4);

            double[] perturbed = FocusLogic.calculateScatterVector(1.0, 0.0, 0.0, 0.5, 0.5, 0.5, 60);
            double length = Math.sqrt(perturbed[0] * perturbed[0] + perturbed[1] * perturbed[1] + perturbed[2] * perturbed[2]);
            assertEquals(1.0, length, 1.0e-4);
        }
    }

    @Nested
    @DisplayName("Node Chain Validation Tests")
    class NodeValidationTests {

        @Test
        @DisplayName("Valid node chains pass supply/demand matching")
        void testValidChains() {
            // Root -> Touch -> Fire
            List<FocusLogic.NodeInfo> chain1 = List.of(
                    new FocusLogic.NodeInfo("ROOT", null, "TRAJECTORY, TARGET"),
                    new FocusLogic.NodeInfo("TOUCH", "TRAJECTORY", "TARGET"),
                    new FocusLogic.NodeInfo("FIRE", "TARGET", null)
            );
            assertTrue(FocusLogic.validateNodeChain(chain1));

            // Root -> Scatter -> Bolt -> Frost
            List<FocusLogic.NodeInfo> chain2 = List.of(
                    new FocusLogic.NodeInfo("ROOT", null, "TRAJECTORY, TARGET"),
                    new FocusLogic.NodeInfo("SCATTER", "TRAJECTORY", "TRAJECTORY"),
                    new FocusLogic.NodeInfo("BOLT", "TRAJECTORY", "TARGET"),
                    new FocusLogic.NodeInfo("FROST", "TARGET", null)
            );
            assertTrue(FocusLogic.validateNodeChain(chain2));
        }

        @Test
        @DisplayName("Mismatched supply/demand rejects invalid node chains")
        void testInvalidChains() {
            // Null or empty chain
            assertFalse(FocusLogic.validateNodeChain(null));
            assertFalse(FocusLogic.validateNodeChain(Collections.emptyList()));

            // Touch requires TRAJECTORY, but preceded by an Effect that outputs null
            List<FocusLogic.NodeInfo> brokenChain = List.of(
                    new FocusLogic.NodeInfo("ROOT", null, "TARGET"),
                    new FocusLogic.NodeInfo("FIRE", "TARGET", null),
                    new FocusLogic.NodeInfo("TOUCH", "TRAJECTORY", "TARGET")
            );
            assertFalse(FocusLogic.validateNodeChain(brokenChain));

            // Touch outputs TARGET, followed by Scatter which requires TRAJECTORY
            List<FocusLogic.NodeInfo> mismatched = List.of(
                    new FocusLogic.NodeInfo("ROOT", null, "TRAJECTORY"),
                    new FocusLogic.NodeInfo("TOUCH", "TRAJECTORY", "TARGET"),
                    new FocusLogic.NodeInfo("SCATTER", "TRAJECTORY", "TRAJECTORY")
            );
            assertFalse(FocusLogic.validateNodeChain(mismatched));
        }
    }
}
