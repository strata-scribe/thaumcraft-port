package thaumcraft.common.entities.monster.tainted;

/**
 * Pure Java logic for evaluating passive mob exposure thresholds to taint and computing mutated health/attack stats.
 * Zero Minecraft/Forge dependencies for JUnit 5 testing.
 */
public final class TaintedAnimalMutationLogic {

    private TaintedAnimalMutationLogic() {
        // Utility class
    }

    /**
     * Checks if the animal's current taint exposure has reached or exceeded the mutation threshold.
     *
     * @param currentExposure The current accumulated taint exposure of the animal.
     * @param threshold The threshold required to trigger mutation.
     * @return true if currentExposure >= threshold, false otherwise.
     */
    public static boolean isMutationThresholdReached(float currentExposure, float threshold) {
        return currentExposure >= threshold;
    }

    /**
     * Calculates the increment in taint exposure based on severity and resistance.
     *
     * @param severity The severity of the taint in the environment (e.g., flux levels).
     * @param animalResistance The inherent resistance of the animal to taint (0.0 to 1.0, where 1.0 is immune).
     * @return The amount of exposure to add to the animal's current accumulation.
     */
    public static float calculateExposureIncrement(float severity, float animalResistance) {
        // Ensure resistance is clamped between 0 and 1
        float clampedResistance = Math.max(0.0f, Math.min(1.0f, animalResistance));
        return Math.max(0.0f, severity * (1.0f - clampedResistance));
    }

    /**
     * Computes the mutated health of the animal based on its base health and the taint severity multiplier.
     *
     * @param baseHealth The original maximum health of the passive mob.
     * @param severityMultiplier A multiplier based on how extreme the taint exposure was (e.g., 1.5x, 2.0x).
     * @param flatBonus A flat bonus added to the mutated health.
     * @return The new maximum health of the mutated animal.
     */
    public static float computeMutatedHealth(float baseHealth, float severityMultiplier, float flatBonus) {
        return (baseHealth * Math.max(1.0f, severityMultiplier)) + Math.max(0.0f, flatBonus);
    }

    /**
     * Computes the mutated attack damage of the animal. Many passive mobs gain attack damage when tainted.
     *
     * @param baseAttack The original attack damage of the mob (often 0 for passives).
     * @param baseMutantAttack The base attack damage a tainted version should have.
     * @param severityMultiplier A multiplier based on taint severity.
     * @return The new attack damage for the mutated animal.
     */
    public static float computeMutatedAttackDamage(float baseAttack, float baseMutantAttack, float severityMultiplier) {
        // Tainted animals generally get a completely new attack value rather than scaling an existing one,
        // but we can combine them.
        float startingAttack = Math.max(baseAttack, baseMutantAttack);
        return startingAttack * Math.max(1.0f, severityMultiplier);
    }
}
