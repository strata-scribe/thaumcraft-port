package thaumcraft.common.entities.monster.wisp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WispChainLightningLogic {

    /**
     * Represents a potential target for chain lightning.
     */
    public static class Target {
        public final int entityId;
        public final double x;
        public final double y;
        public final double z;

        public Target(int entityId, double x, double y, double z) {
            this.entityId = entityId;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    /**
     * Finds the secondary targets for a chain lightning attack.
     * The lightning jumps to the nearest valid target within the jump radius that hasn't been hit yet.
     *
     * @param initialTargetId The entity ID of the initial target hit by the lightning.
     * @param initialTargetPos The position [x, y, z] of the initial target.
     * @param potentialTargets A list of all potential targets in the vicinity.
     * @param jumpRadius The maximum distance the lightning can jump between targets.
     * @param maxBounces The maximum number of secondary targets the lightning can hit.
     * @return A list of the chosen secondary targets, in the order they are hit.
     */
    public static List<Target> findSecondaryTargets(int initialTargetId, double[] initialTargetPos, List<Target> potentialTargets, double jumpRadius, int maxBounces) {
        List<Target> selectedTargets = new ArrayList<>();
        Set<Integer> hitEntityIds = new HashSet<>();
        hitEntityIds.add(initialTargetId);

        double currentX = initialTargetPos[0];
        double currentY = initialTargetPos[1];
        double currentZ = initialTargetPos[2];

        double jumpRadiusSq = jumpRadius * jumpRadius;

        for (int i = 0; i < maxBounces; i++) {
            Target nearestTarget = null;
            double nearestDistSq = Double.MAX_VALUE;

            for (Target target : potentialTargets) {
                if (hitEntityIds.contains(target.entityId)) {
                    continue; // Already hit
                }

                double dx = target.x - currentX;
                double dy = target.y - currentY;
                double dz = target.z - currentZ;
                double distSq = dx * dx + dy * dy + dz * dz;

                if (distSq <= jumpRadiusSq && distSq < nearestDistSq) {
                    nearestDistSq = distSq;
                    nearestTarget = target;
                }
            }

            if (nearestTarget == null) {
                break; // No more valid targets within range
            }

            selectedTargets.add(nearestTarget);
            hitEntityIds.add(nearestTarget.entityId);
            currentX = nearestTarget.x;
            currentY = nearestTarget.y;
            currentZ = nearestTarget.z;
        }

        return selectedTargets;
    }

    /**
     * Computes the damage degradation for a specific bounce of the chain lightning.
     *
     * @param initialDamage The damage dealt to the initial target.
     * @param bounceIndex The index of the bounce (1 for the first secondary target, 2 for the next, etc.).
     * @param degradationFactor The multiplier applied to the damage per bounce (e.g., 0.8 for a 20% reduction per bounce).
     * @return The calculated damage for the specific bounce.
     */
    public static float computeDamageDegradation(float initialDamage, int bounceIndex, float degradationFactor) {
        if (bounceIndex < 0) {
            return initialDamage;
        }
        return initialDamage * (float) Math.pow(degradationFactor, bounceIndex);
    }
}
