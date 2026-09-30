package thaumcraft.common.items.curios.logic;

public final class PrimordialPearlLogic {

    private PrimordialPearlLogic() {}

    public static int getMaxDurability() {
        return 8;
    }

    public static int getRemainingUses(int damage) {
        return Math.max(0, 8 - damage);
    }

    public static boolean isDepleted(int damage) {
        return damage >= 8;
    }

    public static boolean canCraft(int damage) {
        return damage < 8;
    }

    public static int applyCraftDamage(int damage) {
        return Math.min(8, Math.max(0, damage) + 1);
    }

    public static int calculateAuraCharge(int damage) {
        return Math.max(0, (8 - damage) * 25);
    }
}
