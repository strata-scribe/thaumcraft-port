package thaumcraft.common.tiles.crafting;

/**
 * Pure Java logic for Infusion Matrix hazard lightning strikes.
 * Fully decoupled from Minecraft/Forge APIs for unit testing.
 */
public class InfusionLightningLogic {

    public enum TargetType {
        PLAYER,
        PEDESTAL,
        BLOCK
    }

    /**
     * Determines if a lightning strike should occur based on instability factor and current cycle time.
     * @param instabilityFactor The current instability of the matrix.
     * @param currentCycleTime The current tick time in the infusion cycle.
     * @param randomFloat A random float between 0.0 and 1.0.
     * @return true if lightning should strike.
     */
    public static boolean shouldStrike(float instabilityFactor, int currentCycleTime, float randomFloat) {
        // Strike threshold is lowered if instability is high or cycle time is long
        float threshold = 0.05f + (instabilityFactor * 0.01f) + (currentCycleTime * 0.0001f);
        return randomFloat < threshold;
    }

    /**
     * Determines the target type for the lightning strike.
     * @param randomFloat A random float between 0.0 and 1.0.
     * @return The target type for the lightning strike.
     */
    public static TargetType determineTarget(float randomFloat) {
        if (randomFloat < 0.2f) {
            return TargetType.PLAYER;
        } else if (randomFloat < 0.5f) {
            return TargetType.PEDESTAL;
        } else {
            return TargetType.BLOCK;
        }
    }

    /**
     * Calculates the damage scaling for the lightning strike based on instability.
     * @param instabilityFactor The current instability of the matrix.
     * @param randomFloat A random float between 0.0 and 1.0.
     * @return The calculated damage.
     */
    public static float calculateDamage(float instabilityFactor, float randomFloat) {
        float baseDamage = 4.0f;
        float scaledDamage = baseDamage + (instabilityFactor * 0.5f) + (randomFloat * 4.0f);
        return Math.max(1.0f, scaledDamage); // Minimum 1 damage
    }
}
