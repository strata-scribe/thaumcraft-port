package thaumcraft.common.world.aura.logic;

public class TaintedSoilDecayLogic {

    public static final int MAX_PROGRESSION = 100;

    /**
     * Calculates the new decay progression for Tainted Soil.
     *
     * @param currentProgression The current decay progression (0 to MAX_PROGRESSION).
     * @param auraFluxLevel The amount of flux in the local aura chunk.
     * @param distanceToSeed The distance to the nearest taint seed block.
     * @return The updated progression value, capped at MAX_PROGRESSION.
     */
    public static int calculateDecayProgression(int currentProgression, float auraFluxLevel, double distanceToSeed) {
        if (currentProgression >= MAX_PROGRESSION) {
            return MAX_PROGRESSION;
        }

        int increment = 1;

        // High flux speeds up decay
        if (auraFluxLevel > 50.0f) {
            increment += 1;
        }
        if (auraFluxLevel > 150.0f) {
            increment += 1;
        }

        // Being close to a taint seed speeds up decay
        if (distanceToSeed <= 8.0) {
            increment += 1;
        }
        if (distanceToSeed <= 4.0) {
            increment += 1;
        }

        return Math.min(MAX_PROGRESSION, currentProgression + increment);
    }

    /**
     * Calculates the probability (0.0 to 1.0) of releasing spores when the block is broken.
     *
     * @param currentProgression The current decay progression.
     * @param silkTouchLevel The level of the Silk Touch enchantment.
     * @return The chance of spore release.
     */
    public static float calculateSporeReleaseChance(int currentProgression, int silkTouchLevel) {
        if (silkTouchLevel > 0) {
            return 0.0f; // Silk Touch completely prevents spore release
        }

        // Base chance scales with progression.
        // Max progression gives a base 50% chance.
        float baseChance = (currentProgression / (float) MAX_PROGRESSION) * 0.50f;

        // Ensure chance stays bounded between 0.0 and 1.0
        return Math.max(0.0f, Math.min(1.0f, baseChance));
    }
}
