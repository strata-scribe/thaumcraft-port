package thaumcraft.common.items.tools;

public class HandMirrorLogic {

    public interface IItemSlot {
        String getItemId();
        int getCount();
        int getMaxStackSize();
        void setItem(String id, int count);
        void addCount(int amount);
    }

    /**
     * Tries to deposit an item into a linked inventory.
     *
     * @param mirrorDim The dimension ID of the hand mirror.
     * @param mirrorX The X position of the mirror.
     * @param mirrorY The Y position of the mirror.
     * @param mirrorZ The Z position of the mirror.
     * @param linkedDim The dimension ID of the linked inventory.
     * @param linkedX The X position of the linked inventory.
     * @param linkedY The Y position of the linked inventory.
     * @param linkedZ The Z position of the linked inventory.
     * @param maxDistance The maximum allowed distance between the mirror and the inventory.
     * @param inventory The target inventory represented as an array of slots.
     * @param itemToDeposit The item ID to deposit.
     * @param countToDeposit The number of items to deposit.
     * @return The number of items successfully deposited.
     */
    public static int tryDeposit(
            String mirrorDim, double mirrorX, double mirrorY, double mirrorZ,
            String linkedDim, double linkedX, double linkedY, double linkedZ,
            double maxDistance,
            IItemSlot[] inventory,
            String itemToDeposit, int countToDeposit) {

        if (mirrorDim == null || linkedDim == null || !mirrorDim.equals(linkedDim)) {
            return 0;
        }

        double distSq = (mirrorX - linkedX) * (mirrorX - linkedX) +
                        (mirrorY - linkedY) * (mirrorY - linkedY) +
                        (mirrorZ - linkedZ) * (mirrorZ - linkedZ);

        if (distSq > maxDistance * maxDistance) {
            return 0;
        }

        int remaining = countToDeposit;

        // First pass: Try to add to existing matching stacks
        for (IItemSlot slot : inventory) {
            if (remaining <= 0) break;

            if (itemToDeposit.equals(slot.getItemId())) {
                int space = slot.getMaxStackSize() - slot.getCount();
                if (space > 0) {
                    int toAdd = Math.min(remaining, space);
                    slot.addCount(toAdd);
                    remaining -= toAdd;
                }
            }
        }

        // Second pass: Try to add to empty slots
        for (IItemSlot slot : inventory) {
            if (remaining <= 0) break;

            if (slot.getItemId() == null || slot.getCount() == 0) {
                int toAdd = Math.min(remaining, slot.getMaxStackSize());
                slot.setItem(itemToDeposit, toAdd);
                remaining -= toAdd;
            }
        }

        return countToDeposit - remaining;
    }
}
