package thaumcraft.common.world.aura.logic;

import java.util.Random;

public class FluxSaturationLogic {

    public enum FluxCondensationEvent {
        NONE,
        TAINT_FIBERS,
        FLUX_RIFT
    }

    /**
     * Calculates the tipping point where excess flux condenses into taint fibers.
     * Based on AuraHandler logic where flux spills over when it exceeds 75% of the base.
     */
    public static float getTaintFibersTippingPoint(float baseAura) {
        return baseAura * 0.75f;
    }

    /**
     * Calculates the tipping point where excess flux condenses into a flux rift.
     * Typically this requires significantly more flux than taint fibers.
     */
    public static float getFluxRiftTippingPoint(float baseAura) {
        return baseAura * 1.5f;
    }

    /**
     * Evaluates whether a flux condensation event should occur based on current flux, base aura, and randomness.
     */
    public static FluxCondensationEvent evaluateCondensationEvent(float flux, float baseAura, Random random) {
        float riftTippingPoint = getFluxRiftTippingPoint(baseAura);
        float taintTippingPoint = getTaintFibersTippingPoint(baseAura);

        if (flux >= riftTippingPoint) {
            // Chance to form a rift increases as flux exceeds the threshold
            float chance = (flux - riftTippingPoint) / riftTippingPoint; // e.g. at 200% threshold, 0.33 chance
            // Cap chance at 50% max to not spawn them too often
            chance = Math.min(chance, 0.5f);

            if (random.nextFloat() < chance) {
                return FluxCondensationEvent.FLUX_RIFT;
            }
        }

        if (flux >= taintTippingPoint) {
            // Chance to form taint fibers
            float chance = (flux - taintTippingPoint) / taintTippingPoint;
            // Cap chance at 80% max
            chance = Math.min(chance, 0.8f);

            if (random.nextFloat() < chance) {
                return FluxCondensationEvent.TAINT_FIBERS;
            }
        }

        return FluxCondensationEvent.NONE;
    }
}
