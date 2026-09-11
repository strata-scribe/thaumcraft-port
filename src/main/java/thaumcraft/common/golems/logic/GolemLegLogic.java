package thaumcraft.common.golems.logic;

import java.util.Set;

/**
 * Pure Java logic class for evaluating Golem Leg modifiers.
 * Completely decoupled from Minecraft/Forge classes for isolated JUnit testing.
 */
public class GolemLegLogic {

    /**
     * The Stilt trait sets step height to 1.25 blocks.
     * @param baseStepHeight The base step height before modifiers.
     * @param traits The set of traits the golem possesses.
     * @return the modified step height.
     */
    public static float getStepHeight(float baseStepHeight, Set<String> traits) {
        if (traits == null) return baseStepHeight;
        for (String trait : traits) {
            if (trait != null && trait.equalsIgnoreCase("stilt")) {
                return 1.25f;
            }
        }
        return baseStepHeight;
    }

    /**
     * The Wheels trait multiplies speed by 1.5x on smooth surfaces.
     * @param isSmoothSurface Whether the current surface is smooth.
     * @param traits The set of traits the golem possesses.
     * @return the movement speed multiplier.
     */
    public static float getMovementSpeedMultiplier(boolean isSmoothSurface, Set<String> traits) {
        if (traits == null) return 1.0f;
        for (String trait : traits) {
            if (trait != null && (trait.equalsIgnoreCase("wheels") || trait.equalsIgnoreCase("wheeled"))) {
                if (isSmoothSurface) {
                    return 1.5f;
                }
            }
        }
        return 1.0f;
    }

    /**
     * The Climber trait allows ladder climbing.
     * @param traits The set of traits the golem possesses.
     * @return true if the golem can climb ladders.
     */
    public static boolean canClimb(Set<String> traits) {
        if (traits == null) return false;
        for (String trait : traits) {
            if (trait != null && trait.equalsIgnoreCase("climber")) {
                return true;
            }
        }
        return false;
    }

    /**
     * The Flier trait allows propeller flight over obstacles.
     * @param traits The set of traits the golem possesses.
     * @return true if the golem can fly.
     */
    public static boolean canFly(Set<String> traits) {
        if (traits == null) return false;
        for (String trait : traits) {
            if (trait != null && (trait.equalsIgnoreCase("flier") || trait.equalsIgnoreCase("flyer"))) {
                return true;
            }
        }
        return false;
    }
}
