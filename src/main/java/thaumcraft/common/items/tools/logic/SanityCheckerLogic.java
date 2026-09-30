package thaumcraft.common.items.tools.logic;

/**
 * Pure, headless domain logic for the Sanity Checker.
 * Zero Minecraft server or registry dependencies.
 */
public class SanityCheckerLogic {

    /**
     * Calculates total warp value from permanent, normal, and temporary components.
     * Negative values are clamped to 0.
     */
    public static int calculateTotalWarp(int permWarp, int normalWarp, int tempWarp) {
        return Math.max(0, permWarp) + Math.max(0, normalWarp) + Math.max(0, tempWarp);
    }

    /**
     * Evaluates danger level category based on total warp.
     */
    public static String getWarpDangerLevel(int totalWarp) {
        if (totalWarp <= 0) return "NONE";
        if (totalWarp <= 10) return "LOW";
        if (totalWarp <= 25) return "MODERATE";
        if (totalWarp <= 50) return "HIGH";
        return "CRITICAL";
    }

    /**
     * Calculates current sanity percentage remaining relative to a maximum warp threshold.
     * Returns a float between 0.0f and 100.0f.
     */
    public static float calculateSanityPercentage(int totalWarp, int maxThreshold) {
        if (maxThreshold <= 0) return 0.0f;
        float pct = 100.0f - ((float) totalWarp * 100.0f / (float) maxThreshold);
        return Math.max(0.0f, Math.min(100.0f, pct));
    }

    /**
     * Determines whether audible heartbeat sound effects should play.
     */
    public static boolean shouldPlayHeartbeatSound(int totalWarp) {
        return totalWarp >= 25;
    }
}
