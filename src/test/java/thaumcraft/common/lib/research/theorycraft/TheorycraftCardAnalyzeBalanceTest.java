package thaumcraft.common.lib.research.theorycraft;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

import thaumcraft.common.lib.research.theorycraft.logic.CardAnalyzeLogic;
import thaumcraft.common.lib.research.theorycraft.logic.CardBalanceLogic;

class TheorycraftCardAnalyzeBalanceTest {

    // --- CardAnalyzeLogic Tests ---

    @Test
    void testObservationKnowledgeRequirements() {
        assertEquals(1, CardAnalyzeLogic.getRequiredObservations());
        assertTrue(CardAnalyzeLogic.hasRequiredObservations(1));
        assertTrue(CardAnalyzeLogic.hasRequiredObservations(5));
        assertFalse(CardAnalyzeLogic.hasRequiredObservations(0));
    }

    @Test
    void testFilterCategories() {
        List<String> input = Arrays.asList("BASICS", "ALCHEMY", "ARTIFICE", "BASICS");
        List<String> output = CardAnalyzeLogic.filterCategories(input);

        assertEquals(2, output.size());
        assertFalse(output.contains("BASICS"));
        assertTrue(output.contains("ALCHEMY"));
        assertTrue(output.contains("ARTIFICE"));

        assertTrue(CardAnalyzeLogic.filterCategories(null).isEmpty());
        assertTrue(CardAnalyzeLogic.filterCategories(Arrays.asList("BASICS")).isEmpty());
    }

    @Test
    void testSelectRandomCategory() {
        List<String> input = Arrays.asList("ALCHEMY", "ARTIFICE", "GOLEMANCY");

        // With a fixed seed, random selection should be deterministic
        long seed = 12345L;
        String selected = CardAnalyzeLogic.selectRandomCategory(seed, input);
        assertNotNull(selected);
        assertTrue(input.contains(selected));

        // Null or empty list handling
        assertNull(CardAnalyzeLogic.selectRandomCategory(seed, null));
        assertNull(CardAnalyzeLogic.selectRandomCategory(seed, Arrays.asList()));
    }

    @Test
    void testCalculateProgressionRewards() {
        // Test with a few different seeds to ensure bounds
        long[] seeds = {0L, 42L, 12345L, 987654321L};

        for (long seed : seeds) {
            CardAnalyzeLogic.ProgressionReward reward = CardAnalyzeLogic.calculateProgressionRewards(seed);

            assertTrue(reward.mainCategoryPoints >= 25 && reward.mainCategoryPoints <= 50,
                "Points should be between 25 and 50 inclusive, got " + reward.mainCategoryPoints);
            assertEquals(5, reward.basicsBonus, "BASICS bonus should always be exactly 5");
        }
    }

    // --- CardBalanceLogic Tests ---

    @Test
    public void testCanInitialize() {
        Map<String, Integer> totals = new HashMap<>();
        Collection<String> blocked = new HashSet<>();

        totals.put("A", 10);
        totals.put("B", 10);

        assertTrue(CardBalanceLogic.canInitialize(totals, blocked));

        blocked.add("A");
        assertFalse(CardBalanceLogic.canInitialize(totals, blocked));

        totals.put("C", 10);
        assertTrue(CardBalanceLogic.canInitialize(totals, blocked));

        totals.put("C", 0);
        totals.put("B", 0);
        totals.put("A", 0); // Unblocked values sum to 0, which is < 2 (size of unblocked)

        assertFalse(CardBalanceLogic.canInitialize(totals, blocked));

    }

    @Test
    public void testCalculateBalancedTotals_PerfectlyDivisible() {
        Map<String, Integer> totals = new HashMap<>();
        Collection<String> blocked = new HashSet<>();

        totals.put("A", 20);
        totals.put("B", 10);
        totals.put("C", 0);

        assertTrue(CardBalanceLogic.calculateBalancedTotals(totals, blocked));

        assertEquals(10, totals.get("A"));
        assertEquals(10, totals.get("B"));
        assertEquals(10, totals.get("C"));
    }

    @Test
    public void testCalculateBalancedTotals_WithRemainder() {
        Map<String, Integer> totals = new HashMap<>();
        Collection<String> blocked = new HashSet<>();

        totals.put("A", 20);
        totals.put("B", 10);
        totals.put("C", 2);

        assertTrue(CardBalanceLogic.calculateBalancedTotals(totals, blocked));

        int sum = totals.values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(32, sum, "Total sum should be preserved");

        int max = totals.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        int min = totals.values().stream().mapToInt(Integer::intValue).min().orElse(0);

        assertTrue(max - min <= 1, "The difference between max and min should be at most 1");
    }

    @Test
    public void testCalculateBalancedTotals_BlockedCategory() {
        Map<String, Integer> totals = new HashMap<>();
        Collection<String> blocked = new HashSet<>();

        totals.put("A", 20);
        totals.put("B", 10);
        totals.put("C", 0);
        totals.put("BLOCKED", 50);

        blocked.add("BLOCKED");

        assertTrue(CardBalanceLogic.calculateBalancedTotals(totals, blocked));

        assertEquals(10, totals.get("A"));
        assertEquals(10, totals.get("B"));
        assertEquals(10, totals.get("C"));
        assertEquals(50, totals.get("BLOCKED"));
    }
}
