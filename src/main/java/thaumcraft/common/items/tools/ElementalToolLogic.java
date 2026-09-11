package thaumcraft.common.items.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Pure Java simulation and mathematical logic for Thaumcraft Elemental Tools.
 * <p>
 * Strict architectural decoupling: contains zero net.minecraft or net.neoforged imports,
 * allowing instant JUnit 5 test execution without FML runtime classloaders.
 */
public class ElementalToolLogic {

    // =========================================================================
    // 1. Axe of the Stream — Tree Felling & Magnetism
    // =========================================================================

    public static final int MAX_TREE_FELL_LOGS = 128;
    public static final double DEFAULT_MAGNET_RADIUS = 8.0;
    public static final double MAX_MAGNET_SPEED = 0.45;

    public record BlockCoordinate(int x, int y, int z) {
        public double distanceTo(double px, double py, double pz) {
            double dx = x + 0.5 - px;
            double dy = y + 0.5 - py;
            double dz = z + 0.5 - pz;
            return Math.sqrt(dx * dx + dy * dy + dz * dz);
        }
    }

    public record Vector3D(double x, double y, double z) {
        public double length() {
            return Math.sqrt(x * x + y * y + z * z);
        }

        public Vector3D normalize() {
            double len = length();
            if (len < 1e-6) {
                return new Vector3D(0, 0, 0);
            }
            return new Vector3D(x / len, y / len, z / len);
        }

        public Vector3D scale(double factor) {
            return new Vector3D(x * factor, y * factor, z * factor);
        }
    }

    /**
     * Computes the attraction velocity vector pulling an item towards the player.
     *
     * @param itemX Item entity X coordinate
     * @param itemY Item entity Y coordinate
     * @param itemZ Item entity Z coordinate
     * @param playerX Player X coordinate
     * @param playerY Player eye Y coordinate
     * @param playerZ Player Z coordinate
     * @param maxRadius Maximum pull radius
     * @return 3D velocity vector to apply to the item entity
     */
    public static Vector3D calculateMagnetVelocity(
            double itemX, double itemY, double itemZ,
            double playerX, double playerY, double playerZ,
            double maxRadius) {

        double dx = playerX - itemX;
        double dy = playerY - itemY;
        double dz = playerZ - itemZ;
        double distSq = dx * dx + dy * dy + dz * dz;

        if (distSq > maxRadius * maxRadius || distSq < 0.05) {
            return new Vector3D(0, 0, 0);
        }

        double dist = Math.sqrt(distSq);
        double pullSpeed = Math.min(MAX_MAGNET_SPEED, 0.15 + (1.0 - (dist / maxRadius)) * 0.30);
        Vector3D dir = new Vector3D(dx, dy, dz).normalize();
        return dir.scale(pullSpeed);
    }

    /**
     * Evaluates log positions to fell in a tree, ordered from highest Y to lowest.
     */
    public static List<BlockCoordinate> filterAndSortTreeLogs(List<BlockCoordinate> connectedLogs) {
        if (connectedLogs == null || connectedLogs.isEmpty()) {
            return Collections.emptyList();
        }

        List<BlockCoordinate> result = new ArrayList<>(connectedLogs);
        if (result.size() > MAX_TREE_FELL_LOGS) {
            result = result.subList(0, MAX_TREE_FELL_LOGS);
        }

        // Felling proceeds from top to bottom
        result.sort(Comparator.comparingInt(BlockCoordinate::y).reversed());
        return result;
    }

    // =========================================================================
    // 2. Pickaxe of the Core — Ore Sounding & Cluster Yields
    // =========================================================================

    public record SoundedOre(BlockCoordinate pos, int rarityTier, double distance) {}

    public static final int ORE_TIER_COMMON = 1; // Coal, Iron, Copper
    public static final int ORE_TIER_PRECIOUS = 2; // Gold, Lapis, Redstone
    public static final int ORE_TIER_RARE = 3; // Diamond, Emerald, Ancient Debris
    public static final int ORE_TIER_THAUMIC = 4; // Cinnabar, Amber, Infused Crystals

    /**
     * Finds the most valuable / closest ore in a detected cluster list.
     */
    public static SoundedOre findPrioritizedOre(List<SoundedOre> detectedOres) {
        if (detectedOres == null || detectedOres.isEmpty()) {
            return null;
        }

        // Highest rarity tier first; if tied, closest distance
        return detectedOres.stream()
                .max(Comparator.comparingInt(SoundedOre::rarityTier)
                        .thenComparing(Comparator.comparingDouble(SoundedOre::distance).reversed()))
                .orElse(null);
    }

    /**
     * Calculates chance for bonus native cluster drops from Pickaxe of the Core.
     * Base chance 25%, increased by fortune level (10% per level).
     */
    public static boolean shouldDropNativeCluster(int fortuneLevel, double randomRoll01) {
        double chance = 0.25 + (fortuneLevel * 0.10);
        return randomRoll01 < Math.min(0.75, chance);
    }

    // =========================================================================
    // 3. Shovel of the Earthmover — 3x3 Grid Excavation & Placement
    // =========================================================================

    public enum BlockFace {
        DOWN, UP, NORTH, SOUTH, WEST, EAST
    }

    /**
     * Calculates the 3x3 plane of coordinates centered around the clicked block.
     * The orientation of the 3x3 plane is perpendicular to the clicked face normal.
     *
     * @param center Clicked block coordinate
     * @param face The face of the block that was hit
     * @return List of 9 coordinates forming the 3x3 grid
     */
    public static List<BlockCoordinate> calculate3x3Grid(BlockCoordinate center, BlockFace face) {
        List<BlockCoordinate> grid = new ArrayList<>(9);

        switch (face) {
            case UP:
            case DOWN:
                // Horizontal X-Z plane
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        grid.add(new BlockCoordinate(center.x + dx, center.y, center.z + dz));
                    }
                }
                break;
            case NORTH:
            case SOUTH:
                // Vertical X-Y plane
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        grid.add(new BlockCoordinate(center.x + dx, center.y + dy, center.z));
                    }
                }
                break;
            case WEST:
            case EAST:
                // Vertical Y-Z plane
                for (int dz = -1; dz <= 1; dz++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        grid.add(new BlockCoordinate(center.x, center.y + dy, center.z + dz));
                    }
                }
                break;
        }

        return grid;
    }

    // =========================================================================
    // 4. Sword of the Zephyr — Wind Sweep Impulse & Deflection
    // =========================================================================

    public static final double ZEPHYR_MAX_RADIUS = 6.0;
    public static final double ZEPHYR_BASE_IMPULSE = 1.25;

    /**
     * Calculates knockback impulse on an enemy entity caught in the Zephyr wind burst.
     */
    public static Vector3D calculateZephyrImpulse(
            double playerX, double playerY, double playerZ,
            double targetX, double targetY, double targetZ,
            double maxRadius) {

        double dx = targetX - playerX;
        double dy = targetY - playerY;
        double dz = targetZ - playerZ;
        double distSq = dx * dx + dy * dy + dz * dz;

        if (distSq > maxRadius * maxRadius || distSq < 1e-4) {
            return new Vector3D(0, 0, 0);
        }

        double dist = Math.sqrt(distSq);
        double force = ZEPHYR_BASE_IMPULSE * (1.0 - (dist / maxRadius));
        Vector3D horizontal = new Vector3D(dx, 0, dz).normalize().scale(force);
        return new Vector3D(horizontal.x, Math.max(0.35, force * 0.4), horizontal.z);
    }

    // =========================================================================
    // 5. Hoe of the Growth — Crop Growth Acceleration Area
    // =========================================================================

    /**
     * Calculates bonemeal fertilization chance for crops in the Hoe of Growth radius.
     * Direct target has 100% bonemeal effect; surrounding area has 40% chance.
     */
    public static boolean shouldAccelerateCropGrowth(double randomRoll01, boolean isCenterTarget) {
        return isCenterTarget || randomRoll01 < 0.40;
    }
}
