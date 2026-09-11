package thaumcraft.common.entities.monster.boss;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CrimsonPortalWaveLogicTest {

    @Test
    public void testWaveCompositionIncreasingDifficulty() {
        // Wave 1
        CrimsonPortalWaveLogic.WaveComposition wave1 = CrimsonPortalWaveLogic.getWaveComposition(1);
        assertEquals(2, wave1.getKnights());
        assertEquals(0, wave1.getClerics());

        // Wave 2
        CrimsonPortalWaveLogic.WaveComposition wave2 = CrimsonPortalWaveLogic.getWaveComposition(2);
        assertEquals(2, wave2.getKnights()); // Still 2
        assertEquals(0, wave2.getClerics());

        // Wave 3 - Clerics should start appearing
        CrimsonPortalWaveLogic.WaveComposition wave3 = CrimsonPortalWaveLogic.getWaveComposition(3);
        assertEquals(3, wave3.getKnights()); // Increases
        assertEquals(1, wave3.getClerics());

        // Wave 5
        CrimsonPortalWaveLogic.WaveComposition wave5 = CrimsonPortalWaveLogic.getWaveComposition(5);
        assertEquals(4, wave5.getKnights());
        assertEquals(2, wave5.getClerics());

        // Ensure continuous increase
        CrimsonPortalWaveLogic.WaveComposition wave10 = CrimsonPortalWaveLogic.getWaveComposition(10);
        assertTrue(wave10.getKnights() > wave5.getKnights());
        assertTrue(wave10.getClerics() > wave5.getClerics());
    }

    @Test
    public void testWaveCompositionDecreasingInterval() {
        CrimsonPortalWaveLogic.WaveComposition wave1 = CrimsonPortalWaveLogic.getWaveComposition(1);
        CrimsonPortalWaveLogic.WaveComposition wave2 = CrimsonPortalWaveLogic.getWaveComposition(2);
        CrimsonPortalWaveLogic.WaveComposition wave15 = CrimsonPortalWaveLogic.getWaveComposition(15);
        CrimsonPortalWaveLogic.WaveComposition wave20 = CrimsonPortalWaveLogic.getWaveComposition(20);

        // Interval should decrease as wave increases
        assertTrue(wave2.getSpawnIntervalTicks() < wave1.getSpawnIntervalTicks());

        // Interval should bottom out at 100
        assertEquals(100, wave15.getSpawnIntervalTicks());
        assertEquals(100, wave20.getSpawnIntervalTicks());
    }

    @Test
    public void testDestructionFluxRelease() {
        assertEquals(10.0f, CrimsonPortalWaveLogic.getDestructionFluxRelease(0), 0.001f);
        assertEquals(15.0f, CrimsonPortalWaveLogic.getDestructionFluxRelease(1), 0.001f);
        assertEquals(35.0f, CrimsonPortalWaveLogic.getDestructionFluxRelease(5), 0.001f);

        // Test negative input handled correctly
        assertEquals(10.0f, CrimsonPortalWaveLogic.getDestructionFluxRelease(-5), 0.001f);
    }
}
