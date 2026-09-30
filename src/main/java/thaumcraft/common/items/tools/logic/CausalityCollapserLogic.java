package thaumcraft.common.items.tools.logic;

/**
 * Pure, headless domain logic engine for Causality Collapser.
 * Zero Minecraft server/registry dependencies.
 */
public class CausalityCollapserLogic {

    /**
     * Determines whether an explosion of given power can collapse a flux rift of given stability.
     *
     * @param riftStability rift stability threshold
     * @param explosionPower effective explosion power delivered to rift center
     * @return true if explosion power is greater than or equal to rift stability
     */
    public static boolean canCollapseRift(float riftStability, float explosionPower) {
        return explosionPower >= riftStability;
    }

    /**
     * Calculates void seed drops upon collapsing a rift of given size and random roll.
     *
     * @param riftSize size of the flux rift
     * @param roll uniform random roll in [0.0, 1.0)
     * @return drop count of void seeds (0 if riftSize <= 0, minimum 1 otherwise)
     */
    public static int calculateVoidSeedDropCount(float riftSize, float roll) {
        if (riftSize <= 0.0f) {
            return 0;
        }
        int base = (int) Math.floor(riftSize / 5.0f);
        return Math.max(1, base + (roll < 0.5f ? 1 : 0));
    }

    /**
     * Standard explosion radius of the causality collapser.
     *
     * @return explosion radius in blocks
     */
    public static float getExplosionRadius() {
        return 4.0f;
    }
}
