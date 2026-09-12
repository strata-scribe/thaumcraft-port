package thaumcraft.common.lib.alchemy;

public class BathSaltsLogic {

    /**
     * Calculates the duration (in ticks) that the water remains infused (purifying fluid).
     *
     * @param saltQuantity The number of bath salts applied.
     * @return The duration in ticks.
     */
    public static int calculateWaterInfusionDuration(int saltQuantity) {
        if (saltQuantity <= 0) {
            return 0;
        }
        // Base is 12000 ticks (10 minutes) per salt, capped at 72000 ticks (60 minutes).
        return Math.min(12000 * saltQuantity, 72000);
    }

    /**
     * Calculates the duration (in ticks) of the soothing potion effect.
     * As per lore, the more warp a thaumaturge suffers from, the shorter the duration.
     *
     * @param totalWarp The total amount of warp the player has.
     * @return The duration in ticks.
     */
    public static int calculateSoothingPotionDuration(int totalWarp) {
        // Base duration of 24000 ticks (20 minutes) for 0 warp.
        // Reduces by 100 ticks per warp point.
        int baseDuration = 24000;
        int duration = baseDuration - (totalWarp * 100);
        return Math.max(duration, 1200); // Minimum 1 minute (1200 ticks)
    }

    /**
     * Calculates the probability to inhibit a warp event while the soothing potion is active.
     *
     * @param totalWarp The total amount of warp the player has.
     * @return A probability between 0.0 and 1.0.
     */
    public static double calculateWarpEventInhibitionChance(int totalWarp) {
        if (totalWarp <= 10) {
            return 1.0;
        }
        // Drops by 0.5% per point of warp over 10.
        double chance = 1.0 - ((totalWarp - 10) * 0.005);
        return Math.max(chance, 0.5); // Minimum 50% inhibition chance
    }
}
