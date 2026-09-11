package thaumcraft.common.entities.monster.cult;

/**
 * Pure Java logic helper for Crimson Knight combat mechanics.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class CrimsonKnightCombatLogic {

    /**
     * Calculates the damage taken by a Crimson Knight when their shield is raised in phalanx stance.
     * Reduces incoming damage by 75% if the attack is from the front and shield is raised.
     *
     * @param incomingDamage The base incoming damage.
     * @param isShieldRaised Whether the knight's shield is currently raised.
     * @param isFrontal Whether the attack is coming from the frontal arc of the knight.
     * @return The resulting mitigated damage.
     */
    public static float calculatePhalanxDamage(float incomingDamage, boolean isShieldRaised, boolean isFrontal) {
        if (incomingDamage <= 0.0f) {
            return 0.0f;
        }
        if (isShieldRaised && isFrontal) {
            return incomingDamage * 0.25f;
        }
        return incomingDamage;
    }

    /**
     * Checks if an attacker is in front of the knight.
     * Uses a 90-degree cone (45 degrees to either side of the knight's look vector).
     *
     * @param knightYaw The knight's yaw rotation in degrees.
     * @param knightX The knight's X coordinate.
     * @param knightZ The knight's Z coordinate.
     * @param attackerX The attacker's X coordinate.
     * @param attackerZ The attacker's Z coordinate.
     * @return true if the attacker is within the frontal cone.
     */
    public static boolean isFrontal(float knightYaw, float knightX, float knightZ, float attackerX, float attackerZ) {
        float dx = attackerX - knightX;
        float dz = attackerZ - knightZ;

        // Calculate the angle to the attacker in degrees
        // Minecraft yaw is usually 0 at +Z, 90 at -X, 180 at -Z, 270 at +X
        // standard math atan2(dz, dx) has 0 at +X, 90 at +Z
        // So angleTo = atan2(-dx, dz) * 180 / PI for MC, but we can do a standard generic angle difference check
        float angleToAttacker = (float) (Math.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f;

        // Normalize yaw to -180 to 180
        float yaw = knightYaw % 360.0f;
        if (yaw < -180.0f) yaw += 360.0f;
        if (yaw > 180.0f) yaw -= 360.0f;

        float angleDiff = angleToAttacker - yaw;
        // Normalize angle difference to -180 to 180
        angleDiff = angleDiff % 360.0f;
        if (angleDiff < -180.0f) angleDiff += 360.0f;
        if (angleDiff > 180.0f) angleDiff -= 360.0f;

        return Math.abs(angleDiff) <= 45.0f;
    }

    /**
     * Calculates sweeping cleave damage for the Crimson Knight.
     *
     * @param baseDamage The base melee damage of the knight.
     * @return The sweeping cleave damage amount.
     */
    public static float calculateCleaveDamage(float baseDamage) {
        if (baseDamage <= 0.0f) {
            return 0.0f;
        }
        return baseDamage * 0.5f; // Sweep typically does 50% damage
    }

    /**
     * Checks if a target is within the cleave sweeping range and frontal angle.
     *
     * @param knightYaw The knight's yaw rotation in degrees.
     * @param knightX The knight's X coordinate.
     * @param knightZ The knight's Z coordinate.
     * @param targetX The target's X coordinate.
     * @param targetZ The target's Z coordinate.
     * @param cleaveRange The maximum range for the cleave sweep.
     * @return true if the target is within range and the frontal arc.
     */
    public static boolean isWithinCleave(float knightYaw, float knightX, float knightZ, float targetX, float targetZ, float cleaveRange) {
        float dx = targetX - knightX;
        float dz = targetZ - knightZ;
        float distSq = dx * dx + dz * dz;

        if (distSq > cleaveRange * cleaveRange) {
            return false;
        }

        // Cleave could have a wider arc, e.g., 180 degrees (90 degrees to either side)
        float angleToTarget = (float) (Math.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f;

        float yaw = knightYaw % 360.0f;
        if (yaw < -180.0f) yaw += 360.0f;
        if (yaw > 180.0f) yaw -= 360.0f;

        float angleDiff = angleToTarget - yaw;
        angleDiff = angleDiff % 360.0f;
        if (angleDiff < -180.0f) angleDiff += 360.0f;
        if (angleDiff > 180.0f) angleDiff -= 360.0f;

        // Using 90 degrees on either side for a wide cleave sweep arc
        return Math.abs(angleDiff) <= 90.0f;
    }
}
