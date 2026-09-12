package thaumcraft.common.tiles.crafting;

public class CruciblePollutionLogic {

    /**
     * Calculates the amount of flux spillover pollution into the chunk aura
     * when the crucible contains unsmelted excess aspects and they are dumped.
     *
     * @param totalAspects The total vis size of the aspects being dumped.
     * @return The calculated pollution amount.
     */
    public static float calculateSpilloverPollution(int totalAspects) {
        return totalAspects * 0.1f;
    }
}
