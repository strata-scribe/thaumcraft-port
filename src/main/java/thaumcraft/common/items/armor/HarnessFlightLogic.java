package thaumcraft.common.items.armor;

public class HarnessFlightLogic {

    /**
     * Vis drain per second:
     * 1 charge/sec hovering, 2.5 charges/sec flying. 0 if inactive.
     * Sprinting multiplies drain by 3.0.
     */
    public static float calculateHarnessVisDrain(boolean isFlying, boolean isHovering, boolean isSprinting) {
        float baseDrain = 0.0f;
        if (isFlying) {
            baseDrain = 2.5f;
        } else if (isHovering) {
            baseDrain = 1.0f;
        }
        return isSprinting ? baseDrain * 3.0f : baseDrain;
    }

    /**
     * Vis drain per tick (20 ticks per second).
     */
    public static float calculateHarnessDrainPerTick(boolean isFlying, boolean isHovering, boolean isSprinting) {
        return calculateHarnessVisDrain(isFlying, isHovering, isSprinting) / 20.0f;
    }

    /**
     * Flight physics velocity damping:
     * Dampens current motion toward target speed with acceleration factor 0.15.
     */
    public static double calculateHarnessSpeed(double currentMotion, double targetSpeed) {
        double diff = targetSpeed - currentMotion;
        if (Math.abs(diff) < 0.001) {
            return targetSpeed;
        }
        return currentMotion + diff * 0.15;
    }

    /**
     * Vertical motion for flight:
     * Jump provides +0.15 thrust (up to +0.60).
     * Sneak provides -0.15 descent (down to -0.40).
     * Jump + Sneak cancel out to hover.
     * Hover dampens vertical motion (* 0.85), snapping to 0.0 when small (< 0.01).
     */
    public static double calculateHarnessVerticalMotion(double currentMotionY, boolean jumpHeld, boolean sneakHeld, boolean hoverActive) {
        if (jumpHeld && sneakHeld) {
            return hoverActive ? (Math.abs(currentMotionY) < 0.01 ? 0.0 : currentMotionY * 0.85) : currentMotionY;
        }
        if (jumpHeld) {
            return Math.min(0.60, currentMotionY + 0.15);
        }
        if (sneakHeld) {
            return Math.max(-0.40, currentMotionY - 0.15);
        }
        if (hoverActive) {
            return Math.abs(currentMotionY) < 0.01 ? 0.0 : currentMotionY * 0.85;
        }
        return currentMotionY;
    }

    /**
     * Dampens descent upon fuel depletion to prevent instant fall death (clamped to max -0.30).
     */
    public static double calculateHarnessDescentDamping(double currentMotionY) {
        return Math.max(-0.30, currentMotionY);
    }

    /**
     * Forward speed boost:
     * Multiplies current forward speed when sprinting.
     */
    public static double calculateForwardSpeedBoost(double currentSpeed, boolean isSprinting) {
        if (isSprinting) {
            return currentSpeed * 2.0;
        }
        return currentSpeed;
    }
}
