package thaumcraft.common.blocks.essentia.logic;

import java.util.Locale;

/**
 * Pure, headless domain logic methods for Distillation Alembic interactions and calculations.
 * Decoupled from Minecraft server, block state, and registry dependencies.
 */
public class AlembicInteractionLogic {

    /**
     * Calculates the redstone comparator signal strength (0-15) based on essentia stored and capacity.
     * Formula: (amount * 14 / maxAmount) + (amount > 0 ? 1 : 0), clamped to 15.
     *
     * @param amount current essentia amount stored in the alembic
     * @param maxAmount maximum essentia capacity (typically 32)
     * @return redstone comparator signal between 0 and 15
     */
    public static int calculateComparatorSignal(int amount, int maxAmount) {
        if (amount <= 0 || maxAmount <= 0) return 0;
        return Math.min(15, (int) Math.floor(((double) amount / (double) maxAmount) * 14.0) + (amount > 0 ? 1 : 0));
    }

    /**
     * Checks whether an alembic can survive when placed on top of the given block identifier.
     * Alembics may only be placed on an essentia smelter or another alembic.
     *
     * @param blockBelowId identifier or string representation of the block below
     * @return true if the block below is a smelter or alembic, false otherwise
     */
    public static boolean canSurviveOn(String blockBelowId) {
        if (blockBelowId == null) return false;
        String id = blockBelowId.toLowerCase(Locale.ROOT);
        return id.contains("smelter") || id.contains("alembic");
    }

    /**
     * Determines whether a label can be removed from the alembic.
     * Requires the player to be sneaking, the alembic to have a filter,
     * and the clicked face to match the labeled face.
     *
     * @param isSneaking whether the interacting player is sneaking
     * @param hasFilter whether the alembic currently has an aspect filter attached
     * @param hitFaceOrdinal ordinal of the clicked block face
     * @param facingOrdinal ordinal of the face where the label is attached
     * @return true if label removal conditions are met
     */
    public static boolean canRemoveLabel(boolean isSneaking, boolean hasFilter, int hitFaceOrdinal, int facingOrdinal) {
        return isSneaking && hasFilter && hitFaceOrdinal == facingOrdinal;
    }

    /**
     * Determines whether a label can be applied to the alembic.
     * The alembic must not already have a filter, the target face must be horizontal,
     * and the item must be a label.
     *
     * @param hasFilter whether the alembic currently has an aspect filter attached
     * @param isHorizontalFace whether the clicked face is horizontal (north, south, east, west)
     * @param isLabelItem whether the item being used is a label item
     * @return true if label can be applied
     */
    public static boolean canApplyLabel(boolean hasFilter, boolean isHorizontalFace, boolean isLabelItem) {
        return !hasFilter && isHorizontalFace && isLabelItem;
    }

    /**
     * Determines whether essentia can be vented from the alembic into the aura as flux.
     * Requires the player to be sneaking and the alembic to hold essentia.
     *
     * @param isSneaking whether the player is sneaking
     * @param amount current essentia amount stored in the alembic
     * @return true if venting can occur
     */
    public static boolean canVentEssentia(boolean isSneaking, int amount) {
        return isSneaking && amount > 0;
    }

    /**
     * Determines whether an empty phial can drain essentia from the alembic.
     *
     * @param isEmptyPhial whether the held item is an empty phial
     * @param storedAmount current essentia amount stored in the alembic
     * @param phialCapacity capacity required by the phial (typically 8)
     * @return true if the phial can be filled
     */
    public static boolean canDrainPhial(boolean isEmptyPhial, int storedAmount, int phialCapacity) {
        return isEmptyPhial && storedAmount >= phialCapacity;
    }

    /**
     * Determines whether a filled phial can deposit essentia into the alembic.
     *
     * @param isFilledPhial whether the held item is a filled phial
     * @param currentAmount current essentia amount stored in the alembic
     * @param maxAmount maximum capacity of the alembic
     * @param phialAmount essentia amount inside the phial (typically 8)
     * @return true if the phial contents can fit into the alembic
     */
    public static boolean canDepositPhial(boolean isFilledPhial, int currentAmount, int maxAmount, int phialAmount) {
        return isFilledPhial && (currentAmount + phialAmount <= maxAmount);
    }
}
