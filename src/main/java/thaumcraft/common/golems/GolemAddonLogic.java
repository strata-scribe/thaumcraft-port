package thaumcraft.common.golems;

import java.util.Set;

/**
 * Decoupled logic for Golem Addons.
 * Evaluates addon accessories:
 * - Armor Plating: +4 armor
 * - Vis Reactor: self-charging runic shield
 * - Backpack: +1 inventory slot
 * - Fez: increased moral/health
 * - Top Hat: cosmetic/bonus
 * Zero Minecraft imports.
 */
public class GolemAddonLogic {

    /**
     * Armor Plating increases armor rating.
     * @param addonTypes Set of addon keys.
     * @return Bonus armor value.
     */
    public static double getArmorBonus(Set<String> addonTypes) {
        if (addonTypes != null && addonTypes.contains("armor")) {
            return 4.0;
        }
        return 0.0;
    }

    /**
     * Vis Reactor provides self-charging runic shield capability.
     * @param addonTypes Set of addon keys.
     * @return True if the golem has a Vis Reactor.
     */
    public static boolean hasVisReactor(Set<String> addonTypes) {
        return addonTypes != null && addonTypes.contains("vis_reactor");
    }

    /**
     * Backpack increases the amount of inventory slots.
     * @param addonTypes Set of addon keys.
     * @return Bonus to inventory slots.
     */
    public static int getInventorySlotBonus(Set<String> addonTypes) {
        if (addonTypes != null && addonTypes.contains("backpack")) {
            return 1;
        }
        return 0;
    }

    /**
     * Fez provides increased moral/health.
     * @param addonTypes Set of addon keys.
     * @return True if the golem has a Fez.
     */
    public static boolean hasFez(Set<String> addonTypes) {
        return addonTypes != null && addonTypes.contains("fez");
    }

    /**
     * Top Hat.
     * @param addonTypes Set of addon keys.
     * @return True if the golem has a Top Hat.
     */
    public static boolean hasTopHat(Set<String> addonTypes) {
        return addonTypes != null && addonTypes.contains("top_hat");
    }
}
