package thaumcraft.common.items.armor;

/**
 * Pure mathematical logic and decoupled state rules for Void Robe Armor:
 * - Passive repair calculation based on local void aura.
 * - Damage resistance against warp attacks.
 *
 * PURE JAVA INVARIANT: This class must never import from net.minecraft or net.neoforged.
 */
public class VoidRobeArmorLogic {

    /**
     * Calculates the number of ticks required for a passive repair cycle.
     * Higher local void aura reduces the number of ticks needed, up to a limit.
     *
     * @param localVoidAura The amount of local void aura.
     * @return The number of ticks between repairs.
     */
    public static int calculatePassiveRepairTicks(int localVoidAura) {
        if (localVoidAura <= 0) {
            return 20;
        }
        // Reduce 1 tick per 5 void aura, down to a minimum of 5 ticks
        int reduction = localVoidAura / 5;
        return Math.max(5, 20 - reduction);
    }

    /**
     * Calculates the incoming damage after applying resistance from Void Robes.
     * Provides special resistance against warp attacks.
     *
     * @param incomingDamage The raw incoming damage.
     * @param isWarpAttack Whether the attack is a warp attack.
     * @param voidRobePieces The number of Void Robe pieces worn.
     * @return The calculated damage after resistance.
     */
    public static float calculateDamageResistance(float incomingDamage, boolean isWarpAttack, int voidRobePieces) {
        if (incomingDamage <= 0.0f) {
            return 0.0f;
        }

        if (!isWarpAttack || voidRobePieces <= 0) {
            return incomingDamage;
        }

        // 20% resistance per piece against warp attacks
        float resistanceMultiplier = 1.0f - (voidRobePieces * 0.20f);
        resistanceMultiplier = Math.max(0.0f, resistanceMultiplier);

        return incomingDamage * resistanceMultiplier;
    }
}
