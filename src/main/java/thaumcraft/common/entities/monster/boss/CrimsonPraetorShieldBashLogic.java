package thaumcraft.common.entities.monster.boss;

public class CrimsonPraetorShieldBashLogic {

    /**
     * Calculates the X and Z knockback velocity vectors based on the Praetor's yaw angle.
     * In Minecraft, yaw = 0 is +Z, yaw = 90 is -X, yaw = 180 is -Z, yaw = 270 is +X.
     *
     * @param praetorYawDegrees The yaw angle of the Praetor in degrees.
     * @param knockbackStrength The strength/magnitude of the knockback.
     * @return An array containing [xVelocity, zVelocity].
     */
    public static double[] calculateKnockbackVelocity(float praetorYawDegrees, double knockbackStrength) {
        double yawRadians = praetorYawDegrees * Math.PI / 180.0;
        double xVel = -Math.sin(yawRadians) * knockbackStrength;
        double zVel = Math.cos(yawRadians) * knockbackStrength;
        return new double[]{xVel, zVel};
    }

    /**
     * Calculates the shield stun duration in ticks based on the incoming blocked damage.
     *
     * @param incomingDamage The amount of damage blocked by the shield.
     * @return The stun duration in ticks, capped at a maximum value.
     */
    public static int calculateStunDurationTicks(float incomingDamage) {
        // 5 ticks of stun per point of damage, capped at 60 ticks (3 seconds)
        int ticks = (int) (incomingDamage * 5.0f);
        return Math.max(0, Math.min(60, ticks));
    }
}
