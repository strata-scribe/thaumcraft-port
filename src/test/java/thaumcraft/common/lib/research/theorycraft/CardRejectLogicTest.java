package thaumcraft.common.lib.research.theorycraft;

import org.junit.jupiter.api.Test;
import thaumcraft.common.lib.research.theorycraft.logic.CardRejectLogic;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class CardRejectLogicTest {

    @Test
    public void testComputeRejectionCost_Penalty() {
        int cost = CardRejectLogic.computeRejectionCost(1, 2, 0.5f);
        assertEquals(3, cost);
    }

    @Test
    public void testComputeRejectionCost_Refund() {
        int cost = CardRejectLogic.computeRejectionCost(1, 0, 0.05f);
        assertEquals(-1, cost);
    }

    @Test
    public void testComputeRejectionCost_RefundDiminishedByFatigue() {
        int cost = CardRejectLogic.computeRejectionCost(1, 10, 0.05f); // Refund chance is capped at 0.01
        assertEquals(11, cost);

        int costRefund = CardRejectLogic.computeRejectionCost(1, 10, 0.005f);
        assertEquals(-1, costRefund);
    }

    @Test
    public void testComputeRedrawProbabilityDistribution_Empty() {
        Map<String, Double> distribution = CardRejectLogic.computeRedrawProbabilityDistribution(Collections.emptyList(), 0);
        assertTrue(distribution.isEmpty());
    }

    @Test
    public void testComputeRedrawProbabilityDistribution_Normalization() {
        List<String> cards = Arrays.asList("CardA", "CardB", "CardC");
        Map<String, Double> distribution = CardRejectLogic.computeRedrawProbabilityDistribution(cards, 0);

        assertEquals(3, distribution.size());

        double total = 0;
        for (Double prob : distribution.values()) {
            total += prob;
        }
        assertEquals(1.0, total, 0.0001);
    }

    @Test
    public void testComputeRedrawProbabilityDistribution_FatigueFlattens() {
        List<String> cards = Arrays.asList("CardA", "CardB", "CardC");
        Map<String, Double> distributionLowFatigue = CardRejectLogic.computeRedrawProbabilityDistribution(cards, 0);
        Map<String, Double> distributionHighFatigue = CardRejectLogic.computeRedrawProbabilityDistribution(cards, 10);

        // High fatigue should flatten the distribution (equal weights of 1.0)
        assertEquals(1.0/3.0, distributionHighFatigue.get("CardA"), 0.0001);
        assertEquals(1.0/3.0, distributionHighFatigue.get("CardB"), 0.0001);
        assertEquals(1.0/3.0, distributionHighFatigue.get("CardC"), 0.0001);

        // Low fatigue should give CardA higher probability than CardB, etc.
        assertTrue(distributionLowFatigue.get("CardA") > distributionLowFatigue.get("CardB"));
        assertTrue(distributionLowFatigue.get("CardB") > distributionLowFatigue.get("CardC"));
    }

    @Test
    public void testComputeFatigueEscalation() {
        assertEquals(1, CardRejectLogic.computeFatigueEscalation(0, 0));
        assertEquals(3, CardRejectLogic.computeFatigueEscalation(1, 2)); // 1 + 1 + (2/2) = 3
        assertEquals(5, CardRejectLogic.computeFatigueEscalation(3, 3)); // 3 + 1 + (3/2) = 5
        assertEquals(8, CardRejectLogic.computeFatigueEscalation(5, 4)); // 5 + 1 + (4/2) = 8
    }
}
