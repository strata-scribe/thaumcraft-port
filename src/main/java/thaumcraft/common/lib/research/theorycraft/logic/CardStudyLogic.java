package thaumcraft.common.lib.research.theorycraft.logic;

public class CardStudyLogic {

    /**
     * Calculates the research study yield based on item rarity, aspect density, and player observation level.
     *
     * @param itemRarity             The rarity level of the item/artifact (e.g., 0 for common, 1 for uncommon, etc.).
     * @param aspectDensity          The total number of aspect points the item possesses.
     * @param playerObservationLevel The player's observation level.
     * @return The calculated research yield as an integer.
     */
    public static int calculateStudyYield(int itemRarity, int aspectDensity, int playerObservationLevel) {
        int baseYield = 10;
        int rarityBonus = itemRarity * 5;
        int densityBonus = aspectDensity * 2;
        int observationBonus = Math.max(0, playerObservationLevel / 10);

        return baseYield + rarityBonus + densityBonus + observationBonus;
    }
}
