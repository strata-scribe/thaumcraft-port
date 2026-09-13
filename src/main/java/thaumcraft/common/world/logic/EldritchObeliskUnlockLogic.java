package thaumcraft.common.world.logic;

import java.util.List;

/**
 * Pure Java logic helper for Eldritch Obelisk unlocking sequences.
 * Validates Crimson Rites / Eldritch Eye insertion and portal opening triggers.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class EldritchObeliskUnlockLogic {

    /**
     * Enum for valid obelisk unlock items.
     */
    public enum UnlockItem {
        ELDRITCH_EYE,
        CRIMSON_RITES
    }

    /**
     * Checks if a specific item insertion sequence on the 4 pedestals is valid for opening the portal.
     * The standard Thaumcraft mechanic requires either 4 Eldritch Eyes,
     * or a mix that typically allows 1 Crimson Rites and 3 Eldritch Eyes.
     *
     * @param items List of UnlockItems currently placed on the pedestals. Max 4.
     * @return true if the sequence can trigger the portal opening.
     */
    public static boolean isPortalOpeningTriggered(List<UnlockItem> items) {
        if (items == null || items.size() != 4) {
            return false;
        }

        int eyeCount = 0;
        int ritesCount = 0;

        for (UnlockItem item : items) {
            if (item == null) {
                return false;
            }
            if (item == UnlockItem.ELDRITCH_EYE) {
                eyeCount++;
            } else if (item == UnlockItem.CRIMSON_RITES) {
                ritesCount++;
            }
        }

        // Standard: 4 Eldritch Eyes
        if (eyeCount == 4 && ritesCount == 0) {
            return true;
        }

        // Alternative/Crimson: 3 Eldritch Eyes and 1 Crimson Rites
        if (eyeCount == 3 && ritesCount == 1) {
            return true;
        }

        return false;
    }

    /**
     * Validates if a partial or full sequence is valid (i.e. contains only valid unlock items
     * and does not exceed the capacity of 4 pedestals). Also checks that there's no more than 1 Crimson Rites.
     *
     * @param items List of UnlockItems currently placed.
     * @return true if the sequence is valid so far.
     */
    public static boolean isValidInsertionSequence(List<UnlockItem> items) {
        if (items == null || items.size() > 4) {
            return false;
        }

        int ritesCount = 0;
        for (UnlockItem item : items) {
            if (item == null) {
                return false;
            }
            if (item == UnlockItem.CRIMSON_RITES) {
                ritesCount++;
            }
        }

        // Can't have more than 1 Crimson Rites in a valid sequence
        if (ritesCount > 1) {
            return false;
        }

        return true;
    }
}
