package thaumcraft.common.entities.monster.cult;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CrimsonPortalSpawningLogicTest {

    private static final long ONE_DAY = 24000L;

    @Test
    public void testLowWarpEarlyGame() {
        // Early game (1 day), low warp (10)
        CrimsonPortalSpawningLogic.CultistWaveMix mix = CrimsonPortalSpawningLogic.calculateWaveMix(10, ONE_DAY);
        assertEquals(2, mix.knights, "Should have 2 knights");
        assertEquals(1, mix.clerics, "Should have 1 cleric");
        assertEquals(0, mix.praetors, "Should have 0 praetors");
    }

    @Test
    public void testHighWarpEarlyGame() {
        // Early game (1 day), high warp (80)
        CrimsonPortalSpawningLogic.CultistWaveMix mix = CrimsonPortalSpawningLogic.calculateWaveMix(80, ONE_DAY);
        assertEquals(2, mix.knights, "Should have 2 knights");
        assertEquals(2, mix.clerics, "Should have 2 clerics");
        assertEquals(1, mix.praetors, "Should have 1 praetor due to high warp");
    }

    @Test
    public void testHighWarpLateGame() {
        // Late game (150 days), high warp (100)
        CrimsonPortalSpawningLogic.CultistWaveMix mix = CrimsonPortalSpawningLogic.calculateWaveMix(100, 150 * ONE_DAY);
        assertEquals(3, mix.knights, "Should have 3 knights");
        assertEquals(3, mix.clerics, "Should have 3 clerics");
        assertEquals(2, mix.praetors, "Should have 2 praetors");
    }

    @Test
    public void testLowWarpLateGame() {
        // Late game (150 days), low warp (10)
        CrimsonPortalSpawningLogic.CultistWaveMix mix = CrimsonPortalSpawningLogic.calculateWaveMix(10, 150 * ONE_DAY);
        assertEquals(2, mix.knights, "Should have 2 knights");
        assertEquals(1, mix.clerics, "Should have 1 cleric");
        assertEquals(0, mix.praetors, "Should have 0 praetors");
    }

    @Test
    public void testMediumWarpMediumGame() {
        // Mid game (60 days), medium warp (60)
        CrimsonPortalSpawningLogic.CultistWaveMix mix = CrimsonPortalSpawningLogic.calculateWaveMix(60, 60 * ONE_DAY);
        assertEquals(2, mix.knights, "Should have 2 knights");
        assertEquals(2, mix.clerics, "Should have 2 clerics");
        assertEquals(1, mix.praetors, "Should have 1 praetor");
    }
}
