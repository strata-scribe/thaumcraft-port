package thaumcraft.common.blocks.essentia.logic;

/**
 * Pure, headless domain logic for essentia tube connections, bitmask encoding,
 * straight-line pipe verification, and tube filter interaction rules.
 *
 * <p>Zero dependencies on Minecraft server or registries, enabling high-performance
 * headless unit testing.
 */
public final class TubeConnectionLogic {

    private TubeConnectionLogic() {
        // Utility class
    }

    /**
     * Toggles the open/closed state of a tube face.
     *
     * @param currentOpen current open status of the face
     * @return inverted open status
     */
    public static boolean toggleFace(boolean currentOpen) {
        return !currentOpen;
    }

    /**
     * Determines whether a connection can be established across a tube face.
     *
     * @param isFaceOpen whether this tube's face is configured as open
     * @param neighborConnectable whether the adjacent block/tile accepts essentia transport connection
     * @return true if both this face is open and the neighbor is connectable
     */
    public static boolean canConnect(boolean isFaceOpen, boolean neighborConnectable) {
        return isFaceOpen && neighborConnectable;
    }

    /**
     * Encodes 6-directional connectivity into a compact integer bitmask:
     * <ul>
     *   <li>bit 0 (1): North</li>
     *   <li>bit 1 (2): South</li>
     *   <li>bit 2 (4): East</li>
     *   <li>bit 3 (8): West</li>
     *   <li>bit 4 (16): Up</li>
     *   <li>bit 5 (32): Down</li>
     * </ul>
     *
     * @return 6-bit integer mask [0..63]
     */
    public static int encodeDirectionMask(boolean north, boolean south, boolean east, boolean west, boolean up, boolean down) {
        return (north ? 1 : 0) | (south ? 2 : 0) | (east ? 4 : 0) | (west ? 8 : 0) | (up ? 16 : 0) | (down ? 32 : 0);
    }

    /**
     * Checks if a direction ordinal (0..5) has its bit set in the given mask.
     *
     * @param mask direction bitmask
     * @param directionOrdinal ordinal index [0..5]
     * @return true if direction ordinal is valid [0..5] and corresponding bit is set
     */
    public static boolean isDirectionInMask(int mask, int directionOrdinal) {
        return directionOrdinal >= 0 && directionOrdinal < 6 && (mask & (1 << directionOrdinal)) != 0;
    }

    /**
     * Counts the number of connected faces among the 6 cardinal directions.
     *
     * @return count in range [0..6]
     */
    public static int countConnectedFaces(boolean north, boolean south, boolean east, boolean west, boolean up, boolean down) {
        int count = 0;
        if (north) count++;
        if (south) count++;
        if (east) count++;
        if (west) count++;
        if (up) count++;
        if (down) count++;
        return count;
    }

    /**
     * Evaluates if the tube forms an unbranched straight-through line along exactly one axis:
     * <ul>
     *   <li>North-South only</li>
     *   <li>East-West only</li>
     *   <li>Up-Down only</li>
     * </ul>
     *
     * @return true if straight along exactly one axis with no other connections
     */
    public static boolean isStraightThrough(boolean north, boolean south, boolean east, boolean west, boolean up, boolean down) {
        return (north && south && !east && !west && !up && !down) ||
               (east && west && !north && !south && !up && !down) ||
               (up && down && !north && !south && !east && !west);
    }

    /**
     * Checks if a player interaction can clear an existing essentia filter.
     *
     * @param isCrouching whether the player is crouching/sneaking
     * @param isHandEmpty whether the player's main hand is empty
     * @param hasFilter whether the tube currently has an aspect filter configured
     * @return true if crouching, hand empty, and filter is set
     */
    public static boolean canClearFilter(boolean isCrouching, boolean isHandEmpty, boolean hasFilter) {
        return isCrouching && isHandEmpty && hasFilter;
    }

    /**
     * Checks if a player can apply a new aspect filter to the tube.
     *
     * @param hasFilter whether the tube currently has an aspect filter configured
     * @param hasAspectItem whether the held item contains or represents an aspect
     * @return true if filter is not currently set and held item has an aspect
     */
    public static boolean canApplyFilter(boolean hasFilter, boolean hasAspectItem) {
        return !hasFilter && hasAspectItem;
    }
}
