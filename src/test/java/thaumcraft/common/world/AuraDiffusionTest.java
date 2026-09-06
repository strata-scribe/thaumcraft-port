package thaumcraft.common.world;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import thaumcraft.api.aura.AuraChunk;
import java.util.Map;
import java.util.HashMap;

public class AuraDiffusionTest {

    // Simple Coordinate wrapper for testing without minecraft imports
    record Coord(int x, int z) {}

    public void performDiffusionTest(Map<Coord, AuraChunk> chunks) {
        // Direct replication of the diffusion math for testing
        java.util.Set<Coord> activeChunks = new java.util.HashSet<>(chunks.keySet());

        for (Coord pos : activeChunks) {
            AuraChunk chunk = chunks.get(pos);
            if (chunk == null) continue;

            float currentVis = chunk.getVis();
            float currentFlux = chunk.getFlux();

            // Diffuse to neighbors
            int[][] dirs = {{0, -1}, {0, 1}, {1, 0}, {-1, 0}};
            for (int[] dir : dirs) {
                Coord neighborPos = new Coord(pos.x + dir[0], pos.z + dir[1]);
                if (chunks.containsKey(neighborPos)) {
                    AuraChunk neighbor = chunks.get(neighborPos);

                    // Vis diffusion
                    if (currentVis > neighbor.getVis()) {
                        float diff = currentVis - neighbor.getVis();
                        float amount = diff * 0.1f;
                        chunk.setVis(chunk.getVis() - amount);
                        neighbor.setVis(neighbor.getVis() + amount);
                        currentVis = chunk.getVis();
                    }

                    // Flux diffusion
                    if (currentFlux > neighbor.getFlux()) {
                        float diff = currentFlux - neighbor.getFlux();
                        float amount = diff * 0.1f;
                        chunk.setFlux(chunk.getFlux() - amount);
                        neighbor.setFlux(neighbor.getFlux() + amount);
                        currentFlux = chunk.getFlux();
                    }
                }
            }

            // Flux dissipation
            float fluxLimit = chunk.getBase() * 0.75f;
            float spilled = chunk.spillFlux(fluxLimit);
            if (spilled > 0) {
                chunk.setCorruption(chunk.getCorruption() + spilled);
            }
            chunk.degradeCorruption(0.01f);
        }
    }

    @Test
    public void testVisDiffusion() {
        Map<Coord, AuraChunk> chunks = new HashMap<>();
        AuraChunk center = new AuraChunk((short) 100, 100.0f, 0.0f);
        chunks.put(new Coord(0, 0), center);

        AuraChunk north = new AuraChunk((short) 100, 0.0f, 0.0f);
        chunks.put(new Coord(0, -1), north);

        AuraChunk south = new AuraChunk((short) 100, 0.0f, 0.0f);
        chunks.put(new Coord(0, 1), south);

        AuraChunk east = new AuraChunk((short) 100, 0.0f, 0.0f);
        chunks.put(new Coord(1, 0), east);

        AuraChunk west = new AuraChunk((short) 100, 0.0f, 0.0f);
        chunks.put(new Coord(-1, 0), west);

        performDiffusionTest(chunks);

        assertTrue(center.getVis() < 100.0f);
        assertTrue(north.getVis() > 0.0f);
        assertTrue(south.getVis() > 0.0f);
        assertTrue(east.getVis() > 0.0f);
        assertTrue(west.getVis() > 0.0f);
    }

    @Test
    public void testFluxDiffusionAndCorruption() {
        Map<Coord, AuraChunk> chunks = new HashMap<>();
        AuraChunk center = new AuraChunk((short) 100, 0.0f, 200.0f);
        chunks.put(new Coord(0, 0), center);

        AuraChunk north = new AuraChunk((short) 100, 0.0f, 0.0f);
        chunks.put(new Coord(0, -1), north);

        performDiffusionTest(chunks);

        assertTrue(north.getFlux() > 0.0f);
        assertTrue(center.getCorruption() > 0.0f);
    }
}
