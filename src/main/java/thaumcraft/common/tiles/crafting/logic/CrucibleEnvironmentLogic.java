package thaumcraft.common.tiles.crafting.logic;

/**
 * Headless environment and interaction logic for the Crucible.
 * Contains pure helper methods for heat sources, water tank math, boiling states, and safety checks.
 */
public final class CrucibleEnvironmentLogic {

    public static final int TANK_CAPACITY = 1000;
    public static final int BUCKET_VOLUME = 1000;
    public static final int BOTTLE_VOLUME = 333;
    public static final short BOILING_HEAT_THRESHOLD = 151;

    private CrucibleEnvironmentLogic() {}

    /**
     * Determines whether the given block ID qualifies as a heat source.
     *
     * @param blockId registry name string of the block (e.g. "minecraft:fire")
     * @param isLit whether the block is lit (relevant for campfires)
     * @return true if it provides heat to the crucible
     */
    public static boolean isHeatSource(String blockId, boolean isLit) {
        if (blockId == null) {
            return false;
        }

        switch (blockId) {
            case "minecraft:fire":
            case "minecraft:soul_fire":
            case "minecraft:lava":
            case "minecraft:magma_block":
            case "thaumcraft:nitor":
                return true;
            case "minecraft:campfire":
            case "minecraft:soul_campfire":
                return isLit;
            default:
                return false;
        }
    }

    /**
     * Determines whether the given block ID qualifies as a heat source, defaulting isLit to true.
     *
     * @param blockId registry name string of the block
     * @return true if it provides heat to the crucible
     */
    public static boolean isHeatSource(String blockId) {
        return isHeatSource(blockId, true);
    }

    /**
     * Checks if the crucible can be filled with a water bucket.
     */
    public static boolean canFillWithBucket(int currentWater) {
        return currentWater < TANK_CAPACITY;
    }

    /**
     * Calculates the water level after pouring a bucket of water.
     */
    public static int fillWithBucket(int currentWater) {
        return Math.min(TANK_CAPACITY, currentWater + BUCKET_VOLUME);
    }

    /**
     * Checks if water can be drained into an empty bucket.
     * Requires at least a full bucket of water and no dissolved aspects.
     */
    public static boolean canDrainWithBucket(int currentWater, int aspectCount) {
        return currentWater >= BUCKET_VOLUME && aspectCount == 0;
    }

    /**
     * Calculates the water level after scooping out a bucket of water.
     */
    public static int drainWithBucket(int currentWater) {
        return Math.max(0, currentWater - BUCKET_VOLUME);
    }

    /**
     * Checks if the crucible can be filled with a water bottle.
     */
    public static boolean canFillWithBottle(int currentWater) {
        return currentWater + BOTTLE_VOLUME <= TANK_CAPACITY;
    }

    /**
     * Calculates the water level after pouring a bottle of water.
     */
    public static int fillWithBottle(int currentWater) {
        return Math.min(TANK_CAPACITY, currentWater + BOTTLE_VOLUME);
    }

    /**
     * Checks if water can be drained into an empty glass bottle.
     * Requires at least a bottle's volume of water and no dissolved aspects.
     */
    public static boolean canDrainWithBottle(int currentWater, int aspectCount) {
        return currentWater >= BOTTLE_VOLUME && aspectCount == 0;
    }

    /**
     * Calculates the water level after scooping out a bottle of water.
     */
    public static int drainWithBottle(int currentWater) {
        return Math.max(0, currentWater - BOTTLE_VOLUME);
    }

    /**
     * Checks if the crucible is boiling (heat >= 151 and containing water).
     */
    public static boolean isBoiling(short heat, int water) {
        return heat >= BOILING_HEAT_THRESHOLD && water > 0;
    }

    /**
     * Checks if alchemy smelting can occur (identical to boiling condition).
     */
    public static boolean canSmelt(short heat, int water) {
        return isBoiling(heat, water);
    }

    /**
     * Checks if a living entity should take boiling damage when inside the crucible.
     */
    public static boolean shouldHurtLivingEntity(short heat, int water, boolean isFireImmune) {
        return isBoiling(heat, water) && !isFireImmune;
    }
}
