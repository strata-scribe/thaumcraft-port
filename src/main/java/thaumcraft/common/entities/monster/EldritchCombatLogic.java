package thaumcraft.common.entities.monster;

/**
 * Pure Java logic helper for Eldritch Guardian combat mechanics.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class EldritchCombatLogic {

    /**
     * Calculates the healing amount for the Eldritch Guardian from its life drain beam.
     * Heals for 50% of the damage dealt.
     *
     * @param damageDealt The amount of damage dealt to the target.
     * @return The amount to heal.
     */
    public static float calculateLifeDrainHealing(float damageDealt) {
        if (damageDealt <= 0.0f) {
            return 0.0f;
        }
        return damageDealt * 0.5f;
    }

    /**
     * Determines whether the Eldritch Guardian should perform a shadow step.
     * Triggers when receiving heavy physical damage (>= 8.0f) and the damage is physical (not magical).
     *
     * @param incomingDamage The amount of incoming damage.
     * @param isPhysical     Whether the damage is physical.
     * @return true if the guardian should shadow step.
     */
    public static boolean shouldShadowStep(float incomingDamage, boolean isPhysical) {
        return isPhysical && incomingDamage >= 8.0f;
    }

    /**
     * Calculates the exact X, Y, Z coordinates directly behind the attacker based on their yaw.
     *
     * @param targetX   The attacker's X coordinate.
     * @param targetY   The attacker's Y coordinate.
     * @param targetZ   The attacker's Z coordinate.
     * @param targetYaw The attacker's yaw (in degrees).
     * @param distance  The distance to teleport behind the attacker.
     * @return A double array containing the new X, Y, and Z coordinates.
     */
    public static double[] calculateShadowStepCoordinates(double targetX, double targetY, double targetZ, float targetYaw, double distance) {
        // Convert yaw to radians. In Minecraft, yaw 0 is South (+Z), 90 is West (-X), 180 is North (-Z), 270 is East (+X).
        // The direction the player is looking at:
        // dx = -sin(yaw)
        // dz = cos(yaw)
        // To go behind the player, we go in the opposite direction (-dx, -dz) -> (sin(yaw), -cos(yaw)).
        double radYaw = Math.toRadians(targetYaw);

        // Offset coordinates.
        double offsetX = Math.sin(radYaw) * distance;
        double offsetZ = -Math.cos(radYaw) * distance;

        return new double[]{targetX + offsetX, targetY, targetZ + offsetZ};
    }
}
