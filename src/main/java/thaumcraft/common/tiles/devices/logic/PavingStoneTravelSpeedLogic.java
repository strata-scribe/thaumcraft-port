package thaumcraft.common.tiles.devices.logic;

/**
 * Pure Java logic helper for Paving Stone of Travel and Barrier Stone containment field mechanics.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class PavingStoneTravelSpeedLogic {

    /**
     * Calculates the speed boost multiplier for an entity walking on a Paving Stone of Travel.
     * The multiplier increases gradually up to a maximum cap.
     *
     * @param ticksOnStone The number of consecutive ticks the entity has been walking on the stone.
     * @return The speed boost multiplier.
     */
    public static double calculateSpeedBoostMultiplier(int ticksOnStone) {
        if (ticksOnStone < 0) return 1.0;
        // Cap the multiplier at 1.5x after 20 ticks (1 second)
        return 1.0 + Math.min(0.5, ticksOnStone * 0.025);
    }

    /**
     * Calculates the jump boost multiplier for an entity on a Paving Stone of Travel.
     *
     * @param ticksOnStone The number of consecutive ticks the entity has been on the stone.
     * @return The jump boost multiplier.
     */
    public static double calculateJumpBoostMultiplier(int ticksOnStone) {
        if (ticksOnStone < 0) return 1.0;
        // Cap the jump boost at 1.3x after 15 ticks
        return 1.0 + Math.min(0.3, ticksOnStone * 0.02);
    }

    /**
     * Calculates the deceleration or speed penalty when an entity approaches the edge of a Barrier Stone containment field.
     * As the entity gets closer to the barrier edge, their speed drops sharply.
     *
     * @param distanceToCenter The distance from the entity to the center of the containment field.
     * @param containmentRadius The maximum radius of the containment field.
     * @return A speed multiplier (from 0.0 to 1.0) representing the deceleration effect.
     */
    public static double calculateContainmentDeceleration(double distanceToCenter, double containmentRadius) {
        if (distanceToCenter < 0 || containmentRadius <= 0) return 1.0;
        if (distanceToCenter >= containmentRadius) return 0.0; // Completely stopped at or beyond edge

        // Starts slowing down when in the outer 20% of the radius
        double innerRadius = containmentRadius * 0.8;
        if (distanceToCenter <= innerRadius) {
            return 1.0;
        } else {
            // Gradually reduce speed from 1.0 down to 0.0 at the edge
            double penetration = (distanceToCenter - innerRadius) / (containmentRadius - innerRadius);
            return 1.0 - Math.pow(penetration, 2); // Quadratic deceleration
        }
    }
}
