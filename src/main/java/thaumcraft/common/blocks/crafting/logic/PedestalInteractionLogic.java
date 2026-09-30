package thaumcraft.common.blocks.crafting.logic;

/**
 * Pure, headless domain logic for pedestal block interactions and mechanics.
 * Contains zero Minecraft server/registry dependencies.
 */
public final class PedestalInteractionLogic {

    private PedestalInteractionLogic() {}

    /**
     * Determines whether an item can be placed onto a pedestal.
     *
     * @param pedestalHasItem whether the pedestal already holds an item
     * @param isHeldEmpty     whether the player's held item stack is empty
     * @param isSneaking      whether the player is sneaking/crouching
     * @return true if an item can be placed
     */
    public static boolean canPlaceItem(boolean pedestalHasItem, boolean isHeldEmpty, boolean isSneaking) {
        return !pedestalHasItem && !isHeldEmpty && !isSneaking;
    }

    /**
     * Calculates the remaining count of the held item stack after placing one item.
     *
     * @param count      current stack count
     * @param isCreative whether the player is in creative mode (instabuild)
     * @return remaining count
     */
    public static int calculateRemainingHeld(int count, boolean isCreative) {
        return isCreative ? count : Math.max(0, count - 1);
    }

    /**
     * Determines whether an item can be extracted from a pedestal.
     *
     * @param pedestalHasItem whether the pedestal holds an item
     * @param isHandEmpty     whether the interacting hand is empty
     * @param isSneaking      whether the player is sneaking/crouching
     * @return true if an item can be extracted
     */
    public static boolean canExtractItem(boolean pedestalHasItem, boolean isHandEmpty, boolean isSneaking) {
        return pedestalHasItem && (isHandEmpty || !isSneaking);
    }

    /**
     * Calculates redstone comparator output signal based on item presence.
     *
     * @param hasItem whether the pedestal holds an item
     * @return 15 if holding an item, 0 if empty
     */
    public static int calculateComparatorSignal(boolean hasItem) {
        return hasItem ? 15 : 0;
    }

    /**
     * Determines whether there is a symmetry penalty between two opposing pedestals.
     * A penalty occurs if one pedestal holds an item and the other does not.
     *
     * @param ped1HasItem whether first pedestal has an item
     * @param ped2HasItem whether second pedestal has an item
     * @return true if symmetry is broken
     */
    public static boolean hasSymmetryPenalty(boolean ped1HasItem, boolean ped2HasItem) {
        return ped1HasItem != ped2HasItem;
    }

    /**
     * Determines whether the pedestal's held item should drop into the world upon block removal.
     *
     * @param oldBlockId identifier of the old block
     * @param newBlockId identifier of the replacement block
     * @param hasItem    whether the pedestal currently holds an item
     * @return true if the item should be dropped
     */
    public static boolean shouldDropItemOnRemoval(String oldBlockId, String newBlockId, boolean hasItem) {
        return hasItem && oldBlockId != null && !oldBlockId.equals(newBlockId);
    }
}
