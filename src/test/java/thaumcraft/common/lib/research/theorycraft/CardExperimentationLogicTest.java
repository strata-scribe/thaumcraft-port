package thaumcraft.common.lib.research.theorycraft;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CardExperimentationLogicTest {

    @Test
    public void testCalculateSuccessProbability() {
        // Base is 0.4
        assertEquals(0.4, CardExperimentationLogic.calculateSuccessProbability(0, 0), 0.001);

        // Knowledge adds 0.05
        assertEquals(0.45, CardExperimentationLogic.calculateSuccessProbability(1, 0), 0.001);

        // Inspiration adds 0.02
        assertEquals(0.42, CardExperimentationLogic.calculateSuccessProbability(0, 1), 0.001);

        // Both add together
        assertEquals(0.57, CardExperimentationLogic.calculateSuccessProbability(3, 1), 0.001);

        // Cap at 1.0
        assertEquals(1.0, CardExperimentationLogic.calculateSuccessProbability(20, 10), 0.001);

        // Floor at 0.0 (though logic shouldn't hit this with positive inputs, test robustness)
        assertEquals(0.0, CardExperimentationLogic.calculateSuccessProbability(-20, -10), 0.001);
    }

    @Test
    public void testComputeProgressPoints() {
        // Success keeps base points
        assertEquals(25, CardExperimentationLogic.computeProgressPoints(25, true));
        assertEquals(5, CardExperimentationLogic.computeProgressPoints(5, true));

        // Failure divides by 3
        assertEquals(8, CardExperimentationLogic.computeProgressPoints(25, false));
        assertEquals(1, CardExperimentationLogic.computeProgressPoints(5, false));

        // Minimum 1 on failure
        assertEquals(1, CardExperimentationLogic.computeProgressPoints(2, false));
    }

    @Test
    public void testComputeWarpPenalty() {
        // Success yields 0 penalty
        assertEquals(0, CardExperimentationLogic.computeWarpPenalty(true, 10));
        assertEquals(0, CardExperimentationLogic.computeWarpPenalty(true, 5));

        // Failure yields inspiration / 2, minimum 1
        assertEquals(5, CardExperimentationLogic.computeWarpPenalty(false, 10));
        assertEquals(2, CardExperimentationLogic.computeWarpPenalty(false, 5));
        assertEquals(1, CardExperimentationLogic.computeWarpPenalty(false, 1));
        assertEquals(1, CardExperimentationLogic.computeWarpPenalty(false, 0));
    }
}
