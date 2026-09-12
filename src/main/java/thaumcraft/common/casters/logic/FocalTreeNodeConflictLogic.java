package thaumcraft.common.casters.logic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Pure Java logic class for detecting conflicting focus modifiers and mutually exclusive trajectory packages.
 * Decoupled from Minecraft and Forge dependencies for easy testing.
 */
public class FocalTreeNodeConflictLogic {

    public interface INodeConflictProvider {
        /**
         * Returns a set of string identifiers for mutually exclusive categories this node belongs to.
         */
        Set<String> getMutuallyExclusiveCategories();
    }

    /**
     * Checks if a list of nodes contains any mutually exclusive conflicts.
     * @param nodes The list of nodes to check (typically children of a single split/trajectory).
     * @return True if a conflict is detected, false otherwise.
     */
    public static boolean hasConflictingNodes(List<INodeConflictProvider> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return false;
        }

        Set<String> seenCategories = new HashSet<>();

        for (INodeConflictProvider node : nodes) {
            Set<String> categories = node.getMutuallyExclusiveCategories();
            if (categories != null) {
                for (String category : categories) {
                    if (seenCategories.contains(category)) {
                        return true; // Conflict detected
                    }
                    seenCategories.add(category);
                }
            }
        }

        return false;
    }
}
