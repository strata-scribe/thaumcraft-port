package thaumcraft.common.world.features;

/**
 * Pure Java logic helper for procedural generation of the Silverwood Tree.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class SilverwoodTreeLogic {

    /**
     * Checks if relative coordinates (dx, dz) correspond to the elegant single trunk.
     *
     * @param dx Relative X offset from trunk center
     * @param dz Relative Z offset from trunk center
     * @return true if the block is part of the single trunk
     */
    public static boolean isSingleTrunk(int dx, int dz) {
        return dx == 0 && dz == 0;
    }

    /**
     * Checks if relative coordinates (dx, dy, dz) correspond to the pure aura node heart block at mid-height.
     *
     * @param dx Relative X offset
     * @param dy Relative Y offset
     * @param dz Relative Z offset
     * @param height Total tree height
     * @return true if the block is the pure aura node heart block
     */
    public static boolean isPureNodeHeart(int dx, int dy, int dz, int height) {
        return dx == 0 && dz == 0 && dy == (height / 2);
    }

    /**
     * Checks if relative coordinates (dx, dy, dz) fall within the high-vis shimmer foliage.
     *
     * @param dx Relative X offset
     * @param dy Relative Y offset
     * @param dz Relative Z offset
     * @param radiusSq Squared radius threshold
     * @return true if the block emits high-vis shimmer foliage
     */
    public static boolean isHighVisShimmerFoliage(int dx, int dy, int dz, int radiusSq) {
        return (dx * dx + dy * dy + dz * dz) <= radiusSq;
    }

}
