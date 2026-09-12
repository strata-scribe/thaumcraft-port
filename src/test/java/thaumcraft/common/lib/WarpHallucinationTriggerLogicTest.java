package thaumcraft.common.lib;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WarpHallucinationTriggerLogicTest {

    @Test
    public void testEvaluateGate() {
        assertEquals(WarpHallucinationTriggerLogic.WarpGate.NONE, WarpHallucinationTriggerLogic.evaluateGate(0));
        assertEquals(WarpHallucinationTriggerLogic.WarpGate.NONE, WarpHallucinationTriggerLogic.evaluateGate(9));

        assertEquals(WarpHallucinationTriggerLogic.WarpGate.MINOR, WarpHallucinationTriggerLogic.evaluateGate(10));
        assertEquals(WarpHallucinationTriggerLogic.WarpGate.MINOR, WarpHallucinationTriggerLogic.evaluateGate(29));

        assertEquals(WarpHallucinationTriggerLogic.WarpGate.NORMAL, WarpHallucinationTriggerLogic.evaluateGate(30));
        assertEquals(WarpHallucinationTriggerLogic.WarpGate.NORMAL, WarpHallucinationTriggerLogic.evaluateGate(49));

        assertEquals(WarpHallucinationTriggerLogic.WarpGate.MAJOR, WarpHallucinationTriggerLogic.evaluateGate(50));
        assertEquals(WarpHallucinationTriggerLogic.WarpGate.MAJOR, WarpHallucinationTriggerLogic.evaluateGate(100));
    }

    @Test
    public void testDetermineEventSpawnTimerMajor() {
        assertEquals(500, WarpHallucinationTriggerLogic.determineEventSpawnTimer(WarpHallucinationTriggerLogic.WarpGate.MAJOR, 0.0));
        assertEquals(750, WarpHallucinationTriggerLogic.determineEventSpawnTimer(WarpHallucinationTriggerLogic.WarpGate.MAJOR, 0.5));
        assertEquals(999, WarpHallucinationTriggerLogic.determineEventSpawnTimer(WarpHallucinationTriggerLogic.WarpGate.MAJOR, 0.999));
    }

    @Test
    public void testDetermineEventSpawnTimerNormal() {
        assertEquals(1000, WarpHallucinationTriggerLogic.determineEventSpawnTimer(WarpHallucinationTriggerLogic.WarpGate.NORMAL, 0.0));
        assertEquals(1500, WarpHallucinationTriggerLogic.determineEventSpawnTimer(WarpHallucinationTriggerLogic.WarpGate.NORMAL, 0.5));
        assertEquals(1999, WarpHallucinationTriggerLogic.determineEventSpawnTimer(WarpHallucinationTriggerLogic.WarpGate.NORMAL, 0.999));
    }

    @Test
    public void testDetermineEventSpawnTimerMinor() {
        assertEquals(2000, WarpHallucinationTriggerLogic.determineEventSpawnTimer(WarpHallucinationTriggerLogic.WarpGate.MINOR, 0.0));
        assertEquals(3000, WarpHallucinationTriggerLogic.determineEventSpawnTimer(WarpHallucinationTriggerLogic.WarpGate.MINOR, 0.5));
        assertEquals(3998, WarpHallucinationTriggerLogic.determineEventSpawnTimer(WarpHallucinationTriggerLogic.WarpGate.MINOR, 0.999));
    }

    @Test
    public void testDetermineEventSpawnTimerNone() {
        assertEquals(-1, WarpHallucinationTriggerLogic.determineEventSpawnTimer(WarpHallucinationTriggerLogic.WarpGate.NONE, 0.5));
    }

    @Test
    public void testDetermineEventSpawnTimerInvalidRandomFactor() {
        assertThrows(IllegalArgumentException.class, () -> {
            WarpHallucinationTriggerLogic.determineEventSpawnTimer(WarpHallucinationTriggerLogic.WarpGate.MAJOR, -0.1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            WarpHallucinationTriggerLogic.determineEventSpawnTimer(WarpHallucinationTriggerLogic.WarpGate.MAJOR, 1.0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            WarpHallucinationTriggerLogic.determineEventSpawnTimer(WarpHallucinationTriggerLogic.WarpGate.MAJOR, 1.5);
        });
    }
}
