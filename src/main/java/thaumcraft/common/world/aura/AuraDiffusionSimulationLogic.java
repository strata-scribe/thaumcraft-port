package thaumcraft.common.world.aura;

import java.util.List;
import thaumcraft.api.aura.AuraChunk;

public class AuraDiffusionSimulationLogic {

    public static void diffuseChunk(AuraChunk chunk, List<AuraChunk> neighbors) {
        float currentVis = chunk.getVis();
        float currentFlux = chunk.getFlux();

        for (AuraChunk neighbor : neighbors) {
            if (neighbor == null) continue;

            // Vis diffusion
            if (currentVis > neighbor.getVis()) {
                float diff = currentVis - neighbor.getVis();
                float amount = diff * 0.1f; // 10% of difference
                chunk.setVis(chunk.getVis() - amount);
                neighbor.setVis(neighbor.getVis() + amount);
                currentVis = chunk.getVis();
            }

            // Flux diffusion
            if (currentFlux > neighbor.getFlux()) {
                float diff = currentFlux - neighbor.getFlux();
                float amount = diff * 0.1f; // 10% of difference
                chunk.setFlux(chunk.getFlux() - amount);
                neighbor.setFlux(neighbor.getFlux() + amount);
                currentFlux = chunk.getFlux();
            }
        }

        // Flux dissipation / Corruption logic
        float fluxLimit = chunk.getBase() * 0.75f;
        float spilled = chunk.spillFlux(fluxLimit);
        if (spilled > 0) {
            chunk.setCorruption(chunk.getCorruption() + spilled);
        }
        chunk.degradeCorruption(0.01f); // Slowly decay corruption
    }
}
