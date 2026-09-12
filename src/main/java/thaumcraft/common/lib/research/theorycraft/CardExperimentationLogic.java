package thaumcraft.common.lib.research.theorycraft;

public class CardExperimentationLogic {

    /**
     * Calculates success probability based on player knowledge and inspiration level.
     * @param playerKnowledge The player's general knowledge or observation points.
     * @param inspirationLevel The current inspiration level from the research table.
     * @return A success chance between 0.0 and 1.0.
     */
    public static double calculateSuccessProbability(int playerKnowledge, int inspirationLevel) {
        double chance = 0.4 + (playerKnowledge * 0.05) + (inspirationLevel * 0.02);
        return Math.max(0.0, Math.min(1.0, chance));
    }

    /**
     * Computes progress points rewarded based on the base points and whether the experiment succeeded.
     * @param basePoints The randomly rolled base points.
     * @param success Whether the experimentation was a success.
     * @return The final progress points.
     */
    public static int computeProgressPoints(int basePoints, boolean success) {
        if (success) {
            return basePoints;
        } else {
            return Math.max(1, basePoints / 3);
        }
    }

    /**
     * Computes warp hazard penalties on failure.
     * @param success Whether the experimentation was a success.
     * @param inspirationLevel The current inspiration level.
     * @return The amount of warp to add to the player.
     */
    public static int computeWarpPenalty(boolean success, int inspirationLevel) {
        if (success) {
            return 0;
        }
        return Math.max(1, inspirationLevel / 2);
    }
}
