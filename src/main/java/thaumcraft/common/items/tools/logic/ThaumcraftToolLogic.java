package thaumcraft.common.items.tools.logic;

/**
 * Pure, headless domain logic for Thaumcraft tools, weapons, and armor.
 * Contains zero Minecraft server or registry dependencies.
 */
public class ThaumcraftToolLogic {

    public static final int THAUMIUM_DURABILITY = 500;
    public static final int VOID_DURABILITY = 150;
    public static final int CRIMSON_BLADE_DURABILITY = 250;
    public static final int SCRIBING_TOOLS_DURABILITY = 100;

    public static final int THAUMIUM_ENCHANTABILITY = 22;
    public static final int VOID_ENCHANTABILITY = 10;

    public static final float THAUMIUM_DIG_SPEED = 7.0f;
    public static final float VOID_DIG_SPEED = 8.0f;

    public static final int VOID_TOOL_WARP = 1;
    public static final int CRIMSON_BLADE_WARP = 1;

    public static final int WEAKNESS_DURATION_TICKS = 60;
    public static final int THAUMIUM_ARMOR_DURABILITY_FACTOR = 25;

    public static int getThaumiumDurability() {
        return THAUMIUM_DURABILITY;
    }

    public static int getVoidDurability() {
        return VOID_DURABILITY;
    }

    public static int getCrimsonBladeDurability() {
        return CRIMSON_BLADE_DURABILITY;
    }

    public static int getScribingToolsDurability() {
        return SCRIBING_TOOLS_DURABILITY;
    }

    public static int getThaumiumEnchantability() {
        return THAUMIUM_ENCHANTABILITY;
    }

    public static int getVoidEnchantability() {
        return VOID_ENCHANTABILITY;
    }

    public static float getThaumiumDigSpeed() {
        return THAUMIUM_DIG_SPEED;
    }

    public static float getVoidDigSpeed() {
        return VOID_DIG_SPEED;
    }

    public static int getVoidToolWarp() {
        return VOID_TOOL_WARP;
    }

    public static int getCrimsonBladeWarp() {
        return CRIMSON_BLADE_WARP;
    }

    /**
     * Calculates void tool self-repair.
     * Repairs 1 point of durability every 20 ticks if damaged.
     */
    public static int calculateVoidSelfRepair(int currentDamage, int ticksExisted) {
        if (currentDamage <= 0) return 0;
        return (ticksExisted % 20 == 0) ? currentDamage - 1 : currentDamage;
    }

    /**
     * Determines whether weakness effect should be inflicted.
     */
    public static boolean shouldInflictWeakness(float damageDealt) {
        return damageDealt > 0.0f;
    }

    public static int getWeaknessDurationTicks() {
        return WEAKNESS_DURATION_TICKS;
    }

    public static int getThaumiumArmorDurabilityFactor() {
        return THAUMIUM_ARMOR_DURABILITY_FACTOR;
    }
}
