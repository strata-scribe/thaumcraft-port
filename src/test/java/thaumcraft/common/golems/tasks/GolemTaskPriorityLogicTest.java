package thaumcraft.common.golems.tasks;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("GolemTaskPriorityLogic Pure Engine Tests")
public class GolemTaskPriorityLogicTest {

    @Test
    @DisplayName("Priority score calculation: distSq - (priority * 256.0) - (age * 10.0)")
    void testPriorityScoreCalculation() {
        // Priority 0, Age 0 -> purely squared distance
        assertEquals(100.0, GolemTaskPriorityLogic.calculatePriorityScore(100.0, 0, 0), 0.001);

        // Priority 1, Age 0 -> subtracts 256.0
        assertEquals(144.0, GolemTaskPriorityLogic.calculatePriorityScore(400.0, 1, 0), 0.001);

        // Priority 0, Age 5 -> subtracts 50.0
        assertEquals(50.0, GolemTaskPriorityLogic.calculatePriorityScore(100.0, 0, 5), 0.001);

        // Priority 2, Age 10 -> 400 - (2 * 256) - (10 * 10) = 400 - 512 - 100 = -212.0
        assertEquals(-212.0, GolemTaskPriorityLogic.calculatePriorityScore(400.0, 2, 10), 0.001);
    }

    @Test
    @DisplayName("Starvation Prevention: older tasks receive lower effective distance")
    void testStarvationPrevention() {
        double dist = 400.0;
        int priority = 1;

        double scoreNew = GolemTaskPriorityLogic.calculatePriorityScore(dist, priority, 0);
        double scoreOld = GolemTaskPriorityLogic.calculatePriorityScore(dist, priority, 50);

        // The old task should have a much lower score (higher absolute priority in sorting)
        assertTrue(scoreOld < scoreNew, "Older tasks should have a lower effective score");
        assertEquals(scoreNew - 500.0, scoreOld, 0.001); // 50 * 10 = 500
    }

    @Test
    @DisplayName("Task sorting correctly applies priority and age")
    void testTaskSorting() {
        // Task A: distSq = 169 (13 blocks away), priority 0, age 0 -> score = 169.0
        GolemTaskPriorityLogic.TaskSortingDTO taskA = new GolemTaskPriorityLogic.TaskSortingDTO(1, 169.0, 0, 0);

        // Task B: distSq = 400 (20 blocks away), priority 1, age 0 -> score = 400 - 256 = 144.0
        GolemTaskPriorityLogic.TaskSortingDTO taskB = new GolemTaskPriorityLogic.TaskSortingDTO(2, 400.0, 1, 0);

        // Task C: distSq = 400 (20 blocks away), priority 1, age 50 -> score = 400 - 256 - 500 = -356.0
        GolemTaskPriorityLogic.TaskSortingDTO taskC = new GolemTaskPriorityLogic.TaskSortingDTO(3, 400.0, 1, 50);

        // Task D: distSq = 25 (5 blocks away), priority 0, age 0 -> score = 25.0
        GolemTaskPriorityLogic.TaskSortingDTO taskD = new GolemTaskPriorityLogic.TaskSortingDTO(4, 25.0, 0, 0);

        List<GolemTaskPriorityLogic.TaskSortingDTO> candidates = List.of(taskA, taskB, taskC, taskD);
        List<GolemTaskPriorityLogic.TaskSortingDTO> sorted = GolemTaskPriorityLogic.sortTasks(candidates);

        // Expected order by lowest score:
        // 1. Task C (score -356.0)
        // 2. Task D (score 25.0)
        // 3. Task B (score 144.0)
        // 4. Task A (score 169.0)

        assertEquals(3, sorted.get(0).id());
        assertEquals(4, sorted.get(1).id());
        assertEquals(2, sorted.get(2).id());
        assertEquals(1, sorted.get(3).id());
    }
}
