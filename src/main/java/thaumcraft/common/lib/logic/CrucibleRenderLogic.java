package thaumcraft.common.lib.logic;

/**
 * Pure Java logic for crucible rendering calculations, separated from Minecraft/Forge APIs.
 */
public class CrucibleRenderLogic {

    /**
     * Calculates a blended RGB color based on the constituent colors and their amounts.
     *
     * @param colors Array of RGB integer colors for each aspect.
     * @param amounts Array of amounts for each aspect. Must be the same length as colors.
     * @param defaultColor The color to return if the arrays are empty or total amount is zero.
     * @return The blended RGB color.
     */
    public static int calculateBlendedColor(int[] colors, int[] amounts, int defaultColor) {
        if (colors == null || amounts == null || colors.length != amounts.length || colors.length == 0) {
            return defaultColor;
        }

        long r = 0;
        long g = 0;
        long b = 0;
        long total = 0;

        for (int i = 0; i < colors.length; i++) {
            int amount = amounts[i];
            if (amount <= 0) continue;

            int color = colors[i];
            r += ((color >> 16) & 0xFF) * (long) amount;
            g += ((color >> 8) & 0xFF) * (long) amount;
            b += (color & 0xFF) * (long) amount;
            total += amount;
        }

        if (total == 0) {
            return defaultColor;
        }

        int finalR = (int) (r / total);
        int finalG = (int) (g / total);
        int finalB = (int) (b / total);

        return (finalR << 16) | (finalG << 8) | finalB;
    }

    /**
     * Computes the X and Z offset for bubble particles across the water plane of the crucible.
     * The standard Thaumcraft crucible (and vanilla cauldron) has an inner liquid area
     * ranging from 2/16 (0.125) to 14/16 (0.875).
     *
     * @param randomX A random float, typically in the range [0.0, 1.0].
     * @param randomZ A random float, typically in the range [0.0, 1.0].
     * @return A float array containing the [x, z] offset relative to the block's origin.
     */
    public static float[] computeBubbleOffset(float randomX, float randomZ) {
        float min = 0.125f; // 2/16
        float span = 0.75f; // 12/16

        float x = min + (randomX * span);
        float z = min + (randomZ * span);

        return new float[]{x, z};
    }
}
