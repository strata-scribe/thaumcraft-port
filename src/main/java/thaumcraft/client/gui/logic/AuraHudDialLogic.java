package thaumcraft.client.gui.logic;

public class AuraHudDialLogic {

    /**
     * Calculates the target needle angle for the dial based on current and max amounts.
     * The angle is restricted to a certain range. Usually dials go from a min angle to a max angle.
     * Assuming 0 to 180 degrees or similar standard mapping, but we can make it customizable or fixed.
     * Let's say: 0 amount = minAngle, max amount = maxAngle.
     */
    public static float calculateNeedleAngle(float currentAmount, float maxAmount, float minAngle, float maxAngle) {
        if (maxAmount <= 0) return minAngle;
        float percentage = Math.max(0.0f, Math.min(1.0f, currentAmount / maxAmount));
        return minAngle + (percentage * (maxAngle - minAngle));
    }

    /**
     * Applies dampening spring physics to move the current angle smoothly towards the target angle.
     *
     * @param currentAngle The current angle of the needle.
     * @param targetAngle The angle the needle should point to.
     * @param velocity The current velocity of the needle.
     * @param springConstant How strong the spring is (e.g. 0.1f).
     * @param dampening How much dampening is applied to slow down the needle (e.g. 0.8f).
     * @return A float array containing the [newAngle, newVelocity].
     */
    public static float[] applySpringPhysics(float currentAngle, float targetAngle, float velocity, float springConstant, float dampening) {
        // F = -k * x
        float displacement = currentAngle - targetAngle;
        float force = -springConstant * displacement;

        // v = v + F
        float newVelocity = (velocity + force) * dampening;

        // pos = pos + v
        float newAngle = currentAngle + newVelocity;

        return new float[]{newAngle, newVelocity};
    }

    /**
     * Formats the percentage for the HUD, e.g., "75%".
     */
    public static String formatPercentage(float currentAmount, float maxAmount) {
        if (maxAmount <= 0) return "0%";
        float percentage = Math.max(0.0f, Math.min(1.0f, currentAmount / maxAmount));
        int roundedPercent = Math.round(percentage * 100);
        return roundedPercent + "%";
    }
}
