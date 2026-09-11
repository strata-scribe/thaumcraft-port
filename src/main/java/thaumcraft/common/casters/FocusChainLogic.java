package thaumcraft.common.casters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FocusChainLogic {

    public interface ChainTarget {
        int getId();
        double getX();
        double getY();
        double getZ();
    }

    public static int calculateChainComplexity(int maxTargets) {
        return Math.max(2, (int)(1.5f * maxTargets));
    }

    public static float calculateFalloff(float initialPower, int jumpIndex) {
        float power = initialPower;
        for (int i = 0; i < jumpIndex; i++) {
            power *= 0.8f;
        }
        return power;
    }

    public static List<ChainTarget> getSecondaryTargets(List<ChainTarget> availableTargets, ChainTarget primaryTarget, int maxSecondary, double radius) {
        List<ChainTarget> chain = new ArrayList<>();
        if (primaryTarget == null || availableTargets == null || availableTargets.isEmpty() || maxSecondary <= 0) {
            return chain;
        }

        Set<Integer> hitIds = new HashSet<>();
        hitIds.add(primaryTarget.getId());

        ChainTarget current = primaryTarget;
        double radiusSqr = radius * radius;

        for (int i = 0; i < maxSecondary; i++) {
            ChainTarget nextTarget = null;
            double minDistanceSqr = Double.MAX_VALUE;

            for (ChainTarget candidate : availableTargets) {
                if (hitIds.contains(candidate.getId())) {
                    continue;
                }

                double dx = candidate.getX() - current.getX();
                double dy = candidate.getY() - current.getY();
                double dz = candidate.getZ() - current.getZ();
                double distSqr = dx * dx + dy * dy + dz * dz;

                if (distSqr <= radiusSqr && distSqr < minDistanceSqr) {
                    minDistanceSqr = distSqr;
                    nextTarget = candidate;
                }
            }

            if (nextTarget == null) {
                break;
            }

            chain.add(nextTarget);
            hitIds.add(nextTarget.getId());
            current = nextTarget;
        }

        return chain;
    }
}
