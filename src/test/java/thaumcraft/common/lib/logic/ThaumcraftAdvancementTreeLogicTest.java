package thaumcraft.common.lib.logic;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ThaumcraftAdvancementTreeLogicTest {

    @Test
    public void testValidateTreeHierarchy_ValidTree() {
        ThaumcraftAdvancementTreeLogic.AdvancementNode root = new ThaumcraftAdvancementTreeLogic.AdvancementNode("root", null, "icon.png", Collections.emptyList());
        ThaumcraftAdvancementTreeLogic.AdvancementNode child1 = new ThaumcraftAdvancementTreeLogic.AdvancementNode("child1", "root", "icon.png", Collections.emptyList());
        ThaumcraftAdvancementTreeLogic.AdvancementNode child2 = new ThaumcraftAdvancementTreeLogic.AdvancementNode("child2", "root", "icon.png", Collections.emptyList());
        ThaumcraftAdvancementTreeLogic.AdvancementNode grandchild = new ThaumcraftAdvancementTreeLogic.AdvancementNode("grandchild", "child1", "icon.png", Collections.emptyList());

        List<ThaumcraftAdvancementTreeLogic.AdvancementNode> nodes = Arrays.asList(root, child1, child2, grandchild);
        assertTrue(ThaumcraftAdvancementTreeLogic.validateTreeHierarchy(nodes));
    }

    @Test
    public void testValidateTreeHierarchy_DuplicateId() {
        ThaumcraftAdvancementTreeLogic.AdvancementNode root = new ThaumcraftAdvancementTreeLogic.AdvancementNode("root", null, "icon.png", Collections.emptyList());
        ThaumcraftAdvancementTreeLogic.AdvancementNode duplicateRoot = new ThaumcraftAdvancementTreeLogic.AdvancementNode("root", null, "icon.png", Collections.emptyList());

        List<ThaumcraftAdvancementTreeLogic.AdvancementNode> nodes = Arrays.asList(root, duplicateRoot);
        assertFalse(ThaumcraftAdvancementTreeLogic.validateTreeHierarchy(nodes));
    }

    @Test
    public void testValidateTreeHierarchy_MissingParent() {
        ThaumcraftAdvancementTreeLogic.AdvancementNode child = new ThaumcraftAdvancementTreeLogic.AdvancementNode("child", "nonexistent_parent", "icon.png", Collections.emptyList());

        List<ThaumcraftAdvancementTreeLogic.AdvancementNode> nodes = Collections.singletonList(child);
        assertFalse(ThaumcraftAdvancementTreeLogic.validateTreeHierarchy(nodes));
    }

    @Test
    public void testValidateTreeHierarchy_CircularDependency() {
        ThaumcraftAdvancementTreeLogic.AdvancementNode node1 = new ThaumcraftAdvancementTreeLogic.AdvancementNode("node1", "node2", "icon.png", Collections.emptyList());
        ThaumcraftAdvancementTreeLogic.AdvancementNode node2 = new ThaumcraftAdvancementTreeLogic.AdvancementNode("node2", "node1", "icon.png", Collections.emptyList());

        List<ThaumcraftAdvancementTreeLogic.AdvancementNode> nodes = Arrays.asList(node1, node2);
        assertFalse(ThaumcraftAdvancementTreeLogic.validateTreeHierarchy(nodes));
    }

    @Test
    public void testValidateTreeHierarchy_SelfCircularDependency() {
        ThaumcraftAdvancementTreeLogic.AdvancementNode node = new ThaumcraftAdvancementTreeLogic.AdvancementNode("node1", "node1", "icon.png", Collections.emptyList());

        List<ThaumcraftAdvancementTreeLogic.AdvancementNode> nodes = Arrays.asList(node);
        assertFalse(ThaumcraftAdvancementTreeLogic.validateTreeHierarchy(nodes));
    }

    @Test
    public void testValidateTriggerCriteria_Valid() {
        List<String> criteria = Arrays.asList("thaumcraft:discover_aspect", "minecraft:inventory_changed");
        assertTrue(ThaumcraftAdvancementTreeLogic.validateTriggerCriteria(criteria));
    }

    @Test
    public void testValidateTriggerCriteria_InvalidFormat() {
        List<String> criteria = Arrays.asList("thaumcraft_discover_aspect"); // Missing colon
        assertFalse(ThaumcraftAdvancementTreeLogic.validateTriggerCriteria(criteria));

        List<String> criteria2 = Arrays.asList(":discover_aspect"); // Missing namespace
        assertFalse(ThaumcraftAdvancementTreeLogic.validateTriggerCriteria(criteria2));

        List<String> criteria3 = Arrays.asList("thaumcraft:"); // Missing type
        assertFalse(ThaumcraftAdvancementTreeLogic.validateTriggerCriteria(criteria3));

        List<String> criteria4 = Arrays.asList("thaumcraft:discover:aspect"); // Too many colons
        assertFalse(ThaumcraftAdvancementTreeLogic.validateTriggerCriteria(criteria4));
    }

    @Test
    public void testValidateTriggerCriteria_EmptyOrNull() {
        assertFalse(ThaumcraftAdvancementTreeLogic.validateTriggerCriteria(null));
        assertFalse(ThaumcraftAdvancementTreeLogic.validateTriggerCriteria(Arrays.asList("")));
        assertFalse(ThaumcraftAdvancementTreeLogic.validateTriggerCriteria(Arrays.asList((String) null)));
    }

    @Test
    public void testValidateTriggerCriteria_EmptyList() {
        assertTrue(ThaumcraftAdvancementTreeLogic.validateTriggerCriteria(Collections.emptyList()));
    }

    @Test
    public void testValidateIconDefinition_Valid() {
        assertTrue(ThaumcraftAdvancementTreeLogic.validateIconDefinition("thaumcraft:textures/gui/advancement/icon.png"));
        assertTrue(ThaumcraftAdvancementTreeLogic.validateIconDefinition("minecraft:textures/item/diamond.jpg"));
        assertTrue(ThaumcraftAdvancementTreeLogic.validateIconDefinition("icon.PNG"));
    }

    @Test
    public void testValidateIconDefinition_InvalidExtension() {
        assertFalse(ThaumcraftAdvancementTreeLogic.validateIconDefinition("thaumcraft:textures/gui/advancement/icon.gif"));
        assertFalse(ThaumcraftAdvancementTreeLogic.validateIconDefinition("thaumcraft:textures/gui/advancement/icon.txt"));
        assertFalse(ThaumcraftAdvancementTreeLogic.validateIconDefinition("icon"));
    }

    @Test
    public void testValidateIconDefinition_EmptyOrNull() {
        assertFalse(ThaumcraftAdvancementTreeLogic.validateIconDefinition(null));
        assertFalse(ThaumcraftAdvancementTreeLogic.validateIconDefinition(""));
        assertFalse(ThaumcraftAdvancementTreeLogic.validateIconDefinition("   "));
    }
}
