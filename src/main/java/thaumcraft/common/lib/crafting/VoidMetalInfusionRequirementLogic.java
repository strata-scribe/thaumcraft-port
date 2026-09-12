package thaumcraft.common.lib.crafting;

/**
 * Pure Java logic decoupled from Minecraft/Forge for evaluating player
 * permanent warp prerequisites before permitting Void Metal recipes.
 */
public class VoidMetalInfusionRequirementLogic {

    private static final int VOID_METAL_WARP_THRESHOLD = 15;

    /**
     * Determines if a recipe is permitted based on the output item and the player's permanent warp.
     * Void Metal items require a certain amount of permanent warp to be crafted.
     *
     * @param playerPermanentWarp The player's current permanent warp level.
     * @param recipeOutputItem The string identifier/name of the recipe's output item.
     * @return true if the recipe is permitted, false if the prerequisite is not met.
     */
    public static boolean isRecipePermitted(int playerPermanentWarp, String recipeOutputItem) {
        if (recipeOutputItem == null || recipeOutputItem.isEmpty()) {
            return true; // No specific item, permit by default
        }

        // Check if it's a void metal related item
        if (isVoidMetalItem(recipeOutputItem)) {
            return playerPermanentWarp >= VOID_METAL_WARP_THRESHOLD;
        }

        return true; // Not a void metal item, no warp requirement
    }

    /**
     * Helper method to determine if an item is a Void Metal item based on its name.
     */
    private static boolean isVoidMetalItem(String itemName) {
        String lowerName = itemName.toLowerCase();
        // A simple check for "void" and "metal", or "void" in general for these recipes
        return lowerName.contains("void");
    }
}
