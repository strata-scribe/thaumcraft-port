package thaumcraft.api.aura;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuraChunkTest {

    @Test
    public void testRechargeVisLimits() {
        AuraChunk chunk = new AuraChunk((short) 100, 50.0f, 10.0f);

        chunk.rechargeVis(30.0f);
        assertEquals(80.0f, chunk.getVis(), 0.001f);

        chunk.rechargeVis(50.0f); // Should cap at base (100)
        assertEquals(100.0f, chunk.getVis(), 0.001f);
    }

    @Test
    public void testFluxSpilloverThresholds() {
        AuraChunk chunk = new AuraChunk((short) 100, 100.0f, 150.0f);

        float spilled = chunk.spillFlux(100.0f);
        assertEquals(50.0f, spilled, 0.001f);
        assertEquals(100.0f, chunk.getFlux(), 0.001f);

        spilled = chunk.spillFlux(100.0f); // Flux is already at threshold
        assertEquals(0.0f, spilled, 0.001f);
        assertEquals(100.0f, chunk.getFlux(), 0.001f);
    }

    @Test
    public void testCorruptionDegradationMath() {
        AuraChunk chunk = new AuraChunk((short) 100, 100.0f, 0.0f);
        chunk.setCorruption(10.0f);

        chunk.degradeCorruption(0.1f); // Reduce by 10%
        assertEquals(9.0f, chunk.getCorruption(), 0.001f);

        chunk.setCorruption(0.0005f); // Below minimal threshold
        chunk.degradeCorruption(0.1f);
        assertEquals(0.0f, chunk.getCorruption(), 0.001f);
    }
}
