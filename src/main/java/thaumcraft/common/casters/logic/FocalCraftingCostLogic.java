package thaumcraft.common.casters.logic;

/**
 * Pure Java logic class for calculating focal manipulator crafting costs.
 * Decoupled from Minecraft and Forge dependencies for easy testing.
 */
public class FocalCraftingCostLogic {

    /**
     * Calculates the player experience (XP) levels cost based on focal complexity.
     * Formula: Max of 1, or complexity scaled by a factor (e.g., complexity).
     *
     * @param complexity The total complexity of the focus.
     * @return The required player XP cost.
     */
    public static int calculateExpCost(int complexity) {
        if (complexity <= 0) {
            return 0;
        }
        return Math.max(1, complexity);
    }

    /**
     * Calculates the number of vis crystals required for crafting based on focal complexity.
     * Formula: Max of 1, or complexity / 5.
     *
     * @param complexity The total complexity of the focus.
     * @return The required number of vis crystals.
     */
    public static int calculateCrystalCost(int complexity) {
        if (complexity <= 0) {
            return 0;
        }
        return Math.max(1, complexity / 5);
    }
}
