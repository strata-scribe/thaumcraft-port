package thaumcraft.common.lib.research.theorycraft;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import thaumcraft.api.research.theorycraft.ResearchTableData;

public class TheorycraftCardExperimentationFocusTest {

    @Test
    public void testCalculateSuccessProbability() {
        assertEquals(0.4, CardExperimentationLogic.calculateSuccessProbability(0, 0), 0.001);
        assertEquals(0.45, CardExperimentationLogic.calculateSuccessProbability(1, 0), 0.001);
        assertEquals(0.42, CardExperimentationLogic.calculateSuccessProbability(0, 1), 0.001);
        assertEquals(0.47, CardExperimentationLogic.calculateSuccessProbability(1, 1), 0.001);
        assertEquals(0.9, CardExperimentationLogic.calculateSuccessProbability(10, 0), 0.001);
        assertEquals(0.6, CardExperimentationLogic.calculateSuccessProbability(0, 10), 0.001);
        assertEquals(1.0, CardExperimentationLogic.calculateSuccessProbability(15, 15), 0.001); // Caps at 1.0
    }

    @Test
    public void testComputeProgressPoints() {
        assertEquals(10, CardExperimentationLogic.computeProgressPoints(10, true));
        assertEquals(3, CardExperimentationLogic.computeProgressPoints(10, false));
        assertEquals(1, CardExperimentationLogic.computeProgressPoints(2, false));
        assertEquals(1, CardExperimentationLogic.computeProgressPoints(0, false));
    }

    @Test
    public void testComputeWarpPenalty() {
        assertEquals(0, CardExperimentationLogic.computeWarpPenalty(true, 10));
        assertEquals(5, CardExperimentationLogic.computeWarpPenalty(false, 10));
        assertEquals(1, CardExperimentationLogic.computeWarpPenalty(false, 1));
        assertEquals(1, CardExperimentationLogic.computeWarpPenalty(false, 0));
    }

    @Test
    public void testCardFocusAuromancySpecializationReward() {
        CardFocus card = new CardFocus();
        ResearchTableData data = new ResearchTableData(null);

        // Ensure starting state
        assertEquals(0, data.getTotal("AUROMANCY"));

        // Act
        boolean result = card.activate(null, data);

        // Assert
        assertTrue(result, "Activation should return true");
        assertEquals(15, data.getTotal("AUROMANCY"), "CardFocus should add 15 to the AUROMANCY category.");
    }
}
