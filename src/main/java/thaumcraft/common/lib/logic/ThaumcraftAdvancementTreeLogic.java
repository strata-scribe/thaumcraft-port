package thaumcraft.common.lib.logic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ThaumcraftAdvancementTreeLogic {

    public static class AdvancementNode {
        public final String id;
        public final String parentId;
        public final String icon;
        public final List<String> triggerCriteria;

        public AdvancementNode(String id, String parentId, String icon, List<String> triggerCriteria) {
            this.id = id;
            this.parentId = parentId;
            this.icon = icon;
            this.triggerCriteria = triggerCriteria;
        }
    }

    public static boolean validateTreeHierarchy(List<AdvancementNode> nodes) {
        if (nodes == null) return false;

        Set<String> ids = new HashSet<>();

        // Check for duplicates and build ID set
        for (AdvancementNode node : nodes) {
            if (node.id == null || ids.contains(node.id)) {
                return false;
            }
            ids.add(node.id);
        }

        // Check for missing parents and circular dependencies
        for (AdvancementNode node : nodes) {
            if (node.parentId != null) {
                if (!ids.contains(node.parentId)) {
                    return false; // Missing parent
                }

                // Detect circular dependencies (simple tracing up the tree)
                Set<String> visited = new HashSet<>();
                String currentId = node.id;

                while (currentId != null) {
                    if (!visited.add(currentId)) {
                        return false; // Circular dependency detected
                    }

                    // Find the parent node
                    String nextParentId = null;
                    for (AdvancementNode n : nodes) {
                        if (n.id.equals(currentId)) {
                            nextParentId = n.parentId;
                            break;
                        }
                    }
                    currentId = nextParentId;
                }
            }
        }

        return true;
    }

    public static boolean validateTriggerCriteria(List<String> criteria) {
        if (criteria == null) return false;

        for (String criterion : criteria) {
            if (criterion == null || criterion.trim().isEmpty()) {
                return false;
            }

            // Expected format namespace:type
            String[] parts = criterion.split(":");
            if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public static boolean validateIconDefinition(String icon) {
        if (icon == null || icon.trim().isEmpty()) return false;

        String lowerIcon = icon.toLowerCase();
        return lowerIcon.endsWith(".png") || lowerIcon.endsWith(".jpg");
    }
}
