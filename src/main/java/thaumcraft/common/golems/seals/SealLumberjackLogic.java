package thaumcraft.common.golems.seals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Pure Java logic helper for Lumberjack Seal.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class SealLumberjackLogic {

    public static class Position {
        public final int x;
        public final int y;
        public final int z;

        public Position(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            if (x != position.x) return false;
            if (y != position.y) return false;
            return z == position.z;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            result = 31 * result + z;
            return result;
        }
    }

    public interface BlockValidator {
        boolean isLog(int x, int y, int z);
        boolean isSapling(int x, int y, int z);
    }

    /**
     * Scans for logs within designated forestry area.
     */
    public static List<Position> scanForLogs(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockValidator validator) {
        List<Position> logs = new ArrayList<>();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (validator.isLog(x, y, z)) {
                        logs.add(new Position(x, y, z));
                    }
                }
            }
        }
        return logs;
    }

    /**
     * Scans for saplings within designated forestry area.
     */
    public static List<Position> scanForSaplings(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockValidator validator) {
        List<Position> saplings = new ArrayList<>();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (validator.isSapling(x, y, z)) {
                        saplings.add(new Position(x, y, z));
                    }
                }
            }
        }
        return saplings;
    }

    /**
     * Dispatches wood cutting tasks from topmost log down.
     * Sorts the log coordinates so that highest Y coordinates are first.
     */
    public static void prioritizeTopDown(List<Position> logs) {
        logs.sort(new Comparator<Position>() {
            @Override
            public int compare(Position p1, Position p2) {
                // Higher Y comes first
                return Integer.compare(p2.y, p1.y);
            }
        });
    }
}
