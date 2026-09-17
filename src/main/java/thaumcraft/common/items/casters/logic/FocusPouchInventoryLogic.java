package thaumcraft.common.items.casters.logic;

public class FocusPouchInventoryLogic {

    public static int calculatePouchCapacity(int baseCapacity, int upgradeTier) {
        return baseCapacity + (upgradeTier * 18);
    }

    public static int getNextFocusIndex(int currentIndex, int pouchCapacity, boolean reverse, boolean[] populatedSlots) {
        if (populatedSlots == null || populatedSlots.length == 0 || pouchCapacity <= 0) {
            return -1;
        }

        int length = Math.min(pouchCapacity, populatedSlots.length);

        boolean hasPopulated = false;
        for (int i = 0; i < length; i++) {
            if (populatedSlots[i]) {
                hasPopulated = true;
                break;
            }
        }

        if (!hasPopulated) {
            return -1;
        }

        int step = reverse ? -1 : 1;
        int checkIndex = currentIndex;

        for (int i = 0; i < length; i++) {
            checkIndex += step;
            if (checkIndex < 0) {
                checkIndex = length - 1;
            } else if (checkIndex >= length) {
                checkIndex = 0;
            }

            if (populatedSlots[checkIndex]) {
                return checkIndex;
            }
        }

        return currentIndex >= 0 && currentIndex < length && populatedSlots[currentIndex] ? currentIndex : -1;
    }

    public static String[] calculateInventoryPersistence(String[] currentInventory, int[] slotsToKeep) {
        if (currentInventory == null) {
            return null;
        }

        String[] newInventory = new String[currentInventory.length];
        if (slotsToKeep == null) {
            return newInventory;
        }

        for (int index : slotsToKeep) {
            if (index >= 0 && index < currentInventory.length) {
                newInventory[index] = currentInventory[index];
            }
        }

        return newInventory;
    }
}
