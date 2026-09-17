package thaumcraft.common.lib.research.theorycraft.logic;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CardAnalyzeLogicTest {

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
}
