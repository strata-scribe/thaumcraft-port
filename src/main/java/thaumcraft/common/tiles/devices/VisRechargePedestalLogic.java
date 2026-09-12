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
        if (rawVisCost <= 0) {
            return 0.0f;
        }
        float discount = Math.max(0.0f, Math.min(1.0f, visDiscount));
        return rawVisCost * (1.0f - discount);
    }
}
