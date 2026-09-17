package thaumcraft.common.lib.research.theorycraft.logic;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CardRethinkLogicTest {

    @Test
    void testCheckInitialization_EnoughPoints() {
        Map<String, Integer> totals = new HashMap<>();
        totals.put("ALCHEMY", 5);
        totals.put("ARTIFICE", 5);

        assertTrue(CardRethinkLogic.checkInitialization(totals));
    }

    @Test
    void testCheckInitialization_NotEnoughPoints() {
        Map<String, Integer> totals = new HashMap<>();
        totals.put("ALCHEMY", 5);
        totals.put("ARTIFICE", 4);

        assertFalse(CardRethinkLogic.checkInitialization(totals));
    }

    @Test
    void testCalculateRethink_RefundAndPoints() {
        Map<String, Integer> currentTotals = new LinkedHashMap<>();
        currentTotals.put("ALCHEMY", 7);
        currentTotals.put("ARTIFICE", 8);

        CardRethinkLogic.RethinkResult result = CardRethinkLogic.calculateRethink(
                currentTotals,
                0, // currentBonusDraws
                5, // randomBasicsValue
                20, // currentInspirationStart
                10 // currentInspiration (used 10, so refund should be 5)
        );

        assertNotNull(result);

        int remainingSum = 0;
        for (Map.Entry<String, Integer> entry : result.updatedTotals.entrySet()) {
            if (!entry.getKey().equals("BASICS")) {
                remainingSum += entry.getValue();
            }
        }
        assertEquals(5, remainingSum, "Should have removed exactly 10 points from original categories");

        assertEquals(1, result.bonusDraws);
        assertEquals(5, result.basicsAdded);
        assertEquals(5, result.updatedTotals.get("BASICS"));
        assertEquals(5, result.refundedInspiration, "Refunds 50% of used inspiration");
    }

    @Test
    void testCalculateRethink_DepleteCategory() {
        Map<String, Integer> currentTotals = new LinkedHashMap<>();
        currentTotals.put("ALCHEMY", 2);
        currentTotals.put("ARTIFICE", 8);

        CardRethinkLogic.RethinkResult result = CardRethinkLogic.calculateRethink(
                currentTotals,
                0,
                5,
                20,
                10
        );

        assertNotNull(result);

        assertFalse(result.updatedTotals.containsKey("ALCHEMY") && result.updatedTotals.get("ALCHEMY") > 0, "ALCHEMY should be fully depleted and removed");
        assertFalse(result.updatedTotals.containsKey("ARTIFICE") && result.updatedTotals.get("ARTIFICE") > 0, "ARTIFICE should also be depleted as a total of 10 points should be removed");
    }

    @Test
    void testCalculateRethink_RefundOddInspiration() {
        Map<String, Integer> currentTotals = new HashMap<>();
        currentTotals.put("ALCHEMY", 10);

        CardRethinkLogic.RethinkResult result = CardRethinkLogic.calculateRethink(
                currentTotals, 0, 3, 20, 11
        );
        assertEquals(4, result.refundedInspiration);
    }
}
