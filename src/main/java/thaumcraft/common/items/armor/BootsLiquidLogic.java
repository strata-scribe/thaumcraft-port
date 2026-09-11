package thaumcraft.common.items.armor;

/**
 * Pure mathematical and state logic for Boots of the Traveller liquid walking capabilities.
 *
 * PURE JAVA INVARIANT: This class must never import from net.minecraft or net.neoforged.
 */
public final class BootsLiquidLogic {

    private BootsLiquidLogic() {}

    /**
     * Determines if a player wearing charged boots can run across the surface of a liquid.
     * Sinking occurs if the player stops moving forward, stops sprinting, or sneaks.
     *
     * @param hasCharge       Whether the boots have enough charge to power the effect.
     * @param isMovingForward Whether the player has the forward movement key pressed.
     * @param isSneaking      Whether the player is currently sneaking (which intentionally forces sinking).
     * @param isSprinting     Whether the player is currently sprinting (liquid walking requires sprint speed).
     * @return true if the player should be kept atop the liquid surface.
     */
    public static boolean canWalkOnLiquid(boolean hasCharge, boolean isMovingForward, boolean isSneaking, boolean isSprinting) {
        return hasCharge && isMovingForward && isSprinting && !isSneaking;
    }

    /**
     * Calculates the required vertical velocity to keep the player atop the liquid surface
     * if they are actively walking on it and currently falling.
     *
     * @param currentMotionY The entity's current Y velocity.
     * @return The overridden Y velocity. Returns 0.0 if the entity was falling, keeping them level.
     */
    public static double calculateVerticalVelocityOverride(double currentMotionY) {
        if (currentMotionY < 0.0) {
            return 0.0;
        }
        return currentMotionY;
    }

    /**
     * Evaluates if the player's vertical coordinate intersects or sits above the liquid bounding box.
     * Includes a slight tolerance margin to account for movement fluctuations.
     *
     * @param playerY        The player's current Y coordinate.
     * @param liquidSurfaceY The Y coordinate of the liquid surface (usually block Y + liquid height).
     * @return true if the player is considered at or above the surface bounding box.
     */
    public static boolean isAtOrAboveLiquidSurface(double playerY, double liquidSurfaceY) {
        return playerY >= liquidSurfaceY - 0.1;
    }
}
