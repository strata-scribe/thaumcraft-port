package thaumcraft.common.lib;

public class UnnaturalHungerLogic {

    /**
     * Calculates the exhaustion to add per tick due to unnatural hunger.
     * The drain increases with higher unnatural hunger levels.
     *
     * @param unnaturalHungerLevel The level of the unnatural hunger effect.
     * @return The amount of exhaustion to add per tick.
     */
    public static float calculateExhaustionPerTick(int unnaturalHungerLevel) {
        if (unnaturalHungerLevel <= 0) {
            return 0.0f;
        }
        // Example scaling: 0.025f exhaustion per level per tick.
        // In Minecraft, 4.0 exhaustion = 1 food level.
        // 0.025 * 20 ticks = 0.5 exhaustion per second per level.
        // It takes 8 seconds to lose 1 food level at level 1.
        return 0.025f * unnaturalHungerLevel;
    }

    /**
     * Calculates the new food level restored when eating rotten flesh while affected by unnatural hunger.
     * Rotten flesh usually restores 4 food (2 shanks).
     * With unnatural hunger, it restores more food.
     *
     * @param unnaturalHungerLevel The level of the unnatural hunger effect.
     * @param baseFood             The base food restored by the item (usually 4 for Rotten Flesh).
     * @return The modified food amount to restore.
     */
    public static int calculateRottenFleshFood(int unnaturalHungerLevel, int baseFood) {
        if (unnaturalHungerLevel <= 0) {
            return baseFood;
        }
        // Increase food restored by 50% per level, rounding down.
        // e.g. Lvl 1: 4 * 1.5 = 6
        // Lvl 2: 4 * 2.0 = 8
        return baseFood + (int)(baseFood * 0.5f * unnaturalHungerLevel);
    }

    /**
     * Calculates the new saturation restored when eating rotten flesh while affected by unnatural hunger.
     * Rotten flesh usually restores 0.8 saturation.
     * With unnatural hunger, it restores more saturation.
     *
     * @param unnaturalHungerLevel The level of the unnatural hunger effect.
     * @param baseSaturation       The base saturation restored by the item (usually 0.8f for Rotten Flesh).
     * @return The modified saturation amount to restore.
     */
    public static float calculateRottenFleshSaturation(int unnaturalHungerLevel, float baseSaturation) {
        if (unnaturalHungerLevel <= 0) {
            return baseSaturation;
        }
        // Increase saturation restored by 50% per level.
        // e.g. Lvl 1: 0.8 * 1.5 = 1.2
        return baseSaturation * (1.0f + 0.5f * unnaturalHungerLevel);
    }
}
