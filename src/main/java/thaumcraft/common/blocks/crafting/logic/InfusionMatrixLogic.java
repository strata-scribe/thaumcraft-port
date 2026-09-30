package thaumcraft.common.blocks.crafting.logic;

/**
 * Pure, headless domain logic for infusion matrix block interactions and mechanics.
 * Contains zero Minecraft server/registry dependencies.
 */
public final class InfusionMatrixLogic {

    private InfusionMatrixLogic() {}

    /**
     * Calculates redstone comparator output signal based on crafting activity.
     *
     * @param isCrafting whether the infusion matrix is actively crafting
     * @return 15 if actively crafting, 0 if idle
     */
    public static int calculateComparatorSignal(boolean isCrafting) {
        return isCrafting ? 15 : 0;
    }

    /**
     * Determines whether the matrix can be activated or crafting initiated by a player interaction.
     * Sneaking prevents activation to permit block placement or other actions.
     *
     * @param isSneaking whether the interacting player is sneaking/crouching
     * @return true if matrix can be activated
     */
    public static boolean canActivateMatrix(boolean isSneaking) {
        return !isSneaking;
    }

    /**
     * Maps numerical stability value to categorical stability name.
     *
     * @param stability numerical stability value
     * @return category string: VERY_STABLE, STABLE, UNSTABLE, or VERY_UNSTABLE
     */
    public static String getStabilityCategory(float stability) {
        if (stability >= 0.0f) return "VERY_STABLE";
        if (stability >= -5.0f) return "STABLE";
        if (stability >= -10.0f) return "UNSTABLE";
        return "VERY_UNSTABLE";
    }
}
