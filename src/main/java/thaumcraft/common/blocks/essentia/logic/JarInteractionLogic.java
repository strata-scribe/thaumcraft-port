package thaumcraft.common.blocks.essentia.logic;

/**
 * Pure, headless domain logic methods for Warded and Void Jar interactions and comparator signaling.
 * Decoupled from Minecraft server, block entity, and registry dependencies.
 */
public class JarInteractionLogic {

    /**
     * Calculates the redstone comparator signal strength (0-15) based on essentia stored and capacity.
     * Formula: floor((amount * 15.0) / capacity), clamped to [0, 15].
     *
     * @param amount current essentia amount stored in the jar
     * @param capacity maximum essentia capacity (typically 250)
     * @return redstone comparator signal between 0 and 15
     */
    public static int calculateComparatorSignal(int amount, int capacity) {
        if (amount <= 0 || capacity <= 0) return 0;
        return Math.min(15, (int) Math.floor(((double) amount * 15.0) / (double) capacity));
    }

    /**
     * Determines whether a label can be applied to the jar.
     * Requires that the jar does not already have an aspect filter and that the used item is a label.
     *
     * @param hasFilter whether the jar currently has an aspect filter
     * @param isLabelItem whether the item being used is a label item
     * @return true if the label can be applied
     */
    public static boolean canApplyLabel(boolean hasFilter, boolean isLabelItem) {
        return !hasFilter && isLabelItem;
    }

    /**
     * Determines whether a label can be removed from the jar.
     * Requires the player to be sneaking and the jar to currently have an aspect filter.
     *
     * @param isSneaking whether the player is sneaking
     * @param hasFilter whether the jar currently has an aspect filter
     * @return true if the label can be removed
     */
    public static boolean canRemoveLabel(boolean isSneaking, boolean hasFilter) {
        return isSneaking && hasFilter;
    }

    /**
     * Determines whether essentia from a filled phial can be emptied into the jar.
     * Requires a filled phial, compatibility with the jar's aspect rules, and either
     * sufficient room or a void jar.
     *
     * @param isFilledPhial whether the item is a filled phial
     * @param acceptsAspect whether the jar accepts the phial's aspect
     * @param currentAmount current essentia amount stored in the jar
     * @param capacity maximum capacity of the jar
     * @param phialAmount amount of essentia contained in the phial (typically 10)
     * @param isVoid whether the jar is a void jar (can discard excess)
     * @return true if the phial can pour into the jar
     */
    public static boolean canFillFromPhial(boolean isFilledPhial, boolean acceptsAspect,
                                          int currentAmount, int capacity, int phialAmount, boolean isVoid) {
        return isFilledPhial && acceptsAspect && (isVoid || (currentAmount + phialAmount <= capacity));
    }

    /**
     * Determines whether an empty phial can drain essentia from the jar.
     * Requires an empty phial and at least phialAmount essentia stored in the jar.
     *
     * @param isEmptyPhial whether the item is an empty phial
     * @param currentAmount current essentia amount stored in the jar
     * @param phialAmount amount of essentia required by the phial (typically 10)
     * @return true if the phial can drain essentia
     */
    public static boolean canDrainToPhial(boolean isEmptyPhial, int currentAmount, int phialAmount) {
        return isEmptyPhial && currentAmount >= phialAmount;
    }
}
