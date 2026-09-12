package thaumcraft.common.casters;

public class CasterReticleLogic {

    /**
     * Compute charge progress percentage.
     * Capped between 0.0 and 1.0.
     * @param currentChargeTicks The current charge ticks.
     * @param maxChargeTicks The maximum charge ticks.
     * @return The charge progress as a percentage from 0.0 to 1.0.
     */
    public static double computeChargeProgress(int currentChargeTicks, int maxChargeTicks) {
        if (maxChargeTicks <= 0) return 0.0;
        double progress = (double) currentChargeTicks / maxChargeTicks;
        return Math.max(0.0, Math.min(1.0, progress));
    }

    /**
     * Calculate the bloom radius based on charge progress.
     * Decreases as charge progresses (from maxBloom to 0, added to baseRadius).
     * @param chargeProgress The current charge progress (0.0 to 1.0).
     * @param baseRadius The base radius of the reticle.
     * @param maxBloom The maximum bloom added when progress is 0.
     * @return The calculated bloom radius.
     */
    public static double computeBloomRadius(double chargeProgress, double baseRadius, double maxBloom) {
        chargeProgress = Math.max(0.0, Math.min(1.0, chargeProgress));
        return baseRadius + (maxBloom * (1.0 - chargeProgress));
    }

    /**
     * Blend aspect colors by averaging RGB values.
     * @param aspectColors A variable number of RGB colors.
     * @return The blended RGB color, or 0xFFFFFF if no colors are provided.
     */
    public static int blendAspectColors(int... aspectColors) {
        if (aspectColors == null || aspectColors.length == 0) {
            return 0xFFFFFF; // Default white
        }

        long r = 0, g = 0, b = 0;
        for (int color : aspectColors) {
            r += (color >> 16) & 0xFF;
            g += (color >> 8) & 0xFF;
            b += color & 0xFF;
        }

        int count = aspectColors.length;
        r /= count;
        g /= count;
        b /= count;

        return (int) ((r << 16) | (g << 8) | b);
    }
}
