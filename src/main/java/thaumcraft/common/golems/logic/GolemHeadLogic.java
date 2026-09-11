package thaumcraft.common.golems.logic;

import java.util.Set;

/**
 * Pure Java logic class for evaluating Golem Head modifiers.
 * Completely decoupled from Minecraft/Forge classes for isolated JUnit testing.
 */
public class GolemHeadLogic {

    /**
     * The Smart trait enables exact NBT/tag filtering.
     * @param traits The set of traits the golem possesses.
     * @return true if exact filtering is enabled.
     */
    public static boolean hasExactFiltering(Set<String> traits) {
        if (traits == null) return false;
        for (String trait : traits) {
            if (trait != null && trait.equalsIgnoreCase("smart")) {
                return true;
            }
        }
        return false;
    }

    /**
     * The Clueless trait restricts the golem to simple matching (disabling NBT checks or fuzzing).
     * @param traits The set of traits the golem possesses.
     * @return true if the golem uses simple matching.
     */
    public static boolean hasSimpleFiltering(Set<String> traits) {
        if (traits == null) return false;
        for (String trait : traits) {
            if (trait != null && trait.equalsIgnoreCase("clueless")) {
                return true;
            }
        }
        return false;
    }

    /**
     * The Scout trait doubles the golem's search and perception radius.
     * @param baseRadius The base radius before modifiers.
     * @param traits The set of traits the golem possesses.
     * @return the modified perception radius.
     */
    public static int calculatePerceptionRadius(int baseRadius, Set<String> traits) {
        if (traits == null) return baseRadius;
        for (String trait : traits) {
            if (trait != null && trait.equalsIgnoreCase("scout")) {
                return baseRadius * 2;
            }
        }
        return baseRadius;
    }

    /**
     * The Aggressive trait causes the golem to target and attack hostile entities.
     * @param traits The set of traits the golem possesses.
     * @return true if the golem attacks hostiles.
     */
    public static boolean isAggressive(Set<String> traits) {
        if (traits == null) return false;
        for (String trait : traits) {
            if (trait != null && trait.equalsIgnoreCase("aggressive")) {
                return true;
            }
        }
        return false;
    }
}
