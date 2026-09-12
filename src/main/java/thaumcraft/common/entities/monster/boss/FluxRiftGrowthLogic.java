package thaumcraft.common.entities.monster.boss;

public class FluxRiftGrowthLogic {

    /**
     * Calculates the new rift size after venting essentia.
     * Dirty essentia causes faster expansion.
     *
     * @param currentSize          The current size of the flux rift.
     * @param ventedEssentiaAmount The amount of essentia vented.
     * @param dirtyEssentiaRatio   The ratio of dirty essentia to total vented essentia (0.0 to 1.0).
     * @return The new size of the flux rift.
     */
    public static float calculateRiftExpansion(float currentSize, float ventedEssentiaAmount, float dirtyEssentiaRatio) {
        if (ventedEssentiaAmount <= 0) {
            return currentSize;
        }

        // Base expansion is proportional to amount vented
        float baseExpansion = ventedEssentiaAmount * 0.05f;

        // Dirty essentia adds an additional multiplier for expansion
        float dirtyMultiplier = 1.0f + (dirtyEssentiaRatio * 2.0f); // Up to 3x expansion rate for 100% dirty

        return currentSize + (baseExpansion * dirtyMultiplier);
    }

    /**
     * Calculates the new rift stability after venting essentia.
     * Dirty essentia causes faster degradation.
     *
     * @param currentStability     The current stability of the flux rift.
     * @param ventedEssentiaAmount The amount of essentia vented.
     * @param dirtyEssentiaRatio   The ratio of dirty essentia to total vented essentia (0.0 to 1.0).
     * @return The new stability of the flux rift.
     */
    public static float calculateStabilityDegradation(float currentStability, float ventedEssentiaAmount, float dirtyEssentiaRatio) {
        if (ventedEssentiaAmount <= 0) {
            return currentStability;
        }

        // Base degradation is proportional to amount vented
        float baseDegradation = ventedEssentiaAmount * 0.1f;

        // Dirty essentia drastically increases stability degradation
        float dirtyMultiplier = 1.0f + (dirtyEssentiaRatio * 4.0f); // Up to 5x degradation rate for 100% dirty

        float newStability = currentStability - (baseDegradation * dirtyMultiplier);
        return Math.max(0.0f, newStability); // Stability cannot go below 0
    }
}
