package thaumcraft.common.entities.logic;

/**
 * Pure Java logic helper for Eldritch Guardian life drain beam mechanics.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class EldritchGuardianDrainLogic {

    /**
     * Evaluates if the life drain beam connection distance is valid.
     *
     * @param distanceToTarget Current distance between the Guardian and the target
     * @param maxDrainRange The maximum allowed range for the beam to remain connected
     * @return true if the beam is connected, false otherwise
     */
    public static boolean isDrainBeamConnected(double distanceToTarget, double maxDrainRange) {
        return distanceToTarget >= 0.0 && distanceToTarget <= maxDrainRange;
    }

    /**
     * Computes the damage dealt by the life drain beam.
     * Often scales inversely with distance or remains fixed depending on design.
     * Here we implement a basic distance falloff model where damage scales linearly.
     *
     * @param baseDamage The maximum damage the beam can deal at point-blank range
     * @param distance The current distance to the target
     * @param maxRange The maximum range of the beam
     * @return The computed damage amount, bounded between 0 and baseDamage
     */
    public static float calculateLifeDrainDamage(float baseDamage, double distance, double maxRange) {
        if (distance >= maxRange || maxRange <= 0.0) {
            return 0.0f;
        }
        if (distance <= 0.0) {
            return baseDamage;
        }

        // Linear falloff
        float distanceRatio = (float) (distance / maxRange);
        float falloffFactor = 1.0f - distanceRatio;

        return Math.max(0.0f, baseDamage * falloffFactor);
    }

    /**
     * Computes how much health the Guardian recovers from the life drain.
     *
     * @param damageDealt The actual damage dealt to the target
     * @param conversionRate The percentage of damage converted to health (e.g., 0.5f for 50%)
     * @return The health recovered
     */
    public static float calculateHealthConversion(float damageDealt, float conversionRate) {
        if (damageDealt <= 0.0f || conversionRate <= 0.0f) {
            return 0.0f;
        }
        return damageDealt * conversionRate;
    }
}
