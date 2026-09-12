package thaumcraft.common.lib.research.theorycraft;

public class ThaumicDementiaLogic {

    /**
     * Calculates the probability of a theorycraft card being distorted by the player's warp.
     * @param playerWarp The player's total warp level.
     * @return A probability between 0.0 and 1.0.
     */
    public static double calculateDistortionChance(int playerWarp) {
        if (playerWarp <= 0) {
            return 0.0;
        }
        // Base calculation, capped at a maximum of 50% chance to distort
        double chance = playerWarp * 0.005;
        return Math.min(0.5, chance);
    }

    /**
     * Calculates the amount of inspiration lost due to the player's warp.
     * @param playerWarp The player's total warp level.
     * @return The amount of inspiration to deduct.
     */
    public static int calculateInspirationLoss(int playerWarp) {
        if (playerWarp < 20) {
            return 0;
        } else if (playerWarp < 50) {
            return 1;
        } else if (playerWarp < 100) {
            return 2;
        } else {
            return 3;
        }
    }
}
