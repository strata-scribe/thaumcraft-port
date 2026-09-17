package thaumcraft.common.lib.research.theorycraft.logic;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CardPonderLogicTest {

    @Test
    public void testCalculateProgressAllocation_Normal() {
        Set<String> categories = new HashSet<>();
        categories.add("ALCHEMY");
        categories.add("ARTIFICE");

        Set<String> blockedCategories = new HashSet<>();

        CardPonderLogic.AllocationResult result = CardPonderLogic.calculateProgressAllocation(categories, blockedCategories);

        assertFalse(result.earlyExit);
        assertTrue(result.success);
        assertTrue(result.allocations.get("ALCHEMY") == 12 || result.allocations.get("ALCHEMY") == 13);
        assertEquals(25, result.allocations.get("ALCHEMY") + result.allocations.get("ARTIFICE"));
        assertEquals(5, result.allocations.get("BASICS"));
    }

    @Test
    public void testCalculateProgressAllocation_OneBlocked() {
        Set<String> categories = new HashSet<>();
        categories.add("ALCHEMY");
        categories.add("ARTIFICE");

        Set<String> blockedCategories = new HashSet<>();
        blockedCategories.add("ALCHEMY");

        CardPonderLogic.AllocationResult result = CardPonderLogic.calculateProgressAllocation(categories, blockedCategories);

        assertFalse(result.earlyExit);
        assertTrue(result.success);
        assertNull(result.allocations.get("ALCHEMY"));
        assertEquals(25, result.allocations.get("ARTIFICE"));
        assertEquals(5, result.allocations.get("BASICS"));
    }

    @Test
    public void testCalculateProgressAllocation_EarlyExit() {
        Set<String> categories = new HashSet<>();
        categories.add("ALCHEMY");

        Set<String> blockedCategories = new HashSet<>();
        blockedCategories.add("ALCHEMY");

        CardPonderLogic.AllocationResult result = CardPonderLogic.calculateProgressAllocation(categories, blockedCategories);

        assertTrue(result.earlyExit);
        assertFalse(result.success);
        assertTrue(result.allocations.isEmpty());
    }

    @Test
    public void testCalculateCategoryWeighting_Normal() {
        Map<String, Integer> interactions = new HashMap<>();
        interactions.put("ALCHEMY", 10);
        interactions.put("ARTIFICE", 5);

        Map<String, Double> weights = CardPonderLogic.calculateCategoryWeighting(interactions);

        assertEquals(2, weights.size());
        assertEquals(10.0 / 15.0, weights.get("ALCHEMY"), 0.001);
        assertEquals(5.0 / 15.0, weights.get("ARTIFICE"), 0.001);
    }

    @Test
    public void testCalculateCategoryWeighting_EmptyAndNull() {
        Map<String, Double> weights = CardPonderLogic.calculateCategoryWeighting(new HashMap<>());
        assertTrue(weights.isEmpty());

        weights = CardPonderLogic.calculateCategoryWeighting(null);
        assertTrue(weights.isEmpty());
    }

    @Test
    public void testCalculateCategoryWeighting_ZeroTotal() {
        Map<String, Integer> interactions = new HashMap<>();
        interactions.put("ALCHEMY", 0);
        interactions.put("ARTIFICE", 0);

        Map<String, Double> weights = CardPonderLogic.calculateCategoryWeighting(interactions);

        assertEquals(2, weights.size());
        assertEquals(0.0, weights.get("ALCHEMY"), 0.001);
        assertEquals(0.0, weights.get("ARTIFICE"), 0.001);
    }

    @Test
    public void testVerifyInspirationCost() {
        assertTrue(CardPonderLogic.verifyInspirationCost(2));
        assertTrue(CardPonderLogic.verifyInspirationCost(3));
        assertFalse(CardPonderLogic.verifyInspirationCost(1));
        assertFalse(CardPonderLogic.verifyInspirationCost(0));
    }
}
