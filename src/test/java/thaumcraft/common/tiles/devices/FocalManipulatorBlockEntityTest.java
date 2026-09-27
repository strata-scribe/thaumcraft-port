package thaumcraft.common.tiles.devices;

import org.junit.jupiter.api.Test;
import thaumcraft.common.casters.logic.FocalCraftingCostLogic;

import static org.junit.jupiter.api.Assertions.*;

class FocalManipulatorBlockEntityTest {

    @Test
    void testCalculateExpCostLogic() {
        // Test basic complexity based exp cost calculation (1:1 with min 1) directly through logic
        assertEquals(0, FocalCraftingCostLogic.calculateExpCost(0));
        assertEquals(1, FocalCraftingCostLogic.calculateExpCost(1));
        assertEquals(5, FocalCraftingCostLogic.calculateExpCost(5));
        assertEquals(10, FocalCraftingCostLogic.calculateExpCost(10));
    }

    @Test
    void testSlotConstants() {
        assertEquals(0, FocalManipulatorBlockEntity.FOCUS_SLOT, "Focus slot should be 0");
        assertEquals(6, FocalManipulatorBlockEntity.CRYSTAL_SLOTS, "Should be 6 crystal slots");
        assertEquals(7, FocalManipulatorBlockEntity.TOTAL_SLOTS, "Total slots should be 7");
    }
}
