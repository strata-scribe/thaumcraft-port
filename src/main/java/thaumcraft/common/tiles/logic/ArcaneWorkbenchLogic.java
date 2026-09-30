package thaumcraft.common.tiles.logic;

/**
 * Pure, headless domain logic for the Arcane Workbench container and block.
 * Contains zero Minecraft server, level, or registry dependencies.
 */
public final class ArcaneWorkbenchLogic {

    private ArcaneWorkbenchLogic() {
        // Utility class
    }

    /**
     * Calculates analog redstone comparator signal [0, 15] matching Minecraft vanilla container logic:
     * if filledSlots <= 0 || totalSlots <= 0, return 0;
     * double factor = totalFillRatio / totalSlots;
     * return Math.min(15, (int) Math.floor(factor * 14.0) + 1);
     *
     * @param totalSlots     total container slot capacity
     * @param filledSlots    number of non-empty slots
     * @param totalFillRatio sum of (itemCount / maxStackSize) across all slots
     * @return redstone signal between 0 and 15
     */
    public static int calculateComparatorSignal(int totalSlots, int filledSlots, double totalFillRatio) {
        if (filledSlots <= 0 || totalSlots <= 0 || totalFillRatio <= 0.0 || Double.isNaN(totalFillRatio)) {
            return 0;
        }
        double factor = totalFillRatio / (double) totalSlots;
        int signal = (int) Math.floor(factor * 14.0) + 1;
        return Math.max(0, Math.min(15, signal));
    }

    /**
     * Determines whether inventory contents should be dropped when the block state changes.
     *
     * @param oldBlockId registry ID of the old block (e.g. "thaumcraft:arcane_workbench")
     * @param newBlockId registry ID of the new block
     * @return true if contents should be dropped, false otherwise
     */
    public static boolean shouldDropContents(String oldBlockId, String newBlockId) {
        return oldBlockId != null && !oldBlockId.equals(newBlockId);
    }

    /**
     * Checks if a player can open the workbench interface based on their shift/sneak state.
     *
     * @param isShiftKeyDown true if player is sneaking/holding shift
     * @return true if workbench should open
     */
    public static boolean canOpenWorkbench(boolean isShiftKeyDown) {
        return !isShiftKeyDown;
    }

    /**
     * Checks if a given slot index belongs to the crystal inventory (slots 9-14).
     *
     * @param slotIndex 0-based inventory slot index
     * @return true if the slot is a crystal slot
     */
    public static boolean isCrystalSlot(int slotIndex) {
        return slotIndex >= 9 && slotIndex <= 14;
    }

    /**
     * Checks if a given slot index belongs to the 3x3 crafting grid (slots 0-8).
     *
     * @param slotIndex 0-based inventory slot index
     * @return true if the slot is a crafting slot
     */
    public static boolean isCraftingSlot(int slotIndex) {
        return slotIndex >= 0 && slotIndex <= 8;
    }
}
