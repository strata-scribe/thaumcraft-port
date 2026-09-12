package thaumcraft.common.world.logic;

public class CrystalSeedGrowthLogic {

    /**
     * Calculates the number of ticks required for a crystal seed to advance a growth stage.
     * Growth speed is determined by the abundance of vis in the chunk.
     * Higher vis -> fewer ticks (faster growth).
     *
     * @param chunkVis The amount of vis available in the chunk.
     * @return The number of ticks required for the next growth stage. Returns -1 if growth is stalled (vis <= 0).
     */
    public int calculateGrowthTicks(double chunkVis) {
        if (chunkVis <= 0) {
            return -1; // Growth is stalled
        }

        // Base ticks for growth in a standard environment (e.g., 100 vis)
        double baseTicks = 12000.0; // E.g., ~10 minutes
        double standardVis = 100.0;

        // The growth ticks scale inversely with vis abundance
        // Ticks = baseTicks * (standardVis / chunkVis)
        double calculatedTicks = baseTicks * (standardVis / chunkVis);

        // Cap extreme values
        // Minimum ticks (fastest growth) - to prevent instant growth with extremely high vis
        int minTicks = 600; // E.g., ~30 seconds

        // Maximum ticks (slowest growth) - to provide a ceiling for very low vis (but > 0)
        int maxTicks = 48000; // E.g., ~40 minutes

        int finalTicks = (int) Math.round(calculatedTicks);

        if (finalTicks < minTicks) {
            return minTicks;
        } else if (finalTicks > maxTicks) {
            return maxTicks;
        }

        return finalTicks;
    }
}
