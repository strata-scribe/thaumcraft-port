package thaumcraft.common.blocks.logic;

/**
 * Pure Java logic for simulating Tainted Leaves decay and desiccation.
 * Decoupled from net.minecraft for JUnit testing.
 */
public class TaintedLeavesDecayLogic {

    public static final String TAINT_FIBER = "thaumcraft:taint_fiber";
    public static final String SAPLING = "minecraft:sapling";
    public static final String NONE = "none";

    /**
     * Calculates the desiccation state of a tainted leaf based on its proximity to a supporting log.
     *
     * @param currentState  The current desiccation state of the leaf (e.g., 0-5).
     * @param distanceToLog The distance to the nearest supporting log block.
     * @param maxDistance   The maximum distance at which a log can support the leaf.
     * @return The updated desiccation state.
     */
    public static int calculateDesiccation(int currentState, int distanceToLog, int maxDistance) {
        if (distanceToLog > maxDistance) {
            return currentState + 1;
        }
        return 0;
    }

    /**
     * Determines the drop of a tainted leaf upon breaking or decaying.
     *
     * @param randomRoll   A random value between 0.0 (inclusive) and 1.0 (exclusive).
     * @param fortuneLevel The level of the Fortune enchantment used (0 if none or natural decay).
     * @param isDesiccated Whether the leaf has reached full desiccation.
     * @return The string identifier of the dropped item, or "none" if nothing drops.
     */
    public static String determineDrops(double randomRoll, int fortuneLevel, boolean isDesiccated) {
        double saplingBase = isDesiccated ? 0.0 : 0.05;
        double saplingFortuneBonus = isDesiccated ? 0.0 : 0.02 * fortuneLevel;
        double saplingChance = saplingBase + saplingFortuneBonus;

        double fiberBase = isDesiccated ? 0.20 : 0.05;
        double fiberFortuneBonus = 0.05 * fortuneLevel;
        double fiberChance = fiberBase + fiberFortuneBonus;

        if (randomRoll < saplingChance) {
            return SAPLING;
        } else if (randomRoll < saplingChance + fiberChance) {
            return TAINT_FIBER;
        }
        return NONE;
    }
}
