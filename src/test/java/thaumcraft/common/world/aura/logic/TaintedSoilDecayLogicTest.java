package thaumcraft.common.world.aura.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaintedSoilDecayLogicTest {

    @Test
    public void testCalculateDecayProgression_Base() {
        // Base increment is 1
        assertEquals(1, TaintedSoilDecayLogic.calculateDecayProgression(0, 0.0f, 100.0));
        assertEquals(51, TaintedSoilDecayLogic.calculateDecayProgression(50, 0.0f, 100.0));
    }

    @Test
    public void testCalculateDecayProgression_HighFlux() {
        // Increment is 1 (base) + 1 (>50) = 2
        assertEquals(2, TaintedSoilDecayLogic.calculateDecayProgression(0, 60.0f, 100.0));
        // Increment is 1 (base) + 1 (>50) + 1 (>150) = 3
        assertEquals(3, TaintedSoilDecayLogic.calculateDecayProgression(0, 160.0f, 100.0));
    }

    @Test
    public void testCalculateDecayProgression_CloseToSeed() {
        // Increment is 1 (base) + 1 (<=8) = 2
        assertEquals(2, TaintedSoilDecayLogic.calculateDecayProgression(0, 0.0f, 8.0));
        assertEquals(2, TaintedSoilDecayLogic.calculateDecayProgression(0, 0.0f, 6.0));

        // Increment is 1 (base) + 1 (<=8) + 1 (<=4) = 3
        assertEquals(3, TaintedSoilDecayLogic.calculateDecayProgression(0, 0.0f, 4.0));
        assertEquals(3, TaintedSoilDecayLogic.calculateDecayProgression(0, 0.0f, 2.0));
    }

    @Test
    public void testCalculateDecayProgression_Combined() {
        // High flux (>150) and very close (<=4)
        // Base: 1 + 2 (flux) + 2 (dist) = 5
        assertEquals(5, TaintedSoilDecayLogic.calculateDecayProgression(0, 200.0f, 3.0));
    }

    @Test
    public void testCalculateDecayProgression_MaxCapped() {
        // Should cap at MAX_PROGRESSION (100)
        assertEquals(100, TaintedSoilDecayLogic.calculateDecayProgression(99, 200.0f, 2.0));
        assertEquals(100, TaintedSoilDecayLogic.calculateDecayProgression(100, 0.0f, 100.0));
        assertEquals(100, TaintedSoilDecayLogic.calculateDecayProgression(105, 0.0f, 100.0));
    }

    @Test
    public void testCalculateSporeReleaseChance_SilkTouch() {
        // Any silk touch level should make it 0.0f
        assertEquals(0.0f, TaintedSoilDecayLogic.calculateSporeReleaseChance(50, 1), 0.001f);
        assertEquals(0.0f, TaintedSoilDecayLogic.calculateSporeReleaseChance(100, 2), 0.001f);
    }

    @Test
    public void testCalculateSporeReleaseChance_NoSilkTouch() {
        // Base chance is (progression / 100) * 0.50f
        assertEquals(0.0f, TaintedSoilDecayLogic.calculateSporeReleaseChance(0, 0), 0.001f);
        assertEquals(0.25f, TaintedSoilDecayLogic.calculateSporeReleaseChance(50, 0), 0.001f);
        assertEquals(0.50f, TaintedSoilDecayLogic.calculateSporeReleaseChance(100, 0), 0.001f);
    }
}
