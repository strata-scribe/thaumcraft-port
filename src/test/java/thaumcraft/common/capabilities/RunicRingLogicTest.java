package thaumcraft.common.capabilities;

import org.junit.jupiter.api.Test;
import thaumcraft.common.lib.capabilities.RunicRingLogic;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RunicRingLogicTest {

    @Test
    public void testNoRings() {
        assertEquals(0, RunicRingLogic.calculateBonusShielding(0, 0));
    }

    @Test
    public void testSingleRing() {
        assertEquals(1, RunicRingLogic.calculateBonusShielding(1, 0));
        assertEquals(2, RunicRingLogic.calculateBonusShielding(0, 2));
    }

    @Test
    public void testDualRingsWithSynergy() {
        // Tier 1 and Tier 1: 1 + 1 + 1 (synergy) = 3
        assertEquals(3, RunicRingLogic.calculateBonusShielding(1, 1));

        // Tier 2 and Tier 1: 2 + 1 + 1 (synergy) = 4
        assertEquals(4, RunicRingLogic.calculateBonusShielding(2, 1));

        // Tier 3 and Tier 3: 3 + 3 + 1 (synergy) = 7
        assertEquals(7, RunicRingLogic.calculateBonusShielding(3, 3));
    }

    @Test
    public void testNegativeTiersIgnored() {
        // If a ring tier is somehow negative, we treat it as 0 (no bonus, no synergy)
        assertEquals(2, RunicRingLogic.calculateBonusShielding(-1, 2));
        assertEquals(1, RunicRingLogic.calculateBonusShielding(1, -2));
        assertEquals(0, RunicRingLogic.calculateBonusShielding(-1, -1));
    }
}
