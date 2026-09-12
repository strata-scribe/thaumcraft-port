package thaumcraft.common.items.armor;

/**
 * Logic class for Thaumostatic Harness physics simulation, decoupled from Minecraft/Forge dependencies.
 * Handles aerodynamic drag, hover stability, and sprint momentum dampening.
 */
public class ThaumostaticHarnessPhysicsLogic {

    /**
     * Simulates aerodynamic drag.
     * @param velocity The current velocity (X or Z axis).
     * @return The velocity after drag is applied.
     */
    public static double simulateAerodynamicDrag(double velocity) {
        return velocity * 0.95;
    }

    /**
     * Simulates hover stability.
     * @param currentMotionY The current vertical motion.
     * @param isHovering Whether the harness is actively hovering.
     * @return The new vertical motion.
     */
    public static double simulateHoverStability(double currentMotionY, boolean isHovering) {
        if (!isHovering) {
            return currentMotionY;
        }

        if (Math.abs(currentMotionY) < 0.01) {
            return 0.0;
        }

        return currentMotionY * 0.85;
    }

    /**
     * Simulates sprint momentum dampening when a player stops sprinting.
     * @param currentSpeed The current forward speed.
     * @param isSprinting Whether the player is currently sprinting.
     * @param wasSprinting Whether the player was sprinting in the previous tick.
     * @return The dampened speed.
     */
    public static double simulateSprintMomentumDampening(double currentSpeed, boolean isSprinting, boolean wasSprinting) {
        if (!isSprinting && wasSprinting) {
            return currentSpeed * 0.8;
        }
        return currentSpeed;
    }
}
