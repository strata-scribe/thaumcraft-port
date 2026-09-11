package thaumcraft.common.golems;

import java.util.Set;

/**
 * Decoupled logic for Golem Arms.
 * Evaluates arm part abilities:
 * - Claws: increases melee damage.
 * - Dart Launcher: ranged projectile attack.
 * - Fine Manipulators: stacks small items.
 * - Breaker: block breaking strength.
 * Zero Minecraft imports.
 */
public class GolemArmLogic {

    /**
     * Claws increase melee damage.
     * @param armTypes Set of arm keys/traits.
     * @return Bonus melee damage.
     */
    public static double getMeleeDamageBonus(Set<String> armTypes) {
        if (armTypes != null && armTypes.contains("claws")) {
            return 2.0;
        }
        return 0.0;
    }

    /**
     * Dart Launcher provides ranged attack capability.
     * @param armTypes Set of arm keys/traits.
     * @return True if the golem can perform ranged attacks.
     */
    public static boolean hasDartLauncher(Set<String> armTypes) {
        return armTypes != null && armTypes.contains("darts");
    }

    /**
     * Fine Manipulators increase the amount of items that can be stacked/carried.
     * @param armTypes Set of arm keys/traits.
     * @return Bonus to carrying capacity.
     */
    public static int getCarryCapacityBonus(Set<String> armTypes) {
        if (armTypes != null && armTypes.contains("fine")) {
            return 1;
        }
        return 0;
    }

    /**
     * Breaker arms increase block breaking strength / speed.
     * @param armTypes Set of arm keys/traits.
     * @return Multiplier for block breaking speed.
     */
    public static double getBlockBreakingMultiplier(Set<String> armTypes) {
        if (armTypes != null && armTypes.contains("breakers")) {
            return 2.0;
        }
        return 1.0;
    }
}
