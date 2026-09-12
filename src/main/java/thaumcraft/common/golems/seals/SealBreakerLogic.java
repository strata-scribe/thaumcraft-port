package thaumcraft.common.golems.seals;

/**
 * Pure Java logic class for Golem Breaker Seals.
 * Decoupled from Minecraft/Forge to allow for fast, reliable unit testing.
 */
public class SealBreakerLogic {

    /**
     * Calculates the time in ticks it takes for a golem to break a block.
     *
     * @param blockHardness The hardness of the block to break. If negative, the block is unbreakable.
     * @param golemStrength The strength value of the golem.
     * @param toolHarvestLevel The harvest level of the tool used by the golem.
     * @return The duration in ticks to break the block, or -1 if the block is unbreakable.
     */
    public static int calculateBreakDuration(float blockHardness, double golemStrength, int toolHarvestLevel) {
        if (blockHardness < 0) {
            return -1; // Unbreakable block
        }

        if (blockHardness == 0) {
            return 1; // Instant break, but takes at least 1 tick
        }

        // Base time is block hardness * 30 ticks
        double baseTime = blockHardness * 30.0;

        // Reduce time based on golem strength. Each point of strength reduces time by 10%, max 50% reduction
        double strengthMultiplier = 1.0 - Math.min(0.5, golemStrength * 0.1);

        // Reduce time based on tool harvest level. Each level halves the time
        double toolMultiplier = Math.pow(0.5, toolHarvestLevel);

        // Calculate total time, minimum 1 tick
        int ticks = (int) Math.max(1, Math.round(baseTime * strengthMultiplier * toolMultiplier));

        return ticks;
    }
}
