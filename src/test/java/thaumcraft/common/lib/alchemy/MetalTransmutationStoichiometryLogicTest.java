package thaumcraft.common.lib.alchemy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MetalTransmutationStoichiometryLogicTest {

    private static final double DELTA = 1e-6;

    @Test
    public void testCalculateExactAspectYield_ValidInputs() {
        int inputMass = 10;
        double conversionEfficiency = 0.8;
        int catalystAmount = 5;

        double expectedYield = 10 * 0.8 * Math.log1p(5);
        double actualYield = MetalTransmutationStoichiometryLogic.calculateExactAspectYield(inputMass, conversionEfficiency, catalystAmount);

        assertEquals(expectedYield, actualYield, DELTA, "The calculated aspect yield should match the expected formula result.");
    }

    @Test
    public void testCalculateExactAspectYield_ZeroInputMass() {
        assertEquals(0.0, MetalTransmutationStoichiometryLogic.calculateExactAspectYield(0, 0.8, 5), DELTA);
        assertEquals(0.0, MetalTransmutationStoichiometryLogic.calculateExactAspectYield(-5, 0.8, 5), DELTA);
    }

    @Test
    public void testCalculateExactAspectYield_ZeroCatalystAmount() {
        assertEquals(0.0, MetalTransmutationStoichiometryLogic.calculateExactAspectYield(10, 0.8, 0), DELTA);
        assertEquals(0.0, MetalTransmutationStoichiometryLogic.calculateExactAspectYield(10, 0.8, -5), DELTA);
    }

    @Test
    public void testCalculateCatalystConversionRatio_ValidInputs() {
        int inputMass = 20;
        int catalystConsumed = 4;
        double baseRatio = 1.5;

        double expectedRatio = 1.5 * (20.0 / 4);
        double actualRatio = MetalTransmutationStoichiometryLogic.calculateCatalystConversionRatio(inputMass, catalystConsumed, baseRatio);

        assertEquals(expectedRatio, actualRatio, DELTA, "The calculated conversion ratio should match the expected formula result.");
    }

    @Test
    public void testCalculateCatalystConversionRatio_ZeroInputMass() {
        assertEquals(0.0, MetalTransmutationStoichiometryLogic.calculateCatalystConversionRatio(0, 4, 1.5), DELTA);
        assertEquals(0.0, MetalTransmutationStoichiometryLogic.calculateCatalystConversionRatio(-10, 4, 1.5), DELTA);
    }

    @Test
    public void testCalculateCatalystConversionRatio_ZeroCatalystConsumed() {
        assertEquals(0.0, MetalTransmutationStoichiometryLogic.calculateCatalystConversionRatio(20, 0, 1.5), DELTA);
        assertEquals(0.0, MetalTransmutationStoichiometryLogic.calculateCatalystConversionRatio(20, -4, 1.5), DELTA);
    }
}
