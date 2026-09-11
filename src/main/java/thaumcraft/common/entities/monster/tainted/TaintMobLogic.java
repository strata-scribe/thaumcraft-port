package thaumcraft.common.entities.monster.tainted;

import net.minecraft.world.phys.Vec3;

public class TaintMobLogic {

    /**
     * Determines if a target is within the tentacle whip reach.
     * @param attackerPos The position of the attacker (Taintacle)
     * @param targetPos The position of the target
     * @param maxReach The maximum reach of the tentacle
     * @return true if the target is within reach, false otherwise
     */
    public static boolean isWithinWhipReach(Vec3 attackerPos, Vec3 targetPos, double maxReach) {
        return attackerPos.distanceToSqr(targetPos) <= maxReach * maxReach;
    }

    /**
     * Calculates the knockback impulse vector applied to a target hit by a Taintacle.
     * @param attackerPos The position of the attacker
     * @param targetPos The position of the target
     * @param strength The base strength of the knockback
     * @return A Vec3 representing the velocity impulse to add to the target
     */
    public static Vec3 calculateKnockbackImpulse(Vec3 attackerPos, Vec3 targetPos, double strength) {
        Vec3 diff = targetPos.subtract(attackerPos).normalize();
        return new Vec3(diff.x * strength, strength * 0.5, diff.z * strength);
    }

    /**
     * Calculates the damage a Taintacle inflicts, potentially scaling with distance.
     * @param baseDamage The base damage of the entity
     * @param distance The distance to the target
     * @return The final damage to apply
     */
    public static float calculateTentacleDamage(float baseDamage, double distance) {
        // Example: maybe more damage at the tip of the whip (further away)
        return (float) (baseDamage + Math.max(0, (distance - 2.0) * 0.5));
    }
}
