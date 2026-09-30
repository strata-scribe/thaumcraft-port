package thaumcraft.common.items.armor;

/**
 * Pure headless domain logic for Cloth Armor (Thaumaturge's Robes).
 * No Minecraft server or registry dependencies.
 */
public final class ClothArmorLogic {

    private ClothArmorLogic() {}

    /**
     * Calculates the total vis discount percentage provided by cloth armor pieces.
     * Each piece gives 3% vis discount, clamped between 0 and 100.
     *
     * @param pieceCount number of cloth armor pieces worn
     * @return discount percentage (0 - 100)
     */
    public static int calculateClothVisDiscount(int pieceCount) {
        return Math.min(100, Math.max(0, pieceCount * 3));
    }

    /**
     * Returns the durability factor for cloth armor (multiplier against base armor slot durability).
     *
     * @return cloth armor durability factor (8)
     */
    public static int getClothDurabilityFactor() {
        return 8;
    }

    /**
     * Returns the default dyed color for Thaumaturge's robes.
     * Default hex color: 0x6a3860 (purple-ish thaumaturge tint).
     *
     * @return default color RGB integer
     */
    public static int getDefaultClothColor() {
        return 0x6a3860;
    }
}
