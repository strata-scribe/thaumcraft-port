package thaumcraft.common.tiles.crafting.logic;

public class CrucibleBoilingLogic {

    public static final short MAX_HEAT = 200;
    public static final short BOILING_THRESHOLD = 151;

    /**
     * Determines if a block is a valid heat source based on its identifier string.
     * Note: In a real implementation this could take the registry name.
     *
     * @param blockId The identifier string of the block.
     * @return true if the block is a valid heat source.
     */
    public static boolean isHeatSource(String blockId) {
        if (blockId == null) return false;

        return blockId.equals("minecraft:fire") ||
               blockId.equals("minecraft:lava") ||
               blockId.equals("minecraft:magma_block") ||
               blockId.equals("minecraft:campfire") ||
               blockId.equals("minecraft:soul_campfire") ||
               blockId.equals("thaumcraft:nitor");
    }

    /**
     * Calculates the new heat level of the crucible.
     *
     * @param currentHeat The current heat level.
     * @param hasWater Whether the crucible has water.
     * @param hasHeatSource Whether there is a heat source below the crucible.
     * @return The updated heat level.
     */
    public static short calculateHeat(short currentHeat, boolean hasWater, boolean hasHeatSource) {
        if (hasWater) {
            if (hasHeatSource) {
                if (currentHeat < MAX_HEAT) {
                    return (short) (currentHeat + 1);
                }
            } else {
                if (currentHeat > 0) {
                    return (short) (currentHeat - 1);
                }
            }
        } else {
            if (currentHeat > 0) {
                return (short) (currentHeat - 1);
            }
        }
        return currentHeat;
    }

    /**
     * Checks if the heat level reached or dropped from the boiling threshold this tick.
     *
     * @param prevHeat The heat before the update.
     * @param newHeat The heat after the update.
     * @return true if the boiling state changed.
     */
    public static boolean didBoilingStateChange(short prevHeat, short newHeat) {
        return (prevHeat < BOILING_THRESHOLD && newHeat >= BOILING_THRESHOLD) ||
               (prevHeat >= BOILING_THRESHOLD && newHeat < BOILING_THRESHOLD);
    }

    /**
     * Calculates time until boiling readiness in ticks.
     * @param currentHeat The current heat level.
     * @return The ticks remaining until boiling, or 0 if already boiling.
     */
    public static int ticksUntilBoiling(short currentHeat) {
        if (currentHeat >= BOILING_THRESHOLD) {
            return 0;
        }
        return BOILING_THRESHOLD - currentHeat;
    }
}
