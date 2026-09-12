package thaumcraft.common.lib;

/**
 * Pure Java logic helper for Warp Phantasm (Mind Spiders/Wisp) hallucination mob counts and despawn timeouts.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class WarpPhantasmLogic {

    /**
     * Calculates the number of phantom mobs (Mind Spiders/Wisps) to spawn based on total warp.
     *
     * @param totalWarp   The player's total warp.
     * @param randomValue A random value between 0.0 (inclusive) and 1.0 (exclusive).
     * @return The number of mobs to spawn.
     */
    public static int calculateMobCount(int totalWarp, double randomValue) {
        if (totalWarp <= 0) {
            return 0;
        }

        int baseCount = 1 + (totalWarp / 30);
        double extraChance = (totalWarp % 30) / 30.0;

        if (randomValue < extraChance) {
            baseCount++;
        }

        return baseCount;
    }

    /**
     * Calculates the despawn timeout for hallucinated phantom mobs based on total warp.
     * The timeout is in ticks. Minimum is 1200 ticks (60s), capped at 2400 ticks (120s).
     *
     * @param totalWarp The player's total warp.
     * @return The despawn timeout in ticks.
     */
    public static int calculateDespawnTimeoutTicks(int totalWarp) {
        if (totalWarp < 0) {
            return 1200;
        }

        int timeoutTicks = 1200 + (totalWarp * 12);

        return Math.min(2400, Math.max(1200, timeoutTicks));
    }
}
