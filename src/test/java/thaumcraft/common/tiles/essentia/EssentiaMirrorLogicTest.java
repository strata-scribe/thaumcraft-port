package thaumcraft.common.tiles.essentia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EssentiaMirrorLogicTest {

    private EssentiaMirrorLogic logic;

    @BeforeEach
    public void setup() {
        logic = new EssentiaMirrorLogic();
    }

    @Test
    public void testRegisterAndPairMirrors() {
        logic.registerMirror("m1");
        logic.registerMirror("m2");

        logic.pairMirrors("m1", "m2");

        // Set suction on m2 and verify it's broadcast to m1
        logic.setSuction("m2", "aer", 16);

        assertEquals("aer", logic.getBroadcastSuctionAspect("m1"));
        assertEquals(16, logic.getBroadcastSuctionAmount("m1"));
    }

    @Test
    public void testUnpairMirror() {
        logic.registerMirror("m1");
        logic.registerMirror("m2");
        logic.pairMirrors("m1", "m2");

        logic.setSuction("m2", "terra", 32);
        assertEquals("terra", logic.getBroadcastSuctionAspect("m1"));

        logic.unpairMirror("m1");

        assertNull(logic.getBroadcastSuctionAspect("m1"));
        assertEquals(0, logic.getBroadcastSuctionAmount("m1"));
        assertNull(logic.getBroadcastSuctionAspect("m2")); // Should unpair both ways
    }

    @Test
    public void testUnregisterMirrorUnpairs() {
        logic.registerMirror("m1");
        logic.registerMirror("m2");
        logic.pairMirrors("m1", "m2");

        logic.unregisterMirror("m1");

        assertNull(logic.getBroadcastSuctionAspect("m2"));
    }

    @Test
    public void testTransportEssentiaWithDelayAndCallback() {
        logic.registerMirror("m1");
        logic.registerMirror("m2");
        logic.pairMirrors("m1", "m2");

        boolean[] completed = {false};

        logic.sendEssentia("m1", "ignis", 5, () -> {
            completed[0] = true;
        });

        // Vis cost should accumulate on sender
        assertEquals(0.5f, logic.getPendingVisCost("m1"), 0.001f);
        assertEquals(0f, logic.getPendingVisCost("m2"), 0.001f);

        // Tick 4 times, should not be completed yet
        for (int i = 0; i < 4; i++) {
            logic.tickAll();
            assertFalse(completed[0], "Callback triggered too early at tick " + i);
        }

        // Tick 5th time, should trigger callback
        logic.tickAll();
        assertTrue(completed[0], "Callback not triggered after 5 ticks");
    }

    @Test
    public void testConsumeVisCost() {
        logic.registerMirror("m1");
        logic.registerMirror("m2");
        logic.pairMirrors("m1", "m2");

        logic.sendEssentia("m1", "aqua", 10, null);

        assertEquals(1.0f, logic.getPendingVisCost("m1"), 0.001f);

        float consumed = logic.consumeVisCost("m1");
        assertEquals(1.0f, consumed, 0.001f);
        assertEquals(0f, logic.getPendingVisCost("m1"), 0.001f);
    }
}
