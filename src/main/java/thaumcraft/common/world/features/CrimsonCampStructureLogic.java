package thaumcraft.common.world.features;

/**
 * Pure Java logic helper for generating the Crimson Cultist Camp structure.
 * Decouples layout coordinates and bounds checks from Minecraft/NeoForge imports.
 */
public class CrimsonCampStructureLogic {

    /**
     * Radius for checking the area required for the camp (usually an 11x11 square => radius 5).
     */
    public static final int CAMP_RADIUS = 5;

    /**
     * Evaluates if a relative coordinate is part of the obsidian altar stand.
     * The stand is a 3x3 platform centered at (0,0) usually.
     * Or maybe just the center block. Usually an altar is at (0,0).
     * Let's define the center block (0,0) as the altar block itself.
     * And a 3x3 base if needed.
     */
    public static boolean isAltarStand(int dx, int dz) {
        return dx == 0 && dz == 0;
    }

    /**
     * Evaluates if a relative coordinate is part of the ritual circle.
     * Typically a ring. Radius 3 to 5.
     * Let's say outer radius 4.
     */
    public static boolean isRitualCircle(int dx, int dz) {
        int distSq = dx * dx + dz * dz;
        // Inner radius 3 (sq 9), outer 4 (sq 16)
        // distance approx 3-4 blocks. Let's use a circle bounds.
        // For example: 3 <= sqrt(dx^2 + dz^2) <= 4.5
        // Or exactly the radius 4 ring.
        // Let's implement a standard ring logic:
        // distance squared between 9 and 20 roughly?
        // We'll define exactly: radius 4 ring, no corners.
        // Abs(dx)==4 && Abs(dz)<=2 or Abs(dz)==4 && Abs(dx)<=2
        // Or distance squared logic.
        return distSq >= 9 && distSq <= 17;
    }

    /**
     * Evaluates if a relative coordinate is a Red Banner position.
     * Banners are typically at the 4 cardinal points of the circle or corners.
     * Let's say diagonals at radius 3: (3,3), (-3,3), (3,-3), (-3,-3)
     * Or cardinals at radius 4.
     */
    public static boolean isBannerPosition(int dx, int dz) {
        return Math.abs(dx) == 4 && Math.abs(dz) == 4;
    }

    /**
     * Evaluates if a relative coordinate is part of the Crimson Portal frame.
     * The frame is typically above the altar stand. Let's define it purely by dx, dz, dy if needed.
     * For 2D (dx, dz): just the center? Or 3x3?
     */
    public static boolean isPortalFrame(int dx, int dy, int dz) {
        // Simple 3x4 frame around center, standing vertically on Z axis?
        // Let's say dy from 1 to 3, dx in [-1, 1], dz = 0
        if (dz != 0) return false;
        if (dy < 1 || dy > 3) return false;
        if (dy == 1 || dy == 3) {
            return Math.abs(dx) <= 1; // Top and bottom frame
        } else {
            return Math.abs(dx) == 1; // Side frame
        }
    }

    /**
     * Determines if a biome is a surface biome suitable for a camp.
     * Since we can't use Biome types, we'll use boolean parameters.
     * @param hasCeiling true if the dimension has a ceiling (e.g. Nether).
     * @param isOcean true if it's an ocean biome.
     * @return true if it's suitable (no ceiling, not ocean).
     */
    public static boolean isValidSurfaceBiome(boolean hasCeiling, boolean isOcean) {
        return !hasCeiling && !isOcean;
    }
}
