package thaumcraft.common.world.aura;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import thaumcraft.api.aura.AuraChunk;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class AuraDiffusionSimulationLogicTest {

    // Simple Coordinate wrapper for testing without minecraft imports
    record Coord(int x, int z) {}

    public void performDiffusionTest(Map<Coord, AuraChunk> chunks) {
        java.util.Set<Coord> activeChunks = new java.util.HashSet<>(chunks.keySet());

        for (Coord pos : activeChunks) {
            AuraChunk chunk = chunks.get(pos);
            if (chunk == null) continue;

            List<AuraChunk> neighbors = new ArrayList<>();
            // Diffuse to neighbors
            int[][] dirs = {{0, -1}, {0, 1}, {1, 0}, {-1, 0}};
            for (int[] dir : dirs) {
                Coord neighborPos = new Coord(pos.x + dir[0], pos.z + dir[1]);
                if (chunks.containsKey(neighborPos)) {
                    neighbors.add(chunks.get(neighborPos));
                }
            }

            AuraDiffusionSimulationLogic.diffuseChunk(chunk, neighbors);
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
