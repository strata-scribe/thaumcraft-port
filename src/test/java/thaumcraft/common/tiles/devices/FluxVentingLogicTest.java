package thaumcraft.common.tiles.devices;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FluxVentingLogicTest {

    @Test
    void testCalculateVentingRate() {
        // Zero clean lattices
        assertEquals(0.0f, FluxVentingLogic.calculateVentingRate(0, 0, 100.0f));
        assertEquals(0.0f, FluxVentingLogic.calculateVentingRate(-1, 0, 100.0f));

        // Normal base aura, no clogged
        assertEquals(25.0f, FluxVentingLogic.calculateVentingRate(5, 0, 100.0f));

        // Clamped aura scaling
        assertEquals(12.5f, FluxVentingLogic.calculateVentingRate(5, 0, 10.0f)); // min 0.5f scaling
        assertEquals(50.0f, FluxVentingLogic.calculateVentingRate(5, 0, 300.0f)); // max 2.0f scaling

        // With clogged lattices
        assertEquals(22.0f, FluxVentingLogic.calculateVentingRate(5, 2, 100.0f));

        // Clogged overwhelm clean
        assertEquals(0.0f, FluxVentingLogic.calculateVentingRate(1, 10, 100.0f));

        // Zero or negative base aura
        assertEquals(25.0f, FluxVentingLogic.calculateVentingRate(5, 0, 0.0f));
        assertEquals(25.0f, FluxVentingLogic.calculateVentingRate(5, 0, -10.0f));
    }

    @Test
    void testCalculateLatticeWear() {
        // Zero or negative inputs
        assertEquals(0.0f, FluxVentingLogic.calculateLatticeWear(0.0f, 1));
        assertEquals(0.0f, FluxVentingLogic.calculateLatticeWear(-5.0f, 1));
        assertEquals(0.0f, FluxVentingLogic.calculateLatticeWear(10.0f, 0));
        assertEquals(0.0f, FluxVentingLogic.calculateLatticeWear(10.0f, -1));

        // Normal values
        assertEquals(0.2f, FluxVentingLogic.calculateLatticeWear(10.0f, 10));
        assertEquals(0.1f, FluxVentingLogic.calculateLatticeWear(10.0f, 20));
        assertEquals(2.0f, FluxVentingLogic.calculateLatticeWear(10.0f, 1));
    }

    @Test
    void testCalculateCleansingFootprint() {
        // Zero or negative inputs
        assertEquals(0, FluxVentingLogic.calculateCleansingFootprint(0, 10.0f));
        assertEquals(0, FluxVentingLogic.calculateCleansingFootprint(-1, 10.0f));
        assertEquals(0, FluxVentingLogic.calculateCleansingFootprint(5, 0.0f));
        assertEquals(0, FluxVentingLogic.calculateCleansingFootprint(5, -10.0f));

        // Normal calculation: sqrt(activeLattices * ventingRate)
        // sqrt(5 * 20.0f) = sqrt(100.0f) = 10
        assertEquals(10, FluxVentingLogic.calculateCleansingFootprint(5, 20.0f));

        // Clamping to max 64
        // sqrt(100 * 100.0f) = sqrt(10000.0f) = 100 -> clamps to 64
        assertEquals(64, FluxVentingLogic.calculateCleansingFootprint(100, 100.0f));

        // Clamping to min 1
        // sqrt(1 * 0.1f) = sqrt(0.1f) = 0.316 -> casts to 0 -> clamps to 1
        assertEquals(1, FluxVentingLogic.calculateCleansingFootprint(1, 0.1f));
    }
}
