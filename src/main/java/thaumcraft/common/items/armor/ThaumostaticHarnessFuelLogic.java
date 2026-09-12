package thaumcraft.common.items.armor;

public class ThaumostaticHarnessFuelLogic {

    /**
     * Calculates the Potentia essentia consumption rate per second based on flight speed and vertical ascent.
     *
     * @param flightSpeed The horizontal flight speed.
     * @param verticalAscent The vertical ascent rate (positive values indicate upward movement).
     * @return The calculated Potentia consumption rate per second.
     */
    public static float calculatePotentiaConsumption(double flightSpeed, double verticalAscent) {
        float consumption = 1.0f; // Base consumption for hovering

        // Add consumption based on flight speed
        if (flightSpeed > 0) {
            consumption += (float) (2.0f * flightSpeed);
        }

        // Additional consumption for vertical ascent
        if (verticalAscent > 0) {
            consumption += (float) (5.0f * verticalAscent);
        }

        return consumption;
    }
}
