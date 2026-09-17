package thaumcraft.common.lib.research.theorycraft.logic;

public class CardInspiredLogic {

    /**
     * Calculates the number of bonus cards to draw based on warp level and active research aids.
     * @param warpLevel The player's current warp level.
     * @param activeAids The number of active research aids in the table.
     * @return The bonus card draw count (1 or 2).
     */
    public static int calculateBonusCardDraws(int warpLevel, int activeAids) {
        if (warpLevel + activeAids * 5 >= 25) {
            return 2;
        }
        return 1;
    }

    /**
     * Calculates the probability of an inspiration refund.
     * @param warpLevel The player's current warp level.
     * @param activeAids The number of active research aids in the table.
     * @return A refund chance between 0.0 and 1.0.
     */
    public static double calculateInspirationRefundChance(int warpLevel, int activeAids) {
        double chance = (warpLevel * 0.01) + (activeAids * 0.05);
        return Math.max(0.0, Math.min(1.0, chance));
    }
}
