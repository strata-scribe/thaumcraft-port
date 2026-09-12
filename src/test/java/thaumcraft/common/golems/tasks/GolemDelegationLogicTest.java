package thaumcraft.common.golems.tasks;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GolemDelegationLogicTest {

    @Test
    void distributeTasks_equalGolemsAndTasks() {
        var golems = List.of(
            new GolemDelegationLogic.GolemDTO(1, 0, 0, 0),
            new GolemDelegationLogic.GolemDTO(2, 10, 0, 0)
        );
        var tasks = List.of(
            new GolemDelegationLogic.TaskDTO(101, 1, 0, 0, 0, 0), // Close to Golem 1
            new GolemDelegationLogic.TaskDTO(102, 9, 0, 0, 0, 0)  // Close to Golem 2
        );

        List<GolemDelegationLogic.Delegation> assignments = GolemDelegationLogic.distributeTasks(golems, tasks);

        assertEquals(2, assignments.size());

        // Golem 1 -> Task 101
        assertTrue(assignments.contains(new GolemDelegationLogic.Delegation(1, 101)));
        // Golem 2 -> Task 102
        assertTrue(assignments.contains(new GolemDelegationLogic.Delegation(2, 102)));
    }

    @Test
    void distributeTasks_moreGolemsThanTasks() {
        var golems = List.of(
            new GolemDelegationLogic.GolemDTO(1, 0, 0, 0),
            new GolemDelegationLogic.GolemDTO(2, 10, 0, 0),
            new GolemDelegationLogic.GolemDTO(3, 20, 0, 0)
        );
        var tasks = List.of(
            new GolemDelegationLogic.TaskDTO(101, 1, 0, 0, 0, 0), // Close to Golem 1
            new GolemDelegationLogic.TaskDTO(102, 9, 0, 0, 0, 0)  // Close to Golem 2
        );

        List<GolemDelegationLogic.Delegation> assignments = GolemDelegationLogic.distributeTasks(golems, tasks);

        assertEquals(2, assignments.size());
        assertTrue(assignments.contains(new GolemDelegationLogic.Delegation(1, 101)));
        assertTrue(assignments.contains(new GolemDelegationLogic.Delegation(2, 102)));
        // Golem 3 is idle
    }

    @Test
    void distributeTasks_moreTasksThanGolems() {
        var golems = List.of(
            new GolemDelegationLogic.GolemDTO(1, 0, 0, 0)
        );
        var tasks = List.of(
            new GolemDelegationLogic.TaskDTO(101, 10, 0, 0, 0, 0),
            new GolemDelegationLogic.TaskDTO(102, 1, 0, 0, 0, 0) // Closer to Golem 1
        );

        List<GolemDelegationLogic.Delegation> assignments = GolemDelegationLogic.distributeTasks(golems, tasks);

        assertEquals(1, assignments.size());
        assertTrue(assignments.contains(new GolemDelegationLogic.Delegation(1, 102)));
    }

    @Test
    void distributeTasks_priorityAndAgeImpact() {
        var golems = List.of(
            new GolemDelegationLogic.GolemDTO(1, 0, 0, 0)
        );
        var tasks = List.of(
            // Close, but low priority and age
            new GolemDelegationLogic.TaskDTO(101, 1, 0, 0, 0, 0),
            // Farther, but high priority
            new GolemDelegationLogic.TaskDTO(102, 5, 0, 0, 5, 0),
            // Farthest, but very old (high age)
            new GolemDelegationLogic.TaskDTO(103, 10, 0, 0, 0, 100)
        );

        // Distances squared: 101: 1, 102: 25, 103: 100
        // Priorities: 101: 0, 102: 5, 103: 0
        // Ages: 101: 0, 102: 0, 103: 100
        // PRIORITY_WEIGHT = 256.0, AGE_WEIGHT = 10.0
        // Scores:
        // 101: 1 - 0 - 0 = 1
        // 102: 25 - (5 * 256) - 0 = -1255
        // 103: 100 - 0 - (100 * 10) = -900
        // Best score is 102 (-1255)

        List<GolemDelegationLogic.Delegation> assignments = GolemDelegationLogic.distributeTasks(golems, tasks);

        assertEquals(1, assignments.size());
        assertTrue(assignments.contains(new GolemDelegationLogic.Delegation(1, 102)));
    }

    @Test
    void distributeTasks_emptyInputs() {
        assertTrue(GolemDelegationLogic.distributeTasks(Collections.emptyList(), List.of(new GolemDelegationLogic.TaskDTO(1,0,0,0,0,0))).isEmpty());
        assertTrue(GolemDelegationLogic.distributeTasks(List.of(new GolemDelegationLogic.GolemDTO(1,0,0,0)), Collections.emptyList()).isEmpty());
        assertTrue(GolemDelegationLogic.distributeTasks(null, null).isEmpty());
    }
}
