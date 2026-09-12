package thaumcraft.common.casters.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FocalTreeNodeConflictLogicTest {

    private static class TestNode implements FocalTreeNodeConflictLogic.INodeConflictProvider {
        private final Set<String> categories;

        public TestNode(String... categories) {
            if (categories == null || categories.length == 0) {
                this.categories = Collections.emptySet();
            } else {
                this.categories = new HashSet<>(Arrays.asList(categories));
            }
        }

        @Override
        public Set<String> getMutuallyExclusiveCategories() {
            return categories;
        }
    }

    @Test
    @DisplayName("No conflict when nodes have no categories")
    void testNoConflictEmptyCategories() {
        List<FocalTreeNodeConflictLogic.INodeConflictProvider> nodes = Arrays.asList(
                new TestNode(),
                new TestNode()
        );
        assertFalse(FocalTreeNodeConflictLogic.hasConflictingNodes(nodes));
    }

    @Test
    @DisplayName("No conflict when nodes have different categories")
    void testNoConflictDifferentCategories() {
        List<FocalTreeNodeConflictLogic.INodeConflictProvider> nodes = Arrays.asList(
                new TestNode("trajectory_pierce"),
                new TestNode("trajectory_split")
        );
        assertFalse(FocalTreeNodeConflictLogic.hasConflictingNodes(nodes));
    }

    @Test
    @DisplayName("Conflict detected when nodes share a mutually exclusive category")
    void testConflictSharedCategory() {
        List<FocalTreeNodeConflictLogic.INodeConflictProvider> nodes = Arrays.asList(
                new TestNode("trajectory_pierce", "damage_modifier"),
                new TestNode("trajectory_pierce")
        );
        assertTrue(FocalTreeNodeConflictLogic.hasConflictingNodes(nodes));
    }

    @Test
    @DisplayName("No conflict with null or empty list")
    void testNoConflictNullOrEmpty() {
        assertFalse(FocalTreeNodeConflictLogic.hasConflictingNodes(null));
        assertFalse(FocalTreeNodeConflictLogic.hasConflictingNodes(Collections.emptyList()));
    }
}
