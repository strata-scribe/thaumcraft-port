package thaumcraft.common.tiles.devices;

/**
 * Pure Java logic helper for Vis Recharge Pedestal mechanics.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class VisRechargePedestalLogic {

    /**
     * Calculates the energy transfer rate from aura to item.
     *
     * @param auraAvailable The amount of aura energy available.
     * @param itemMissingCharge The amount of charge the item is missing.
     * @param baseTransferRate The base transfer rate of the pedestal.
     * @return The amount of energy to transfer.
     */
    public static int calculateTransferRate(int auraAvailable, int itemMissingCharge, int baseTransferRate) {
        if (auraAvailable <= 0 || itemMissingCharge <= 0 || baseTransferRate <= 0) {
            return 0;
        }
        return Math.min(baseTransferRate, Math.min(auraAvailable, itemMissingCharge));
    }

    /**
     * Applies vis discount to a raw vis cost.
     *
     * @param rawVisCost The raw vis cost.
     * @param visDiscount The vis discount as a percentage (e.g. 0.1 for 10%, 1.0 for 100%).
     * @return The discounted vis cost.
     */
    public static float applyVisDiscount(float rawVisCost, float visDiscount) {
        if (Float.isNaN(rawVisCost) || Float.isNaN(visDiscount) || rawVisCost <= 0.0f) {
            return 0.0f;
        }
        float discount = Math.max(0.0f, Math.min(1.0f, visDiscount));
        return rawVisCost * (1.0f - discount);
    }

    /**
     * Checks if recharge can proceed given available aura and missing charge.
     */
    public static boolean canRecharge(int auraAvailable, int itemMissingCharge) {
        return auraAvailable > 0 && itemMissingCharge > 0;
    }

    /**
     * Checks if recharge can proceed given available aura, missing charge, and base transfer rate.
     */
    public static boolean canRecharge(int auraAvailable, int itemMissingCharge, int transferRate) {
        return auraAvailable > 0 && itemMissingCharge > 0 && transferRate > 0;
    }

    /**
     * Estimates remaining ticks to fully charge the item at the given transfer rate.
     * Returns 0 if item is already charged, or Integer.MAX_VALUE if transferRate <= 0.
     * Uses 64-bit arithmetic to prevent 32-bit signed integer overflow when itemMissingCharge is large.
     */
    public static int calculateRemainingTicks(int itemMissingCharge, int transferRate) {
        if (itemMissingCharge <= 0) return 0;
        if (transferRate <= 0) return Integer.MAX_VALUE;
        long needed = ((long) itemMissingCharge + transferRate - 1L) / transferRate;
        return (int) Math.min((long) Integer.MAX_VALUE, needed);
    }

    /**
     * Calculates the missing charge on an item given max charge and current charge.
     */
    public static int calculateMissingCharge(int maxCharge, int currentCharge) {
        if (maxCharge <= 0) return 0;
        int charge = Math.max(0, currentCharge);
        return Math.max(0, maxCharge - charge);
    }

    /**
     * Calculates the effective whole integer charge to add to an item from actual drained vis,
     * bounded by the transfer rate. Returns 0 if drained vis is less than 1.0 or transferRate <= 0.
     */
    public static int calculateEffectiveCharge(int transferRate, float actualDrainedVis) {
        if (transferRate <= 0 || Float.isNaN(actualDrainedVis) || actualDrainedVis < 1.0f) {
            return 0;
        }
        return Math.min(transferRate, (int) actualDrainedVis);
    }
}
