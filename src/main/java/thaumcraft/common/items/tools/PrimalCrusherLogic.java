package thaumcraft.common.items.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure Java simulation and mathematical logic for the Primal Crusher.
 * <p>
 * Strict architectural decoupling: contains zero net.minecraft or net.neoforged imports,
 * allowing instant JUnit 5 test execution without FML runtime classloaders.
 */

public class PrimalCrusherLogic {

    public record BlockCoordinate(int x, int y, int z) {}

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
                        grid.add(new BlockCoordinate(center.x() + dx, center.y(), center.z() + dz));
                    }
                }
                break;
            case NORTH:
            case SOUTH:
                // Vertical X-Y plane
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        grid.add(new BlockCoordinate(center.x() + dx, center.y() + dy, center.z()));
                    }
                }
                break;
            case WEST:
            case EAST:
                // Vertical Y-Z plane
                for (int dz = -1; dz <= 1; dz++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        grid.add(new BlockCoordinate(center.x(), center.y() + dy, center.z() + dz));
                    }
                }
                break;
        }

        return grid;
    }

    /**
     * Checks if the given block material is valid for the Primal Crusher's area effect
     * and effective tool speed (stone, dirt, sand, gravel).
     *
     * @param materialType The type of the material as a String (e.g. "stone", "dirt")
     * @return true if the material is effective for the Primal Crusher
     */
    public static boolean isEffectiveMaterial(String materialType) {
        if (materialType == null) {
            return false;
        }

        String lower = materialType.toLowerCase();
        return lower.contains("stone") ||
               lower.contains("dirt") ||
               lower.contains("sand") ||
               lower.contains("gravel");
    }

    /**
     * Calculates the extra damage inflicted by the Primal Crusher.
     * Inflicts an additional 50% damage (multiplier of 1.5) on eldritch and tainted entities.
     *
     * @param entityType The type/ID of the entity as a String (e.g. "eldritch_guardian")
     * @param baseDamage The base damage of the attack
     * @return The final damage after applying any bonus
     */
    public static float calculateDamageBonus(String entityType, float baseDamage) {
        if (entityType == null) {
            return baseDamage;
        }

        String lower = entityType.toLowerCase();
        if (lower.contains("eldritch") || lower.contains("taint")) {
            return baseDamage * 1.5f;
        }

        return baseDamage;
    }

}
