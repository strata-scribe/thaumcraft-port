package thaumcraft.api.aura;

import org.junit.jupiter.api.Test;
import thaumcraft.common.world.aura.AuraDiffusionSimulationLogic;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuraDiffusionSimulationLogicTest {

    @Test
    void test3x3GridVisDiffusion() {
        // Center chunk with 500 vis
        AuraChunk centerChunk = new AuraChunk((short) 500, 500.0f, 0.0f);
        List<AuraChunk> neighbors = new ArrayList<>();

        // 8 neighbors with 100 vis
        for (int i = 0; i < 8; i++) {
            neighbors.add(new AuraChunk((short) 500, 100.0f, 0.0f));
        }

        AuraDiffusionSimulationLogic.diffuseChunk(centerChunk, neighbors);

        // Center chunk's vis should decrease
        assertTrue(centerChunk.getVis() < 500.0f, "Center chunk vis should decrease after diffusion");

        // Neighbor chunks' vis should increase
        for (AuraChunk neighbor : neighbors) {
            assertTrue(neighbor.getVis() > 100.0f, "Neighbor chunk vis should increase after diffusion");
        }
    }

    @Test
    void testConservationOfTotalVis() {
        AuraChunk centerChunk = new AuraChunk((short) 500, 500.0f, 0.0f);
        List<AuraChunk> neighbors = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            neighbors.add(new AuraChunk((short) 500, 100.0f, 0.0f));
        }

        float totalVisBefore = centerChunk.getVis();
        for (AuraChunk neighbor : neighbors) {
            totalVisBefore += neighbor.getVis();
        }

        AuraDiffusionSimulationLogic.diffuseChunk(centerChunk, neighbors);

        float totalVisAfter = centerChunk.getVis();
        for (AuraChunk neighbor : neighbors) {
            totalVisAfter += neighbor.getVis();
        }

        // Vis should be perfectly conserved
        assertEquals(totalVisBefore, totalVisAfter, 0.001f, "Total vis should be conserved after diffusion");
    }

    @Test
    void testFluxDiffusionRate() {
        // Base is 100. Flux limit will be 75.
        // Initial flux is 100 (which is above limit).
        // Initial corruption is 10.
        AuraChunk centerChunk = new AuraChunk((short) 100, 100.0f, 100.0f);
        centerChunk.setCorruption(10.0f);

        List<AuraChunk> neighbors = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            neighbors.add(new AuraChunk((short) 100, 100.0f, 0.0f));
        }

        // Calculate expected flux before dissipation, but after diffusion
        // Diffusion for flux: currentFlux = 100
        // for each neighbor (flux 0):
        // diff = currentFlux - 0
        // amount = diff * 0.1
        // chunk gets -amount, neighbor gets +amount

        // To precisely test thresholds, let's step through a simpler scenario with no neighbors.
        // Wait, the prompt says "Verify flux diffusion rate matches mathematical thresholds."
        // Let's test just the diffusion first, and then the dissipation.

        // Let's make a new isolated chunk for testing dissipation separately,
        // or we can just calculate what it should be.
        // If we want to strictly test the 10% rate:

        AuraChunk simpleCenter = new AuraChunk((short) 100, 0f, 100f);
        List<AuraChunk> simpleNeighbors = new ArrayList<>();
        AuraChunk singleNeighbor = new AuraChunk((short) 100, 0f, 0f);
        simpleNeighbors.add(singleNeighbor);

        AuraDiffusionSimulationLogic.diffuseChunk(simpleCenter, simpleNeighbors);

        // 1. Diffusion:
        // diff = 100 - 0 = 100
        // amount = 100 * 0.1 = 10
        // simpleCenter flux -> 90
        // singleNeighbor flux -> 10
        assertEquals(10.0f, singleNeighbor.getFlux(), 0.001f, "Neighbor should receive exactly 10% of flux difference");

        // 2. Dissipation / Corruption logic on simpleCenter
        // simpleCenter current flux = 90
        // limit = 100 * 0.75 = 75
        // spilled = 90 - 75 = 15
        // simpleCenter gets 15 corruption
        // flux is capped at 75
        assertEquals(75.0f, simpleCenter.getFlux(), 0.001f, "Flux should dissipate down to 75% of chunk base");

        // Corruption decay: it had 0, gained 15, then decays by 1% (0.01)
        // 15 * 0.01 = 0.15
        // Final corruption = 15 - 0.15 = 14.85
        assertEquals(14.85f, simpleCenter.getCorruption(), 0.001f, "Corruption should increase by spilled flux and decay by 1%");
    }
}
