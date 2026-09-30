package thaumcraft.common.items.armor;

/**
 * Headless domain logic for Crimson Cultist armor sets (Plate, Robe, Praetor).
 * Pure mathematical calculations with zero Minecraft server/registry dependencies.
 */
public final class CrimsonArmorLogic {

    private CrimsonArmorLogic() {
        // Utility class
    }

    public static int calculateCrimsonWarp(String armorFamily, int pieceCount) {
        if ("praetor".equalsIgnoreCase(armorFamily)) {
            return 2 * Math.max(0, pieceCount);
        }
        if ("robe".equalsIgnoreCase(armorFamily) || "plate".equalsIgnoreCase(armorFamily)) {
            return Math.max(0, pieceCount);
        }
        return 0;
    }

    public static int calculateVisDiscount(int robePieceCount) {
        return Math.min(100, Math.max(0, robePieceCount * 5));
    }

    public static boolean isCultistDisguised(int crimsonArmorCount) {
        return crimsonArmorCount >= 3;
    }

    public static boolean isPraetorLeader(boolean hasPraetorHelm, boolean hasPraetorChest) {
        return hasPraetorHelm && hasPraetorChest;
    }

    public static int getArmorDurabilityMultiplier(String armorFamily) {
        if ("praetor".equalsIgnoreCase(armorFamily)) {
            return 30;
        }
        if ("plate".equalsIgnoreCase(armorFamily)) {
            return 20;
        }
        if ("robe".equalsIgnoreCase(armorFamily)) {
            return 17;
        }
        return 15;
    }
}
