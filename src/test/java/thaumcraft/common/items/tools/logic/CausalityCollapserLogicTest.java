package thaumcraft.common.items.tools.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CausalityCollapserLogic Unit Tests")
public class CausalityCollapserLogicTest {

    @Test
    @DisplayName("Verify rift collapse condition with explosion power vs stability")
    void testCanCollapseRift() {
        assertTrue(CausalityCollapserLogic.canCollapseRift(5.0f, 10.0f),
                "Explosion power strictly greater than stability should collapse rift");
        assertTrue(CausalityCollapserLogic.canCollapseRift(5.0f, 5.0f),
                "Explosion power exactly equal to stability should collapse rift");
        assertFalse(CausalityCollapserLogic.canCollapseRift(5.0f, 4.99f),
                "Explosion power lower than stability should not collapse rift");
        assertFalse(CausalityCollapserLogic.canCollapseRift(20.0f, 1.0f),
                "Much lower explosion power should fail collapse");
    }

    @Test
    @DisplayName("Verify void seed drop count calculation across rift sizes and rolls")
    void testCalculateVoidSeedDropCount() {
        // Non-positive rift sizes yield zero drops
        assertEquals(0, CausalityCollapserLogic.calculateVoidSeedDropCount(0.0f, 0.1f));
        assertEquals(0, CausalityCollapserLogic.calculateVoidSeedDropCount(-1.0f, 0.4f));
        assertEquals(0, CausalityCollapserLogic.calculateVoidSeedDropCount(-10.0f, 0.9f));

        // Small rift (size < 5, base = 0, minimum 1 drop guaranteed)
        assertEquals(1, CausalityCollapserLogic.calculateVoidSeedDropCount(2.0f, 0.1f));
        assertEquals(1, CausalityCollapserLogic.calculateVoidSeedDropCount(2.0f, 0.9f));
        assertEquals(1, CausalityCollapserLogic.calculateVoidSeedDropCount(4.9f, 0.6f));

        // Medium rift (size = 12.0, base = 2)
        assertEquals(3, CausalityCollapserLogic.calculateVoidSeedDropCount(12.0f, 0.2f),
                "Roll < 0.5 gives extra bonus seed");
        assertEquals(2, CausalityCollapserLogic.calculateVoidSeedDropCount(12.0f, 0.8f),
                "Roll >= 0.5 gives base seeds");

        // Large rift (size = 25.0, base = 5)
        assertEquals(6, CausalityCollapserLogic.calculateVoidSeedDropCount(25.0f, 0.49f));
        assertEquals(5, CausalityCollapserLogic.calculateVoidSeedDropCount(25.0f, 0.50f));
        assertEquals(5, CausalityCollapserLogic.calculateVoidSeedDropCount(25.0f, 0.99f));
    }

    @Test
    @DisplayName("Verify explosion radius returns standard 4.0 blocks")
    void testGetExplosionRadius() {
        assertEquals(4.0f, CausalityCollapserLogic.getExplosionRadius(), 1e-6f);
    }
}
