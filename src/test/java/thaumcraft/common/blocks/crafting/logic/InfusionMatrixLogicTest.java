package thaumcraft.common.blocks.crafting.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InfusionMatrixLogicTest {

    @Test
    @DisplayName("calculateComparatorSignal: returns 15 when crafting, 0 when idle")
    public void testCalculateComparatorSignal() {
        assertEquals(15, InfusionMatrixLogic.calculateComparatorSignal(true));
        assertEquals(0, InfusionMatrixLogic.calculateComparatorSignal(false));
    }

    @Test
    @DisplayName("canActivateMatrix: permits normal clicks, forbids shift-clicks")
    public void testCanActivateMatrix() {
        // Normal click (not sneaking) -> allowed
        assertTrue(InfusionMatrixLogic.canActivateMatrix(false));

        // Shift click (sneaking) -> blocked
        assertFalse(InfusionMatrixLogic.canActivateMatrix(true));
    }

    @Test
    @DisplayName("getStabilityCategory: classifies stability correctly across boundaries")
    public void testStabilityCategories() {
        // Positive stability values
        assertEquals("VERY_STABLE", InfusionMatrixLogic.getStabilityCategory(25.0f));
        assertEquals("VERY_STABLE", InfusionMatrixLogic.getStabilityCategory(10.0f));
        assertEquals("VERY_STABLE", InfusionMatrixLogic.getStabilityCategory(0.1f));

        // Zero boundary
        assertEquals("VERY_STABLE", InfusionMatrixLogic.getStabilityCategory(0.0f));

        // STABLE range: [-5.0, 0.0)
        assertEquals("STABLE", InfusionMatrixLogic.getStabilityCategory(-0.1f));
        assertEquals("STABLE", InfusionMatrixLogic.getStabilityCategory(-2.5f));
        assertEquals("STABLE", InfusionMatrixLogic.getStabilityCategory(-5.0f));

        // UNSTABLE range: [-10.0, -5.0)
        assertEquals("UNSTABLE", InfusionMatrixLogic.getStabilityCategory(-5.01f));
        assertEquals("UNSTABLE", InfusionMatrixLogic.getStabilityCategory(-7.5f));
        assertEquals("UNSTABLE", InfusionMatrixLogic.getStabilityCategory(-10.0f));

        // VERY_UNSTABLE range: < -10.0
        assertEquals("VERY_UNSTABLE", InfusionMatrixLogic.getStabilityCategory(-10.01f));
        assertEquals("VERY_UNSTABLE", InfusionMatrixLogic.getStabilityCategory(-15.0f));
        assertEquals("VERY_UNSTABLE", InfusionMatrixLogic.getStabilityCategory(-100.0f));
    }
}
