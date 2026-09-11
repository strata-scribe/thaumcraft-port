package thaumcraft.common.golems.tasks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Pure Java logic engine for Golemancy task prioritization.
 *
 * Invariant: ZERO imports from net.minecraft.* or net.neoforged.* to allow
 * complete JUnit 5 test isolation.
 */
public final class GolemTaskPriorityLogic {

    public static final double PRIORITY_WEIGHT = 256.0;
    public static final double AGE_WEIGHT = 10.0;

    private GolemTaskPriorityLogic() {}

    /**
     * Calculates the effective distance score of a task given its squared Euclidean
     * distance, task priority level, and age.
     *
     * Older tasks (higher age) get a reduced effective distance to prevent starvation.
     */
    public static double calculatePriorityScore(double distSq, int priority, long age) {
        return distSq - (priority * PRIORITY_WEIGHT) - (age * AGE_WEIGHT);
    }

    /**
     * DTO for sorting tasks in pure logic tests.
     */
    public record TaskSortingDTO(int id, double distSq, int priority, long age) {}

    /**
     * Sorts tasks in ascending order of effective distance (highest priority & closest first).
     */
    public static List<TaskSortingDTO> sortTasks(List<TaskSortingDTO> tasks) {
        if (tasks == null) return List.of();
        List<TaskSortingDTO> sorted = new ArrayList<>(tasks);
        sorted.sort(Comparator.comparingDouble(t -> calculatePriorityScore(t.distSq(), t.priority(), t.age())));
        return sorted;
    }
}
