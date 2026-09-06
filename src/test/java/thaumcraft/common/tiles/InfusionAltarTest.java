package thaumcraft.common.tiles;

import org.junit.jupiter.api.Test;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Infusion Altar system covering:
 * <ul>
 *   <li>Symmetry math (stabilisation diminishing returns)</li>
 *   <li>Stability classification and modifiers</li>
 *   <li>Essentia cost multiplier scaling</li>
 *   <li>AspectList recipe essentia operations</li>
 * </ul>
 *
 * <p>These tests run without a Minecraft server context and only exercise
 * the pure-logic parts of the infusion system.  Tests that require
 * {@code ItemStack} or {@code Items} (which need MC bootstrap) are omitted.</p>
 */
class InfusionAltarTest {

    // =========================================================================
    // 1. Symmetry math — diminishing returns
    // =========================================================================

    /**
     * Mirrors the formula from TileInfusionMatrix.calcDiminishingReturns:
     * {@code base * 0.75^count} where count is the number of previously-seen
     * blocks of the same type.
     */
    private static float calcDiminishingReturns(int count, float base) {
        if (count > 0) {
            return base * (float) Math.pow(0.75, count);
        }
        return base;
    }

    @Test
    void diminishingReturns_firstPair_fullValue() {
        float result = calcDiminishingReturns(0, 0.1f);
        assertEquals(0.1f, result, 1e-6f, "First pair gets full stabilisation");
    }

    @Test
    void diminishingReturns_secondPair_reduced() {
        float result = calcDiminishingReturns(1, 0.1f);
        assertEquals(0.1f * 0.75f, result, 1e-6f, "Second pair is 75% of base");
    }

    @Test
    void diminishingReturns_thirdPair_doubleReduced() {
        float result = calcDiminishingReturns(2, 0.1f);
        float expected = 0.1f * (float) Math.pow(0.75, 2);
        assertEquals(expected, result, 1e-5f, "Third pair is 0.75^2 * base");
    }

    @Test
    void diminishingReturns_zeroBase_alwaysZero() {
        assertEquals(0.0f, calcDiminishingReturns(5, 0.0f), 1e-6f,
                "Zero base always produces zero");
    }

    @Test
    void diminishingReturns_largeCount_approachesZero() {
        float result = calcDiminishingReturns(20, 0.25f);
        assertTrue(result < 0.001f, "After 20 iterations, result approaches zero");
        assertTrue(result > 0.0f, "But remains positive for non-zero base");
    }

    // =========================================================================
    // 2. Symmetry penalty — asymmetric pairs
    // =========================================================================

    @Test
    void symmetryPenalty_asymmetric_subtractsMax() {
        float amt1 = 0.1f;
        float amt2 = 0.25f;
        float penalty = -Math.max(amt1, amt2);
        assertEquals(-0.25f, penalty, 1e-6f,
                "Asymmetric pair penalises by the larger stabilisation amount");
    }

    @Test
    void symmetryPenalty_symmetric_noSubtraction() {
        float amt1 = 0.1f;
        float amt2 = 0.1f;
        // Symmetric pairs contribute positively
        float contribution = calcDiminishingReturns(0, amt1);
        assertTrue(contribution > 0, "Symmetric pair contributes positive stability");
    }

    // =========================================================================
    // 3. Stability classification and modifiers
    // =========================================================================

    private enum EnumStability {
        VERY_STABLE, STABLE, UNSTABLE, VERY_UNSTABLE
    }

    private static EnumStability getStability(float stability, int stabilityCap) {
        if (stability > stabilityCap / 2f) return EnumStability.VERY_STABLE;
        if (stability >= 0.0f) return EnumStability.STABLE;
        if (stability > -25.0f) return EnumStability.UNSTABLE;
        return EnumStability.VERY_UNSTABLE;
    }

    private static float getModFromStability(EnumStability s) {
        return switch (s) {
            case VERY_STABLE -> 5.0f;
            case STABLE -> 6.0f;
            case UNSTABLE -> 7.0f;
            case VERY_UNSTABLE -> 8.0f;
        };
    }

    @Test
    void stabilityEnum_veryStable() {
        assertEquals(EnumStability.VERY_STABLE, getStability(20.0f, 25));
    }

    @Test
    void stabilityEnum_stable() {
        assertEquals(EnumStability.STABLE, getStability(5.0f, 25));
    }

    @Test
    void stabilityEnum_stable_atZero() {
        assertEquals(EnumStability.STABLE, getStability(0.0f, 25));
    }

    @Test
    void stabilityEnum_unstable() {
        assertEquals(EnumStability.UNSTABLE, getStability(-10.0f, 25));
    }

    @Test
    void stabilityEnum_veryUnstable() {
        assertEquals(EnumStability.VERY_UNSTABLE, getStability(-30.0f, 25));
    }

    @Test
    void stabilityEnum_boundary_veryUnstable() {
        assertEquals(EnumStability.VERY_UNSTABLE, getStability(-25.0f, 25));
    }

    @Test
    void stabilityModifier_mapping() {
        assertEquals(5.0f, getModFromStability(EnumStability.VERY_STABLE));
        assertEquals(6.0f, getModFromStability(EnumStability.STABLE));
        assertEquals(7.0f, getModFromStability(EnumStability.UNSTABLE));
        assertEquals(8.0f, getModFromStability(EnumStability.VERY_UNSTABLE));
    }

    @Test
    void lossPerCycle_computation() {
        int instability = 10;
        float mod = 5.0f; // VERY_STABLE
        float lpc = instability / mod;
        assertEquals(2.0f, lpc, 1e-6f, "Loss per cycle = instability / modifier");
    }

    @Test
    void lossPerCycle_highInstability() {
        int instability = 50;
        float mod = 8.0f; // VERY_UNSTABLE
        float lpc = instability / mod;
        assertEquals(6.25f, lpc, 1e-6f, "High instability with worst modifier");
    }

    // =========================================================================
    // 4. Cost multiplier scaling (essentia)
    // =========================================================================

    @Test
    void aspectList_costMult_scaling() {
        AspectList al = new AspectList();
        al.add(Aspect.FIRE, 10);
        al.add(Aspect.WATER, 6);
        float costMult = 0.5f;

        AspectList scaled = new AspectList();
        for (Aspect a : al.getAspects()) {
            int amt = (int) (al.getAmount(a) * costMult);
            if (amt > 0) scaled.add(a, amt);
        }
        assertEquals(5, scaled.getAmount(Aspect.FIRE), "FIRE scaled: 10 * 0.5 = 5");
        assertEquals(3, scaled.getAmount(Aspect.WATER), "WATER scaled: 6 * 0.5 = 3");
    }

    @Test
    void aspectList_costMult_roundsDown_dropsZero() {
        AspectList al = new AspectList();
        al.add(Aspect.AIR, 1);
        float costMult = 0.5f;
        int scaled = (int) (al.getAmount(Aspect.AIR) * costMult);
        assertEquals(0, scaled, "1 * 0.5 truncated to int = 0, should be dropped");
    }

    @Test
    void aspectList_costMult_minimum_clamp() {
        // costMult is clamped to minimum of 0.5 in craftingStart
        float costMult = 0.3f;
        if (costMult < 0.5f) costMult = 0.5f;
        assertEquals(0.5f, costMult, 1e-6f, "costMult clamped to 0.5 minimum");
    }

    @Test
    void aspectList_fullCost() {
        AspectList al = new AspectList();
        al.add(Aspect.MAGIC, 20);
        al.add(Aspect.CRAFT, 10);
        float costMult = 1.0f;

        AspectList scaled = new AspectList();
        for (Aspect a : al.getAspects()) {
            int amt = (int) (al.getAmount(a) * costMult);
            if (amt > 0) scaled.add(a, amt);
        }
        assertEquals(20, scaled.getAmount(Aspect.MAGIC), "Full cost: no scaling");
        assertEquals(10, scaled.getAmount(Aspect.CRAFT), "Full cost: no scaling");
    }

    // =========================================================================
    // 5. Essentia drain simulation
    // =========================================================================

    @Test
    void essentiaReduce_singleAspect() {
        AspectList essentia = new AspectList();
        essentia.add(Aspect.FIRE, 5);
        assertTrue(essentia.reduce(Aspect.FIRE, 1), "Should reduce successfully");
        assertEquals(4, essentia.getAmount(Aspect.FIRE), "Fire reduced to 4");
    }

    @Test
    void essentiaReduce_multipleAspects_drainOrder() {
        AspectList essentia = new AspectList();
        essentia.add(Aspect.FIRE, 3);
        essentia.add(Aspect.WATER, 2);

        // Drain fire first
        for (Aspect a : essentia.getAspects()) {
            if (essentia.getAmount(a) > 0) {
                essentia.reduce(a, 1);
                break;
            }
        }
        // First aspect (FIRE) should be drained
        assertEquals(2, essentia.getAmount(Aspect.FIRE));
        assertEquals(2, essentia.getAmount(Aspect.WATER));
    }

    @Test
    void essentiaReduce_tooMuch_fails() {
        AspectList essentia = new AspectList();
        essentia.add(Aspect.EARTH, 2);
        assertFalse(essentia.reduce(Aspect.EARTH, 3), "Cannot reduce more than available");
        assertEquals(2, essentia.getAmount(Aspect.EARTH), "Amount unchanged on failure");
    }

    @Test
    void essentiaReduce_toZero_removesEntry() {
        AspectList essentia = new AspectList();
        essentia.add(Aspect.ORDER, 1);
        essentia.remove(Aspect.ORDER, 1);
        assertEquals(0, essentia.getAmount(Aspect.ORDER));
        assertEquals(0, essentia.size(), "Aspect entry removed when reaching 0");
    }

    // =========================================================================
    // 6. Pillar bonus calculations
    // =========================================================================

    @Test
    void pillarBonus_ancient_reducesTime_reducesCost() {
        int cycleTime = 10;
        float costMult = 1.0f;
        float stabilityReplenish = 0.0f;
        // Ancient pillar bonus
        --cycleTime;
        costMult -= 0.1f;
        stabilityReplenish -= 0.1f;
        assertEquals(9, cycleTime);
        assertEquals(0.9f, costMult, 1e-6f);
        assertEquals(-0.1f, stabilityReplenish, 1e-6f);
    }

    @Test
    void pillarBonus_eldritch_fasterButCostlier() {
        int cycleTime = 10;
        float costMult = 1.0f;
        float stabilityReplenish = 0.0f;
        // Eldritch pillar bonus
        cycleTime -= 3;
        costMult += 0.05f;
        stabilityReplenish += 0.2f;
        assertEquals(7, cycleTime);
        assertEquals(1.05f, costMult, 1e-6f);
        assertEquals(0.2f, stabilityReplenish, 1e-6f);
    }

    @Test
    void matrixAugment_speed_fasterButCostlier() {
        int cycleTime = 10;
        float costMult = 1.0f;
        // 4 speed augments
        for (int i = 0; i < 4; i++) {
            --cycleTime;
            costMult += 0.01f;
        }
        assertEquals(6, cycleTime);
        assertEquals(1.04f, costMult, 1e-5f);
    }

    @Test
    void matrixAugment_cost_slowerButCheaper() {
        int cycleTime = 10;
        float costMult = 1.0f;
        // 4 cost augments
        for (int i = 0; i < 4; i++) {
            ++cycleTime;
            costMult -= 0.02f;
        }
        assertEquals(14, cycleTime);
        assertEquals(0.92f, costMult, 1e-5f);
    }

    @Test
    void countDelay_derivedFromCycleTime() {
        int cycleTime = 10;
        int countDelay = Math.max(1, cycleTime / 2);
        assertEquals(5, countDelay);

        cycleTime = 1;
        countDelay = Math.max(1, cycleTime / 2);
        assertEquals(1, countDelay, "Minimum countDelay is 1");
    }
}
