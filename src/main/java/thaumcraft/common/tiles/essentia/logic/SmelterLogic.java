package thaumcraft.common.tiles.essentia.logic;

/**
 * Pure, headless domain logic methods for Essentia Smelter calculations.
 * Decoupled from Minecraft server and registry dependencies.
 */
public class SmelterLogic {

    /**
     * Calculates the redstone comparator signal strength (0-15) based on vis stored and capacity.
     *
     * @param vis current essentia in the smelter buffer
     * @param capacity maximum essentia capacity for the smelter tier
     * @return redstone comparator signal between 0 and 15
     */
    public static int calculateComparatorSignal(int vis, int capacity) {
        if (vis <= 0 || capacity <= 0) return 0;
        return Math.min(15, (int) Math.floor((double) vis * 15.0 / (double) capacity));
    }

    /**
     * Determines whether inventory should be dropped when a block state changes.
     *
     * @param oldBlockId identifier of the old block
     * @param newBlockId identifier of the new block
     * @return true if inventory should drop, false if preserved or old block was null
     */
    public static boolean shouldDropInventory(String oldBlockId, String newBlockId) {
        return oldBlockId != null && !oldBlockId.equals(newBlockId);
    }

    /**
     * Calculates the cook time for smelting an item, applying reductions for bellows and aux pumps.
     * Each bellows reduces base cook time by 25% (up to 2 bellows / 50% max reduction).
     * Aux pumps further reduce cook time via SmelterAuxLogic.calculateSmeltTime.
     * The minimum return value is 1.
     *
     * @param baseCookTime the initial cook time before speedup modifiers
     * @param bellows number of adjacent bellows attached
     * @param auxPumps number of adjacent auxiliary pumps attached
     * @return final cook time in ticks (minimum 1)
     */
    public static int calculateCookTime(int baseCookTime, int bellows, int auxPumps) {
        int effectiveBellows = Math.max(0, Math.min(bellows, 2));
        float bellowsMultiplier = 1.0f - (effectiveBellows * 0.25f);
        int afterBellows = (int) (baseCookTime * bellowsMultiplier);
        int finalTime = SmelterAuxLogic.calculateSmeltTime(afterBellows, auxPumps);
        return Math.max(1, finalTime);
    }
}
