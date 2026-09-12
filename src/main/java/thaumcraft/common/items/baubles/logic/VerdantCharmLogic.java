package thaumcraft.common.items.baubles.logic;

public class VerdantCharmLogic {

    public static final int BASE_HEAL_INTERVAL = 100;
    public static final int MIN_HEAL_INTERVAL = 20;

    /**
     * Calculates the interval in ticks between healing pulses.
     * A lower health percentage results in a faster (shorter) pulse interval.
     *
     * @param currentHealth The current health of the entity.
     * @param maxHealth     The maximum health of the entity.
     * @return The interval in ticks.
     */
    public static int calculateHealingPulseInterval(float currentHealth, float maxHealth) {
        if (currentHealth >= maxHealth || maxHealth <= 0) {
            return BASE_HEAL_INTERVAL;
        }

        float healthRatio = Math.max(0.0f, Math.min(1.0f, currentHealth / maxHealth));
        int intervalRange = BASE_HEAL_INTERVAL - MIN_HEAL_INTERVAL;

        return MIN_HEAL_INTERVAL + Math.round(intervalRange * healthRatio);
    }

    /**
     * Calculates the conversion ratio for food saturation restoration.
     * Returns a ratio that scales based on the current food level, providing
     * better conversion efficiency when the player is more hungry.
     *
     * @param currentFoodLevel The current food level (typically 0-20).
     * @param maxFoodLevel     The maximum food level (typically 20).
     * @return The saturation conversion ratio.
     */
    public static float calculateSaturationConversionRatio(int currentFoodLevel, int maxFoodLevel) {
        if (maxFoodLevel <= 0) {
            return 0.0f;
        }
        float foodRatio = Math.max(0.0f, Math.min(1.0f, (float) currentFoodLevel / maxFoodLevel));

        // Base conversion ratio is 0.5, scales up to 1.5 when completely starving.
        return 0.5f + (1.0f * (1.0f - foodRatio));
    }
}
