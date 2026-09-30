package thaumcraft.common.blocks.essentia.logic;

/**
 * Pure, headless domain logic methods for Essentia Smelter attachment validation.
 * Decoupled from Minecraft world, block state, and registry dependencies.
 */
public class SmelterAttachmentLogic {

    /**
     * Determines whether an attachment (such as an auxiliary pump or vent) can be attached to a smelter.
     * The attachment cannot be placed on the smelter's front face.
     *
     * @param attachedFace direction the attachment faces towards the smelter
     * @param smelterFacing horizontal facing direction of the smelter
     * @param isSmelter whether the adjacent block is actually a smelter
     * @return true if attachment is valid, false otherwise
     */
    public static boolean canAttachToSmelter(String attachedFace, String smelterFacing, boolean isSmelter) {
        if (!isSmelter || attachedFace == null || smelterFacing == null) return false;
        return !attachedFace.equalsIgnoreCase(smelterFacing);
    }

    /**
     * Checks if the given direction name corresponds to a valid horizontal attachment face.
     *
     * @param directionName the name of the direction (e.g. "north", "south", "east", "west")
     * @return true if valid horizontal attachment face, false otherwise
     */
    public static boolean isValidAttachmentFace(String directionName) {
        return "north".equalsIgnoreCase(directionName) || "south".equalsIgnoreCase(directionName) ||
               "east".equalsIgnoreCase(directionName) || "west".equalsIgnoreCase(directionName);
    }
}
