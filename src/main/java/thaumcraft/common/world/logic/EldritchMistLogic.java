package thaumcraft.common.world.logic;

/**
 * Pure Java logic helper for Eldritch Mist.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class EldritchMistLogic {

    /**
     * Calculates the current radius of the dread mist based on active time.
     *
     * @param ticksActive The number of ticks the mist has been active.
     * @param expansionRate The rate at which the mist expands (radius units per tick).
     * @param maxRadius The maximum radius the mist can reach.
     * @return The current radius of the mist.
     */
    public static double calculateMistRadius(int ticksActive, double expansionRate, double maxRadius) {
        if (ticksActive < 0) {
            return 0.0;
        }
        double currentRadius = ticksActive * expansionRate;
        return Math.min(currentRadius, maxRadius);
    }

    /**
     * Calculates the sanity loss for an entity inside the mist.
     * Sanity loss is higher the closer the entity is to the center of the mist.
     *
     * @param distanceToCenter Distance of the entity from the mist's center.
     * @param currentRadius The current radius of the mist.
     * @param maxSanityLoss The maximum sanity loss (applied at the center).
     * @return The sanity loss amount. Returns 0.0 if outside the mist.
     */
    public static double calculateSanityLoss(double distanceToCenter, double currentRadius, double maxSanityLoss) {
        if (currentRadius <= 0.0 || distanceToCenter >= currentRadius) {
            return 0.0;
        }

        // Linear drop-off: 100% loss at the center, approaching 0% at the edge.
        double depthRatio = 1.0 - (distanceToCenter / currentRadius);
        return maxSanityLoss * depthRatio;
    }

    /**
     * Determines if an entity is currently engulfed by the mist.
     *
     * @param distanceToCenter Distance of the entity from the mist's center.
     * @param currentRadius The current radius of the mist.
     * @return True if the entity is inside the mist, false otherwise.
     */
    public static boolean isInsideMist(double distanceToCenter, double currentRadius) {
        return distanceToCenter < currentRadius && currentRadius > 0.0;
    }

    /**
     * Determines if sanity loss should be applied on the current tick (typically once per second).
     *
     * @param ticksInMist The consecutive number of ticks the entity has been inside the mist.
     * @return True if sanity loss should be applied this tick.
     */
    public static boolean shouldApplySanityLoss(int ticksInMist) {
        return ticksInMist > 0 && ticksInMist % 20 == 0;
    }
}
