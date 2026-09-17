package thaumcraft.common.lib.research.theorycraft.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Collection;

public class CardBalanceLogicTest {

    @Test
    public void testCanInitialize() {
        Map<String, Integer> totals = new HashMap<>();
        Collection<String> blocked = new HashSet<>();

        totals.put("A", 10);
        totals.put("B", 10);

        assertTrue(CardBalanceLogic.canInitialize(totals, blocked));

        blocked.add("A");
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
