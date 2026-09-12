package thaumcraft.common.golems.tasks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Pure Java logic engine for distributing tasks to golems.
 *
 * Invariant: ZERO imports from net.minecraft.* or net.neoforged.* to allow
 * complete JUnit 5 test isolation.
 */
public final class GolemDelegationLogic {

    private GolemDelegationLogic() {}

    public record GolemDTO(int id, double x, double y, double z) {}
    public record TaskDTO(int id, double x, double y, double z, int priority, long age) {}
    public record Delegation(int golemId, int taskId) {}

    private record Pair(GolemDTO golem, TaskDTO task, double score) {}

    /**
     * Distributes pending tasks across multiple active golems without worker starvation.
     * Uses a greedy matching approach based on task priority scores.
     */
    public static List<Delegation> distributeTasks(List<GolemDTO> golems, List<TaskDTO> tasks) {
        List<Delegation> assignments = new ArrayList<>();
        if (golems == null || tasks == null || golems.isEmpty() || tasks.isEmpty()) {
            return assignments;
        }

        List<Pair> pairs = new ArrayList<>(golems.size() * tasks.size());
        for (GolemDTO golem : golems) {
            for (TaskDTO task : tasks) {
                double dx = golem.x() - task.x();
                double dy = golem.y() - task.y();
                double dz = golem.z() - task.z();
                double distSq = dx * dx + dy * dy + dz * dz;
                double score = GolemTaskPriorityLogic.calculatePriorityScore(distSq, task.priority(), task.age());
                pairs.add(new Pair(golem, task, score));
            }
        }

        // Sort by score ascending (lowest score is best)
        pairs.sort(Comparator.comparingDouble(Pair::score));

        Set<Integer> assignedGolems = new HashSet<>();
        Set<Integer> assignedTasks = new HashSet<>();

        for (Pair pair : pairs) {
            if (!assignedGolems.contains(pair.golem().id()) && !assignedTasks.contains(pair.task().id())) {
                assignments.add(new Delegation(pair.golem().id(), pair.task().id()));
                assignedGolems.add(pair.golem().id());
                assignedTasks.add(pair.task().id());

                // Stop early if all golems or all tasks are assigned
                if (assignedGolems.size() == golems.size() || assignedTasks.size() == tasks.size()) {
                    break;
                }
            }
        }

        return assignments;
    }
}
