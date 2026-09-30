package thaumcraft.common.tiles.devices;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VisRechargePedestalLogicTest {

    @Test
    public void testCalculateTransferRateNormal() {
        // Base transfer rate limits the transfer
        assertEquals(5, VisRechargePedestalLogic.calculateTransferRate(10, 10, 5));

        // Aura available limits the transfer
        assertEquals(3, VisRechargePedestalLogic.calculateTransferRate(3, 10, 5));

        // Item missing charge limits the transfer
        assertEquals(2, VisRechargePedestalLogic.calculateTransferRate(10, 2, 5));

        // Exact match
        assertEquals(5, VisRechargePedestalLogic.calculateTransferRate(5, 5, 5));
    }

    @Test
    public void testCalculateTransferRateZeroOrNegative() {
        assertEquals(0, VisRechargePedestalLogic.calculateTransferRate(0, 10, 5));
        assertEquals(0, VisRechargePedestalLogic.calculateTransferRate(10, 0, 5));
        assertEquals(0, VisRechargePedestalLogic.calculateTransferRate(10, 10, 0));

        assertEquals(0, VisRechargePedestalLogic.calculateTransferRate(-1, 10, 5));
        assertEquals(0, VisRechargePedestalLogic.calculateTransferRate(10, -5, 5));
        assertEquals(0, VisRechargePedestalLogic.calculateTransferRate(10, 10, -10));
    }

    @Test
    public void testApplyVisDiscountNormal() {
        assertEquals(9.0f, VisRechargePedestalLogic.applyVisDiscount(10.0f, 0.1f), 0.001f);
        assertEquals(5.0f, VisRechargePedestalLogic.applyVisDiscount(10.0f, 0.5f), 0.001f);
        assertEquals(10.0f, VisRechargePedestalLogic.applyVisDiscount(10.0f, 0.0f), 0.001f);
        assertEquals(0.0f, VisRechargePedestalLogic.applyVisDiscount(10.0f, 1.0f), 0.001f);
    }

    @Test
    public void testApplyVisDiscountOutOfBounds() {
        // Negative discount should be treated as 0
        assertEquals(10.0f, VisRechargePedestalLogic.applyVisDiscount(10.0f, -0.1f), 0.001f);

        // >100% discount should be treated as 100%
        assertEquals(0.0f, VisRechargePedestalLogic.applyVisDiscount(10.0f, 1.5f), 0.001f);
    }

    @Test
    public void testApplyVisDiscountZeroOrNegativeCost() {
        assertEquals(0.0f, VisRechargePedestalLogic.applyVisDiscount(0.0f, 0.1f), 0.001f);
        assertEquals(0.0f, VisRechargePedestalLogic.applyVisDiscount(-5.0f, 0.1f), 0.001f);
    }

    @Test
    public void testCanRecharge() {
        assertTrue(VisRechargePedestalLogic.canRecharge(10, 5));
        assertTrue(VisRechargePedestalLogic.canRecharge(1, 1));
        assertFalse(VisRechargePedestalLogic.canRecharge(0, 5));
        assertFalse(VisRechargePedestalLogic.canRecharge(10, 0));
        assertFalse(VisRechargePedestalLogic.canRecharge(-1, 5));
        assertFalse(VisRechargePedestalLogic.canRecharge(5, -2));
    }

    @Test
    public void testCalculateRemainingTicks() {
        // Exact division
        assertEquals(2, VisRechargePedestalLogic.calculateRemainingTicks(10, 5));
        // Ceiling division
        assertEquals(3, VisRechargePedestalLogic.calculateRemainingTicks(11, 5));
        assertEquals(1, VisRechargePedestalLogic.calculateRemainingTicks(1, 5));
        // Zero missing charge
        assertEquals(0, VisRechargePedestalLogic.calculateRemainingTicks(0, 5));
        assertEquals(0, VisRechargePedestalLogic.calculateRemainingTicks(-5, 5));
        // Zero or negative transfer rate
        assertEquals(Integer.MAX_VALUE, VisRechargePedestalLogic.calculateRemainingTicks(10, 0));
        assertEquals(Integer.MAX_VALUE, VisRechargePedestalLogic.calculateRemainingTicks(10, -1));
    }
}
