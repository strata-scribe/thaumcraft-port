package thaumcraft.common.blocks.devices.logic;

/**
 * Pure Java logic helper for Recharge Pedestal player interaction mechanics.
 * Strict mathematical and state decoupling: zero Minecraft or NeoForge classloader imports.
 */
public class RechargePedestalInteractionLogic {

    /**
     * Determines whether an item insertion can proceed.
     *
     * @param pedestalHasItem Whether the pedestal currently holds an item.
     * @param isRechargeable Whether the item being inserted is rechargeable.
     * @param stackCount The count of items in the stack being inserted.
     * @return True if insertion is valid, false otherwise.
     */
    public static boolean canInsert(boolean pedestalHasItem, boolean isRechargeable, int stackCount) {
        return !pedestalHasItem && isRechargeable && stackCount > 0;
    }

    /**
     * Determines whether an item extraction can proceed.
     *
     * @param pedestalHasItem Whether the pedestal currently holds an item.
     * @return True if extraction is valid, false otherwise.
     */
    public static boolean canExtract(boolean pedestalHasItem) {
        return pedestalHasItem;
    }

    /**
     * Determines whether an extracted item can be safely delivered (either to player inventory or dropped into world).
     *
     * @param hasPlayer Whether a receiving player is present.
     * @param hasWorldLocation Whether a valid world level and position are available to drop the item.
     * @return True if the item can be delivered to at least one destination, false if it would be lost.
     */
    public static boolean canDeliverExtractedItem(boolean hasPlayer, boolean hasWorldLocation) {
        return hasPlayer || hasWorldLocation;
    }

    /**
     * Calculates the remaining count of the held stack after inserting 1 into the pedestal.
     *
     * @param stackCount Current count of the held stack.
     * @return The new count (clamped to min 0).
     */
    public static int calculateRemainingStackCount(int stackCount) {
        return Math.max(0, stackCount - 1);
    }

    /**
     * Calculates the comparator output signal (0 to 15) for an item on the pedestal.
     *
     * @param hasItem Whether the pedestal has an item.
     * @param currentCharge Current charge of the rechargeable item.
     * @param maxCharge Maximum charge of the rechargeable item.
     * @return Redstone comparator signal level from 0 to 15.
     */
    public static int calculateComparatorSignal(boolean hasItem, int currentCharge, int maxCharge) {
        if (!hasItem) return 0;
        if (maxCharge <= 0) return 15;
        int charge = Math.max(0, currentCharge);
        return Math.min(15, (int) (((long) charge * 15L) / (long) maxCharge));
    }
}
