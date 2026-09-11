package thaumcraft.common.world.features;

/**
 * Pure Java logic helper for Greatwood Tree features (trunk, leaf cluster spheres, and hollow chamber).
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class GreatwoodTreeGeneratorLogic {

    /**
     * Evaluates if a relative position (dx, dz) is within the 2x2 trunk pillar.
     * 2x2 trunk coordinates are (0,0), (1,0), (0,1), (1,1).
     *
     * @param dx Relative X offset
     * @param dz Relative Z offset
     * @return true if part of the trunk pillar
     */
    public static boolean isTrunkPillar(int dx, int dz) {
        return dx >= 0 && dx <= 1 && dz >= 0 && dz <= 1;
    }

    /**
     * Evaluates if a relative position (dx, dy, dz) is inside a leaf cluster sphere.
     * (dx*dx + dy*dy + dz*dz) <= radius * radius.
     *
     * @param dx Relative X offset from center of sphere
     * @param dy Relative Y offset from center of sphere
     * @param dz Relative Z offset from center of sphere
     * @param radius Radius of the leaf cluster sphere
     * @return true if inside the spherical leaf cluster
     */
    public static boolean isInsideLeafCluster(int dx, int dy, int dz, float radius) {
        return (dx * dx + dy * dy + dz * dz) <= (radius * radius);
    }

    /**
     * Evaluates the chance for a hollow trunk chamber.
     * e.g., 1 in 8 chance.
     *
     * @param randomInt A random integer roll
     * @param bound The bound for the roll (e.g., 8)
     * @return true if the roll indicates a hollow chamber (e.g., randomInt == 0)
     */
    public static boolean shouldGenerateHollowChamber(int randomInt, int bound) {
        return randomInt % bound == 0;
    }

    /**
     * Enumerates content types inside a hollow chamber.
     */
    public enum ChamberContent {
        SPAWNER, WEB, AIR, TRUNK
    }

    /**
     * Evaluates chamber block placement for a given relative position.
     * The hollow chamber is typically a 2x2 trunk pillar from y=0 to y=chamberHeight.
     * It places a spider spawner at the center-bottom, web around it, and air otherwise inside the hollow part.
     *
     * @param dx Relative X offset
     * @param dy Relative Y offset
     * @param dz Relative Z offset
     * @param chamberHeight Height of the chamber
     * @return the type of content for this position
     */
    public static ChamberContent getChamberContent(int dx, int dy, int dz, int chamberHeight) {
        if (!isTrunkPillar(dx, dz)) {
            return ChamberContent.TRUNK; // Outside the 2x2 trunk area, shouldn't really be called but default
        }

        // Base and top of chamber are solid trunk
        if (dy < 1 || dy >= chamberHeight - 1) {
            return ChamberContent.TRUNK;
        }

        // Inside the hollow area
        if (dy == 1) {
            if (dx == 0 && dz == 0) {
                return ChamberContent.SPAWNER;
            } else {
                return ChamberContent.WEB;
            }
        }

        if (dy == 2) {
            return ChamberContent.WEB;
        }

        return ChamberContent.AIR;
    }
}
