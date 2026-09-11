package thaumcraft.common.casters.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FocalTreeLogicTest {

    private static class TestNode implements FocalTreeLogic.INode {
        private final FocalTreeLogic.NodeType type;
        private final int complexity;
        private final List<FocalTreeLogic.INode> children = new ArrayList<>();

        public TestNode(FocalTreeLogic.NodeType type, int complexity, FocalTreeLogic.INode... kids) {
            this.type = type;
            this.complexity = complexity;
            if (kids != null) {
                this.children.addAll(Arrays.asList(kids));
            }
        }

        @Override
        public FocalTreeLogic.NodeType getType() {
            return type;
        }

        @Override
        public int getComplexity() {
            return complexity;
        }

        @Override
        public List<FocalTreeLogic.INode> getChildren() {
            return children;
        }

        public void addChild(FocalTreeLogic.INode child) {
            this.children.add(child);
        }
    }

    @Test
    @DisplayName("Valid tree calculates total complexity successfully")
    void testValidTreeSuccess() {
        // Root(0) -> Medium(2) -> Mod(1) -> Effect(4)
        TestNode effect = new TestNode(FocalTreeLogic.NodeType.EFFECT, 4);
        TestNode mod = new TestNode(FocalTreeLogic.NodeType.MOD, 1, effect);
        TestNode medium = new TestNode(FocalTreeLogic.NodeType.MEDIUM, 2, mod);
        TestNode root = new TestNode(FocalTreeLogic.NodeType.ROOT, 0, medium);

        FocalTreeLogic.ValidationResult result = FocalTreeLogic.validateTree(root, 10);
        assertTrue(result.isValid);
        assertEquals(7, result.totalComplexity);
    }

    @Test
    @DisplayName("Fails when tree exceeds max complexity limit")
    void testMaxComplexityExceeded() {
        TestNode effect = new TestNode(FocalTreeLogic.NodeType.EFFECT, 10);
        TestNode root = new TestNode(FocalTreeLogic.NodeType.ROOT, 5, effect);

        FocalTreeLogic.ValidationResult result = FocalTreeLogic.validateTree(root, 10); // total 15
        assertFalse(result.isValid);
        assertTrue(result.errorMessage.contains("exceeds maximum allowed"));
    }

    @Test
    @DisplayName("Fails if non-effect node is a leaf")
    void testNonEffectLeaf() {
        TestNode mod = new TestNode(FocalTreeLogic.NodeType.MOD, 2); // No children!
        TestNode root = new TestNode(FocalTreeLogic.NodeType.ROOT, 0, mod);

        FocalTreeLogic.ValidationResult result = FocalTreeLogic.validateTree(root, 10);
        assertFalse(result.isValid);
        assertTrue(result.errorMessage.contains("leaf nodes must be of type EFFECT"));
    }

    @Test
    @DisplayName("Fails if tree starts with non-ROOT/MEDIUM")
    void testInvalidRootType() {
        TestNode effect = new TestNode(FocalTreeLogic.NodeType.EFFECT, 2);
        TestNode mod = new TestNode(FocalTreeLogic.NodeType.MOD, 1, effect); // MOD as root!

        FocalTreeLogic.ValidationResult result = FocalTreeLogic.validateTree(mod, 10);
        assertFalse(result.isValid);
        assertTrue(result.errorMessage.contains("must start with a ROOT or MEDIUM node"));
    }

    @Test
    @DisplayName("Fails if tree has cyclic loop")
    void testCyclicLoop() {
        TestNode mod = new TestNode(FocalTreeLogic.NodeType.MOD, 1);
        TestNode root = new TestNode(FocalTreeLogic.NodeType.ROOT, 0, mod);
        // Add cycle
        mod.addChild(root);

        FocalTreeLogic.ValidationResult result = FocalTreeLogic.validateTree(root, 10);
        assertFalse(result.isValid);
        assertTrue(result.errorMessage.contains("Cyclic loop detected"));
    }

    @Test
    @DisplayName("Empty tree fails")
    void testEmptyTree() {
        FocalTreeLogic.ValidationResult result = FocalTreeLogic.validateTree(null, 10);
        assertFalse(result.isValid);
        assertTrue(result.errorMessage.contains("Tree is empty"));
    }
}
