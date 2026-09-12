package thaumcraft.common.golems.logic;

import java.util.HashSet;
import java.util.Set;

/**
 * Pure Java logic class for resolving matrix interactions between Golem traits
 * and calculating final attributes based on those traits.
 * Decoupled from Minecraft/Forge APIs for isolated JUnit testing.
 */
public class GolemTraitMatrixLogic {

    public static class GolemAttributes {
        public double health;
        public double armor;
        public double speed;
        public double damage;
        public double knockbackRes;

        public GolemAttributes(double health, double armor, double speed, double damage, double knockbackRes) {
            this.health = health;
            this.armor = armor;
            this.speed = speed;
            this.damage = damage;
            this.knockbackRes = knockbackRes;
        }
    }

    /**
     * Resolves opposing traits in a given set, removing both if they conflict.
     * Opposing pairs are:
     * - deft vs clumsy
     * - heavy vs light
     * - fragile vs armored
     *
     * @param traits The original set of traits.
     * @return A new set of traits with opposing pairs removed.
     */
    public static Set<String> resolveOpposingTraits(Set<String> traits) {
        if (traits == null) {
            return new HashSet<>();
        }

        Set<String> resolved = new HashSet<>();
        for (String trait : traits) {
            if (trait != null) {
                resolved.add(trait.toLowerCase());
            }
        }

        // Deft vs Clumsy
        if (resolved.contains("deft") && resolved.contains("clumsy")) {
            resolved.remove("deft");
            resolved.remove("clumsy");
        }

        // Heavy vs Light
        if (resolved.contains("heavy") && resolved.contains("light")) {
            resolved.remove("heavy");
            resolved.remove("light");
        }

        // Fragile vs Armored
        if (resolved.contains("fragile") && resolved.contains("armored")) {
            resolved.remove("fragile");
            resolved.remove("armored");
        }

        return resolved;
    }

    /**
     * Calculates final attributes based on base values and resolved traits.
     *
     * @param baseHealth Base health
     * @param baseArmor Base armor
     * @param baseSpeed Base speed
     * @param baseDamage Base damage
     * @param baseKnockbackRes Base knockback resistance
     * @param traits The set of traits (preferably resolved)
     * @return The final calculated attributes.
     */
    public static GolemAttributes calculateAttributes(double baseHealth, double baseArmor, double baseSpeed, double baseDamage, double baseKnockbackRes, Set<String> traits) {
        double health = baseHealth;
        double armor = baseArmor;
        double speed = baseSpeed;
        double damage = baseDamage;
        double knockbackRes = baseKnockbackRes;

        Set<String> resolvedTraits = resolveOpposingTraits(traits);

        if (resolvedTraits.contains("light")) {
            speed *= 1.2;
        }
        if (resolvedTraits.contains("fragile")) {
            health *= 0.75;
        }
        if (resolvedTraits.contains("heavy")) {
            speed *= 0.8;
            knockbackRes += 0.5;
        }
        if (resolvedTraits.contains("armored")) {
            armor += 4;
        }
        if (resolvedTraits.contains("brutal")) {
            damage += 2;
        }

        return new GolemAttributes(health, armor, speed, damage, knockbackRes);
    }
}
