package thaumcraft.common.golems;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SealButcherLogic Pure Engine Tests")
public class SealButcherLogicTest {

    @Test
    @DisplayName("Empty list returns empty map")
    void testEmptyList() {
        Map<String, SealButcherLogic.TaskType> result = SealButcherLogic.evaluatePopulation(Collections.emptyList(), 5);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Adults exceeding threshold triggers CULL")
    void testCullTask() {
        List<SealButcherLogic.AnimalData> animals = Arrays.asList(
                new SealButcherLogic.AnimalData("cow", true),
                new SealButcherLogic.AnimalData("cow", true),
                new SealButcherLogic.AnimalData("cow", true)
        );

        Map<String, SealButcherLogic.TaskType> result = SealButcherLogic.evaluatePopulation(animals, 2);
        assertEquals(1, result.size());
        assertEquals(SealButcherLogic.TaskType.CULL, result.get("cow"));
    }

    @Test
    @DisplayName("Adults equal to threshold and >= 2 triggers FEED")
    void testFeedTaskExactThreshold() {
        List<SealButcherLogic.AnimalData> animals = Arrays.asList(
                new SealButcherLogic.AnimalData("pig", true),
                new SealButcherLogic.AnimalData("pig", true)
        );

        Map<String, SealButcherLogic.TaskType> result = SealButcherLogic.evaluatePopulation(animals, 2);
        assertEquals(1, result.size());
        assertEquals(SealButcherLogic.TaskType.FEED, result.get("pig"));
    }

    @Test
    @DisplayName("Adults below threshold but >= 2 triggers FEED")
    void testFeedTaskBelowThreshold() {
        List<SealButcherLogic.AnimalData> animals = Arrays.asList(
                new SealButcherLogic.AnimalData("sheep", true),
                new SealButcherLogic.AnimalData("sheep", true),
                new SealButcherLogic.AnimalData("sheep", false)
        );

        Map<String, SealButcherLogic.TaskType> result = SealButcherLogic.evaluatePopulation(animals, 5);
        assertEquals(1, result.size());
        assertEquals(SealButcherLogic.TaskType.FEED, result.get("sheep"));
    }

    @Test
    @DisplayName("Adults < 2 triggers NONE")
    void testNoneTaskNotEnoughAdults() {
        List<SealButcherLogic.AnimalData> animals = Arrays.asList(
                new SealButcherLogic.AnimalData("chicken", true),
                new SealButcherLogic.AnimalData("chicken", false),
                new SealButcherLogic.AnimalData("chicken", false)
        );

        Map<String, SealButcherLogic.TaskType> result = SealButcherLogic.evaluatePopulation(animals, 5);
        assertEquals(1, result.size());
        assertEquals(SealButcherLogic.TaskType.NONE, result.get("chicken"));
    }

    @Test
    @DisplayName("Multiple species evaluated independently")
    void testMultipleSpecies() {
        List<SealButcherLogic.AnimalData> animals = Arrays.asList(
                new SealButcherLogic.AnimalData("cow", true),
                new SealButcherLogic.AnimalData("cow", true),
                new SealButcherLogic.AnimalData("cow", true), // cow CULL (3 > 2)

                new SealButcherLogic.AnimalData("pig", true),
                new SealButcherLogic.AnimalData("pig", true), // pig FEED (2 <= 2, >= 2)

                new SealButcherLogic.AnimalData("sheep", true),
                new SealButcherLogic.AnimalData("sheep", false) // sheep NONE (1 < 2)
        );

        Map<String, SealButcherLogic.TaskType> result = SealButcherLogic.evaluatePopulation(animals, 2);
        assertEquals(3, result.size());
        assertEquals(SealButcherLogic.TaskType.CULL, result.get("cow"));
        assertEquals(SealButcherLogic.TaskType.FEED, result.get("pig"));
        assertEquals(SealButcherLogic.TaskType.NONE, result.get("sheep"));
    }
}
