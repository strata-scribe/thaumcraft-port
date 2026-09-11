package thaumcraft.common.world.features;

/**
 * Pure Java logic helper for procedural tree generation and world structure geometry.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class WorldgenTreeLogic {

    // --- Greatwood Tree Procedural Geometry ---

    /**
     * Calculates Greatwood trunk height: 11 + rand11 (height between 11 and 21).
     *
     * @param rand11 Random value in range [0, 10]
     * @return Total trunk height [11, 21]
     */
    public static int calculateGreatwoodHeight(int rand11) {
        return 11 + Math.abs(rand11 % 11);
    }

    /**
     * Overload for raw addition without modulo clamping.
     */
    public static int calculateGreatwoodHeightRaw(int rand11) {
        return 11 + Math.max(0, rand11);
    }

    /**
     * Checks if Greatwood generates a subterranean spider dungeon (1-in-8 chance).
     *
     * @param roll8 Random roll in range [0, 7]
     * @return true if roll equals 0
     */
    public static boolean shouldSpawnSpiderDungeon(int roll8) {
        return (roll8 % 8) == 0;
    }

    /**
     * Alias for shouldSpawnSpiderDungeon.
     */
    public static boolean shouldSpawnGreatwoodSpiderNest(int roll8) {
        return shouldSpawnSpiderDungeon(roll8);
    }

    /**
     * Calculates Greatwood canopy layer sizing along the vertical axis.
     *
     * @param heightLimit Total tree height
     * @param currentY Current vertical level
     * @return Radius sizing factor, or negative if below canopy base
     */
    public static float calculateGreatwoodLayerSize(int heightLimit, int currentY) {
        if (currentY < heightLimit * 0.3f) {
            return -1.618f;
        }
        float h2 = heightLimit / 2.0f;
        float dy = h2 - currentY;
        if (dy == 0.0f) {
            return h2 * 0.5f;
        }
        if (Math.abs(dy) >= h2) {
            return 0.0f;
        }
        return (float) Math.sqrt(h2 * h2 - dy * dy) * 0.5f;
    }

    /**
     * Calculates base canopy radius (scaleWidth = 1.2f).
     */
    public static float calculateBaseCanopyRadius(float trunkRadius) {
        return trunkRadius * 1.2f;
    }

    /**
     * Calculates upper canopy radius (scaleWidth = 1.66f).
     */
    public static float calculateUpperCanopyRadius(float trunkRadius) {
        return trunkRadius * 1.66f;
    }

    /**
     * Checks if relative coordinates (dx, dz) lie within the base canopy radius.
     */
    public static boolean isInsideBaseCanopy(int dx, int dz, float baseRadius) {
        return (dx * dx + dz * dz) <= (baseRadius * baseRadius);
    }

    /**
     * Checks if relative coordinates (dx, dz) lie within the upper canopy radius.
     */
    public static boolean isInsideUpperCanopy(int dx, int dz, float upperRadius) {
        return (dx * dx + dz * dz) <= (upperRadius * upperRadius);
    }

    /**
     * Checks if relative coordinates (dx, dz) lie within canopy radius.
     */
    public static boolean isInsideGreatwoodCanopy(int dx, int dz, float radius) {
        return (dx * dx + dz * dz) <= (radius * radius);
    }

    /**
     * Dual canopy selector check.
     */
    public static boolean isInsideDualCanopy(int dx, int dz, float baseRadius, float upperRadius, boolean isUpper) {
        float r = isUpper ? upperRadius : baseRadius;
        return (dx * dx + dz * dz) <= (r * r);
    }


    // --- Silverwood Tree Procedural Geometry ---

    /**
     * Checks if relative coordinates (dx, dz) correspond to the cross-shaped trunk:
     * matches trunk core (0,0) or cardinal arms ((1,0), (-1,0), (0,1), (0,-1)).
     *
     * @param dx Relative X offset from trunk center
     * @param dz Relative Z offset from trunk center
     * @return true if the block is part of the cross-shaped trunk
     */
    public static boolean isSilverwoodTrunkBlock(int dx, int dz) {
        return (dx == 0 && dz == 0)
                || (Math.abs(dx) == 1 && dz == 0)
                || (dx == 0 && Math.abs(dz) == 1);
    }

    /**
     * Checks if relative coordinates (dx, dz) correspond to the 4 diagonal root buttresses at ground level:
     * ((1,1), (-1,1), (1,-1), (-1,-1)).
     *
     * @param dx Relative X offset from trunk center
     * @param dz Relative Z offset from trunk center
     * @return true if the block is a root buttress
     */
    public static boolean isSilverwoodButtressBlock(int dx, int dz) {
        return Math.abs(dx) == 1 && Math.abs(dz) == 1;
    }

    /**
     * Checks if relative coordinates (dx, dy, dz) fall within the Silverwood spherical foliage cloud:
     * (dx*dx + dy*dy + dz*dz) <= radiusSq
     *
     * @param dx Relative X offset
     * @param dy Relative Y offset
     * @param dz Relative Z offset
     * @param radiusSq Squared sphere radius
     * @return true if inside spherical foliage
     */
    public static boolean isInsideSilverwoodCanopy(int dx, int dy, int dz, int radiusSq) {
        return (dx * dx + dy * dy + dz * dz) <= radiusSq;
    }

    /**
     * Foliage spherical cloud check with random radius bonus.
     */
    public static boolean isSilverwoodCanopyBlock(int dx, int dy, int dz, int randomRadiusBonus) {
        double distSq = dx * dx + dy * dy + dz * dz;
        return distSq <= (10 + randomRadiusBonus);
    }

    /**
     * Calculates Silverwood trunk height: minHeight + (roll % varHeight).
     * Typically minHeight=7, varHeight=4 => height in [7, 10].
     */
    public static int calculateSilverwoodHeight(int minHeight, int varHeight, int roll) {
        return minHeight + (varHeight > 0 ? Math.abs(roll % varHeight) : 0);
    }

    /**
     * Convenience overload for standard Silverwood height (7 + rand4).
     */
    public static int calculateSilverwoodHeight(int rand4) {
        return 7 + Math.abs(rand4 % 4);
    }


}
