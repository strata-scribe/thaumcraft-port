package thaumcraft.common.world;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.common.world.features.SilverwoodNodeCleansingLogic;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SilverwoodNodeCleansingLogicTest {

    @Test
    @DisplayName("Test flux purification rate - normal conditions")
    public void testCalculateFluxPurificationRate_Normal() {
        // nodeSize = 2, flux = 20.0
        // baseRate = 0.5 + 2 * 0.25 = 1.0
        assertEquals(1.0, SilverwoodNodeCleansingLogic.calculateFluxPurificationRate(2, 20.0), 0.001);
    }

    @Test
    @DisplayName("Test flux purification rate - high flux bonus")
    public void testCalculateFluxPurificationRate_HighFlux() {
        // nodeSize = 2, flux = 60.0 (> 50)
        // baseRate = 0.5 + 2 * 0.25 = 1.0 -> 1.0 * 1.5 = 1.5
        assertEquals(1.5, SilverwoodNodeCleansingLogic.calculateFluxPurificationRate(2, 60.0), 0.001);
    }

    @Test
    @DisplayName("Test flux purification rate - bounded by available flux")
    public void testCalculateFluxPurificationRate_Bounded() {
        // nodeSize = 10, flux = 1.0
        // baseRate = 0.5 + 10 * 0.25 = 3.0 -> min(3.0, 1.0) = 1.0
        assertEquals(1.0, SilverwoodNodeCleansingLogic.calculateFluxPurificationRate(10, 1.0), 0.001);
    }

    @Test
    @DisplayName("Test flux purification rate - zero and negative inputs")
    public void testCalculateFluxPurificationRate_EdgeCases() {
        assertEquals(0.0, SilverwoodNodeCleansingLogic.calculateFluxPurificationRate(0, 10.0), 0.001);
        assertEquals(0.0, SilverwoodNodeCleansingLogic.calculateFluxPurificationRate(-1, 10.0), 0.001);
        assertEquals(0.0, SilverwoodNodeCleansingLogic.calculateFluxPurificationRate(5, 0.0), 0.001);
        assertEquals(0.0, SilverwoodNodeCleansingLogic.calculateFluxPurificationRate(5, -5.0), 0.001);
    }

    @Test
    @DisplayName("Test pure aura radius - normal sizes")
    public void testCalculatePureAuraRadius_Normal() {
        assertEquals(10, SilverwoodNodeCleansingLogic.calculatePureAuraRadius(1));
        assertEquals(12, SilverwoodNodeCleansingLogic.calculatePureAuraRadius(2));
        assertEquals(18, SilverwoodNodeCleansingLogic.calculatePureAuraRadius(5));
    }

    @Test
    @DisplayName("Test pure aura radius - zero and negative node size")
    public void testCalculatePureAuraRadius_EdgeCases() {
        assertEquals(0, SilverwoodNodeCleansingLogic.calculatePureAuraRadius(0));
        assertEquals(0, SilverwoodNodeCleansingLogic.calculatePureAuraRadius(-5));
    }
}
