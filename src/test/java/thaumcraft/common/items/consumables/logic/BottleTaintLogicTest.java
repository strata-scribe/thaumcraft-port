package thaumcraft.common.items.consumables.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BottleTaintLogic Unit Tests")
public class BottleTaintLogicTest {

    @Test
    @DisplayName("Verify flux aura pollution calculation")
    void testCalculateFluxAuraPollution() {
        assertEquals(0, BottleTaintLogic.calculateFluxAuraPollution(0));
        assertEquals(5, BottleTaintLogic.calculateFluxAuraPollution(1));
        assertEquals(25, BottleTaintLogic.calculateFluxAuraPollution(5));
        assertEquals(0, BottleTaintLogic.calculateFluxAuraPollution(-1));
        assertEquals(0, BottleTaintLogic.calculateFluxAuraPollution(-10));
    }

    @Test
    @DisplayName("Verify taint spread radius calculation")
    void testCalculateTaintSpreadRadius() {
        assertEquals(0.0f, BottleTaintLogic.calculateTaintSpreadRadius(0), 1e-6f);
        assertEquals(2.5f, BottleTaintLogic.calculateTaintSpreadRadius(1), 1e-6f);
        assertEquals(5.0f, BottleTaintLogic.calculateTaintSpreadRadius(2), 1e-6f);
        assertEquals(12.5f, BottleTaintLogic.calculateTaintSpreadRadius(5), 1e-6f);
        assertEquals(0.0f, BottleTaintLogic.calculateTaintSpreadRadius(-1), 1e-6f);
        assertEquals(0.0f, BottleTaintLogic.calculateTaintSpreadRadius(-5), 1e-6f);
    }

    @Test
    @DisplayName("Verify taint poison duration ticks")
    void testGetTaintPoisonDurationTicks() {
        assertEquals(200, BottleTaintLogic.getTaintPoisonDurationTicks());
    }
}
