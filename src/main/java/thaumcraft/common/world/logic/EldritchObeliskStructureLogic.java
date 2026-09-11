package thaumcraft.common.world.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure Java logic helper for Eldritch Obelisk structure generation.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class EldritchObeliskStructureLogic {

    /**
     * Checks if relative coordinates (dx, dz) are within the dais platform bounds.
     * Platform radius is typically 3 (for a 7x7 platform: [-3, 3] on both axes).
     */
    public static boolean isObeliskPlatformBlock(int dx, int dz, int platformRadius) {
        return Math.abs(dx) <= platformRadius && Math.abs(dz) <= platformRadius;
    }

    /**
     * Checks if relative coordinates (dx, dz) correspond to one of the 4 corner pedestals.
     * For radius 3, pedestals are at (±3, ±3).
     */
    public static boolean isObeliskPedestal(int dx, int dz, int radius) {
        return Math.abs(dx) == radius && Math.abs(dz) == radius;
    }

    /**
     * Checks if relative coordinates (dx, dz) correspond to the central monolithic spire (0, 0).
     */
    public static boolean isObeliskSpireBlock(int dx, int dz) {
        return dx == 0 && dz == 0;
    }

    /**
     * Calculates central spire height (12 for obsidian pillar).
     */
    public static int getSpireHeight() {
        return 12;
    }

    /**
     * Checks if coordinate is the apex capstone at the top of the spire.
     */
    public static boolean isObeliskCapstone(int dx, int dy, int dz) {
        return dx == 0 && dz == 0 && dy == getSpireHeight() + 1;
    }

    /**
     * Checks if the stone block at relative coordinates (dx, dz) should be a glyphed stone.
     */
    public static boolean isGlyphCarvedStone(int dx, int dz) {
        return (Math.abs(dx) == 2 && Math.abs(dz) <= 2) || (Math.abs(dz) == 2 && Math.abs(dx) <= 2);
    }

    /**
     * Calculates structure bounding box [minX, minY, minZ, maxX, maxY, maxZ].
     *
     * @param platformRadius The radius of the platform.
     * @return Bounding box array
     */
    public static int[] calculateBoundingBox(int platformRadius) {
        int height = getSpireHeight() + 1; // 0 is platform, 1..12 is spire, 13 is capstone
        return new int[]{-platformRadius, 0, -platformRadius, platformRadius, height, platformRadius};
    }

    /**
     * Calculates and returns the relative offsets of all pieces in the structure.
     * Structure pieces include: platform, pedestals, spire, and capstone.
     *
     * @param platformRadius The radius of the platform.
     * @return A list of integer arrays [dx, dy, dz]
     */
    public static List<int[]> getPieceOffsets(int platformRadius) {
        List<int[]> offsets = new ArrayList<>();
        int height = getSpireHeight();

        // Platform (y = 0) and Pedestals (y = 1)
        for (int dx = -platformRadius; dx <= platformRadius; dx++) {
            for (int dz = -platformRadius; dz <= platformRadius; dz++) {
                if (isObeliskPlatformBlock(dx, dz, platformRadius)) {
                    offsets.add(new int[]{dx, 0, dz});
                }
                if (isObeliskPedestal(dx, dz, platformRadius)) {
                    offsets.add(new int[]{dx, 1, dz});
                }
            }
        }

        // Spire (y = 1 to height)
        for (int y = 1; y <= height; y++) {
            offsets.add(new int[]{0, y, 0});
        }

        // Capstone (y = height + 1)
        offsets.add(new int[]{0, height + 1, 0});

        return offsets;
    }
}
