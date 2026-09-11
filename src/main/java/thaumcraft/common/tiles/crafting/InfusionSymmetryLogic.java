package thaumcraft.common.tiles.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Pure Java logic for evaluating infusion matrix symmetry.
 * Zero Minecraft imports for unit testing.
 */
public class InfusionSymmetryLogic {

    public static class Coordinate {
        public final int x;
        public final int y;
        public final int z;

        public Coordinate(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Coordinate)) return false;
            Coordinate that = (Coordinate) o;
            return x == that.x && y == that.y && z == that.z;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            result = 31 * result + z;
            return result;
        }
    }

    public interface IWorldScannable {
        Object getBlockType(Coordinate pos);
        float getStabilizationAmount(Coordinate pos);
        boolean hasSymmetryPenalty(Coordinate pos1, Coordinate pos2);
        float getSymmetryPenalty(Coordinate pos);
    }

    private float stabilityReplenish = 0.0f;
    private final List<Coordinate> problemBlocks = new ArrayList<>();
    private final Map<Object, Integer> tempBlockCount = new HashMap<>();

    public void evaluateSymmetry(Coordinate center, Set<Coordinate> stabilisers, IWorldScannable world) {
        Set<Coordinate> pending = new HashSet<>(stabilisers);

        while (!pending.isEmpty()) {
            Coordinate c1 = pending.iterator().next();

            // Mirror position across the matrix center
            int dx = center.x - c1.x;
            int dz = center.z - c1.z;
            Coordinate c2 = new Coordinate(center.x + dx, c1.y, center.z + dz);

            Object sb1 = world.getBlockType(c1);
            Object sb2 = world.getBlockType(c2);

            float amt1 = world.getStabilizationAmount(c1);
            float amt2 = world.getStabilizationAmount(c2);

            if (sb1 != null && sb1.equals(sb2) && amt1 == amt2) {
                if (world.hasSymmetryPenalty(c1, c2)) {
                    stabilityReplenish -= world.getSymmetryPenalty(c1);
                    problemBlocks.add(c1);
                } else {
                    stabilityReplenish += calcDiminishingReturns(sb1, amt1);
                }
            } else {
                stabilityReplenish -= Math.max(amt1, amt2);
                problemBlocks.add(c1);
            }

            pending.remove(c1);
            pending.remove(c2);
        }
    }

    private float calcDiminishingReturns(Object blockType, float base) {
        float bb = base;
        int c = tempBlockCount.getOrDefault(blockType, 0);
        if (c > 0) {
            bb *= (float) Math.pow(0.75, c);
        }
        tempBlockCount.put(blockType, c + 1);
        return bb;
    }

    public float getStabilityReplenish() {
        return stabilityReplenish;
    }

    public List<Coordinate> getProblemBlocks() {
        return problemBlocks;
    }
}
