package thaumcraft.common.world.features;

/**
 * Pure Java logic helper for calculating Silverwood node flux purification
 * and aura radius mechanics. Zero Minecraft or NeoForge classloader imports.
 */
public class SilverwoodNodeCleansingLogic {

    /**
     * Calculates the amount of flux purified per cycle based on node size and available flux.
     *
     * @param nodeSize the size/power of the node
     * @param localFlux the amount of flux in the aura
     * @return the amount of flux to remove
     */
    public static double calculateFluxPurificationRate(int nodeSize, double localFlux) {
        if (nodeSize <= 0 || localFlux <= 0.0) {
            return 0.0;
        }

        double baseRate = 0.5 + (nodeSize * 0.25);

        // Bonus efficiency in high flux environments
        if (localFlux > 50.0) {
            baseRate *= 1.5;
        }

        // Cannot purify more flux than what is available
        return Math.min(baseRate, localFlux);
    }

    /**
     * Calculates the radius of the pure aura around a Silverwood node.
     *
     * @param nodeSize the size/power of the node
     * @return the radius in blocks
     */
    public static int calculatePureAuraRadius(int nodeSize) {
        if (nodeSize <= 0) {
            return 0;
        }
        return 8 + (nodeSize * 2);
    }
}
