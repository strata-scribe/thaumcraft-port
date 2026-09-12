package thaumcraft.common.lib.alchemy;

public class MetalTransmutationStoichiometryLogic {

    /**
     * Calculates the exact aspect yield for a metal transmutation process.
     *
     * @param inputMass            The mass of the input metal.
     * @param conversionEfficiency The efficiency of the conversion (e.g., 0.8 for 80%).
     * @param catalystAmount       The amount of catalyst available.
     * @return The calculated exact aspect yield.
     */
    public static double calculateExactAspectYield(int inputMass, double conversionEfficiency, int catalystAmount) {
        if (inputMass <= 0 || catalystAmount <= 0) {
            return 0.0;
        }

        // Example formula: yield = inputMass * conversionEfficiency * log(catalystAmount + 1)
        return inputMass * conversionEfficiency * Math.log1p(catalystAmount);
    }

    /**
     * Calculates the catalyst conversion ratio for a metal transmutation process.
     *
     * @param inputMass        The mass of the input metal.
     * @param catalystConsumed The amount of catalyst consumed during the process.
     * @param baseRatio        The base conversion ratio.
     * @return The calculated catalyst conversion ratio.
     */
    public static double calculateCatalystConversionRatio(int inputMass, int catalystConsumed, double baseRatio) {
        if (inputMass <= 0 || catalystConsumed <= 0) {
            return 0.0;
        }

        // Example formula: ratio = baseRatio * (inputMass / catalystConsumed)
        return baseRatio * ((double) inputMass / catalystConsumed);
    }
}
