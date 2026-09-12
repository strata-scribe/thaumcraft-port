package thaumcraft.common.casters.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FocalCraftingCostLogicTest {

    @Test
    @DisplayName("XP cost for complexity 0 should be 0")
    void testExpCostZeroComplexity() {
        assertEquals(0, FocalCraftingCostLogic.calculateExpCost(0));
    }

    @Test
    @DisplayName("XP cost for negative complexity should be 0")
    void testExpCostNegativeComplexity() {
        assertEquals(0, FocalCraftingCostLogic.calculateExpCost(-5));
    }

    @Test
    @DisplayName("XP cost for valid complexity values")
    void testExpCostValidComplexity() {
        assertEquals(1, FocalCraftingCostLogic.calculateExpCost(1));
        assertEquals(5, FocalCraftingCostLogic.calculateExpCost(5));
        assertEquals(15, FocalCraftingCostLogic.calculateExpCost(15));
        assertEquals(50, FocalCraftingCostLogic.calculateExpCost(50));
    }

    @Test
    @DisplayName("Crystal cost for complexity 0 should be 0")
    void testCrystalCostZeroComplexity() {
        assertEquals(0, FocalCraftingCostLogic.calculateCrystalCost(0));
    }

    @Test
    @DisplayName("Crystal cost for negative complexity should be 0")
    void testCrystalCostNegativeComplexity() {
        assertEquals(0, FocalCraftingCostLogic.calculateCrystalCost(-10));
    }

    @Test
    @DisplayName("Crystal cost for valid complexity values")
    void testCrystalCostValidComplexity() {
        // formula: Math.max(1, complexity / 5)
        assertEquals(1, FocalCraftingCostLogic.calculateCrystalCost(1));
        assertEquals(1, FocalCraftingCostLogic.calculateCrystalCost(4));
        assertEquals(1, FocalCraftingCostLogic.calculateCrystalCost(5));
        assertEquals(2, FocalCraftingCostLogic.calculateCrystalCost(10));
        assertEquals(3, FocalCraftingCostLogic.calculateCrystalCost(15));
        assertEquals(10, FocalCraftingCostLogic.calculateCrystalCost(50));
    }
}
