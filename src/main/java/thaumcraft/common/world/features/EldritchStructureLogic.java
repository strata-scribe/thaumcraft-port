package thaumcraft.common.world.features;

/**
 * Pure Java logic helper for procedural generation of Eldritch Obelisk and Mound structures.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class EldritchStructureLogic {

    // --- Eldritch Obelisk Procedural Geometry ---

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
     * Calculates central spire height. Per issue description, generates tall 12-block pillars.
     */
    public static int getObeliskSpireHeight() {
        return 12;
    }

    /**
     * Checks if coordinate is the apex capstone at the top of the spire.
     */
    public static boolean isObeliskCapstone(int dx, int dy, int dz, int spireHeight) {
        return dx == 0 && dz == 0 && dy == spireHeight;
    }

    /**
     * Checks if relative coordinates (dx, dz) correspond to the outer sinister monoliths.
     * Placed at cardinal directions outside the platform, e.g., (±5, 0) and (0, ±5).
     */
    public static boolean isSinisterMonolith(int dx, int dz, int distance) {
        return (Math.abs(dx) == distance && dz == 0) || (dx == 0 && Math.abs(dz) == distance);
    }

    // --- Eldritch Mound Procedural Geometry ---

    /**
     * Calculates outer chamber radius for Eldritch Mound (6 + rand3)
     */
    public static int calculateMoundRadius(int rand3) {
        return 6 + Math.abs(rand3 % 3);
    }

    /**
     * Checks if coordinate falls within the spherical mound chamber bounds.
     */
    public static boolean isInsideMoundChamber(int dx, int dy, int dz, int radius) {
        return (dx * dx + dy * dy + dz * dz) <= (radius * radius);
    }

    /**
     * Checks if the coordinate is the central loot chest in the mound.
     */
    public static boolean isMoundLootChest(int dx, int dy, int dz) {
        return dx == 0 && dy == 0 && dz == 0;
    }

    /**
     * Checks if the coordinate is one of the 4 cardinal spawner positions.
     */
    public static boolean isMoundSpawner(int dx, int dy, int dz) {
        return dy == 0 && ((Math.abs(dx) == 3 && dz == 0) || (dx == 0 && Math.abs(dz) == 3));
    }
}
