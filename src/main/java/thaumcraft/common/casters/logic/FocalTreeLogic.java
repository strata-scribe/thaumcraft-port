package thaumcraft.common.casters.logic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Pure Java logic class for validating spell focus node trees.
 * Decoupled from Minecraft and Forge dependencies for easy testing.
 */
public class FocalTreeLogic {

    public enum NodeType {
        ROOT,
        MEDIUM,
        MOD,
        EFFECT,
        PACKAGE
    }

    public interface INode {
        NodeType getType();
        int getComplexity();
        List<INode> getChildren();
    }

    public static class ValidationResult {
        public final boolean isValid;
        public final String errorMessage;
        public final int totalComplexity;

        public ValidationResult(boolean isValid, String errorMessage, int totalComplexity) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
            this.totalComplexity = totalComplexity;
        }

        public static ValidationResult success(int totalComplexity) {
            return new ValidationResult(true, null, totalComplexity);
        }

        public static ValidationResult fail(String errorMessage) {
            return new ValidationResult(false, errorMessage, 0);
        }
    }

    /**
     * Validates a spell focus tree based on the provided root node.
     * Checks for a single root medium, valid effect leaves, absence of cyclic loops,
     * and validates total complexity against the allowable limit.
     *
     * @param root The root node of the tree.
     * @param maxComplexity The maximum allowable complexity.
     * @return ValidationResult indicating success or the specific failure reason.
     */
    public static ValidationResult validateTree(INode root, int maxComplexity) {
        if (root == null) {
            return ValidationResult.fail("Tree is empty.");
        }

        if (root.getType() != NodeType.ROOT && root.getType() != NodeType.MEDIUM) {
            return ValidationResult.fail("Tree must start with a ROOT or MEDIUM node.");
        }

        Set<INode> visited = new HashSet<>();
        int[] complexityBox = new int[]{0};

        String error = traverseAndValidate(root, visited, complexityBox, true);
        if (error != null) {
            return ValidationResult.fail(error);
        }

        if (complexityBox[0] > maxComplexity) {
            return ValidationResult.fail(String.format("Total complexity %d exceeds maximum allowed %d.", complexityBox[0], maxComplexity));
        }

        return ValidationResult.success(complexityBox[0]);
    }

    private static String traverseAndValidate(INode node, Set<INode> visited, int[] complexityBox, boolean isRootLevel) {
        if (node == null) {
            return "Tree contains a null node.";
        }

        if (visited.contains(node)) {
            return "Cyclic loop detected in tree.";
        }

        visited.add(node);
        complexityBox[0] += node.getComplexity();

        List<INode> children = node.getChildren();

        // If a node has no children, it must be an EFFECT
        if (children == null || children.isEmpty()) {
            if (node.getType() != NodeType.EFFECT) {
                return "All leaf nodes must be of type EFFECT.";
            }
        } else {
            // Recursively validate all children
            for (INode child : children) {
                String childError = traverseAndValidate(child, visited, complexityBox, false);
                if (childError != null) {
                    return childError;
                }
            }
        }

        // Remove node from visited set after DFS traversal to detect back-edges (cycles)
        // rather than cross-edges (DAGs).
        visited.remove(node);
        return null;
    }
}
