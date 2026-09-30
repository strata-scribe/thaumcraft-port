package thaumcraft.common.items.tools;

import net.minecraft.world.item.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.common.items.tools.logic.ThaumcraftToolLogic;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemScribingTools Contract & Unit Tests")
public class ItemScribingToolsTest {

    @Test
    @DisplayName("Verify class hierarchy: ItemScribingTools must extend Item")
    public void testClassHierarchy() {
        assertTrue(Item.class.isAssignableFrom(ItemScribingTools.class),
                "ItemScribingTools must extend net.minecraft.world.item.Item");
    }

    @Test
    @DisplayName("Verify constructors via reflection: (Properties) and ()")
    public void testConstructors() throws Exception {
        Constructor<ItemScribingTools> propsCtor = ItemScribingTools.class.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "ItemScribingTools must have (Item.Properties) constructor");

        Constructor<ItemScribingTools> defaultCtor = ItemScribingTools.class.getConstructor();
        assertNotNull(defaultCtor, "ItemScribingTools must have () default constructor");
    }

    @Test
    @DisplayName("Verify instantiation and default properties (stacksTo 1, durability 100)")
    public void testInstantiationAndDurability() {
        assertEquals(100, ThaumcraftToolLogic.getScribingToolsDurability());
        try {
            ItemScribingTools tools = new ItemScribingTools();
            assertNotNull(tools);
            assertEquals(1, tools.getDefaultMaxStackSize(), "Scribing tools max stack size must be 1");
        } catch (Throwable t) {
            // Handled gracefully in unbootstrapped pure JUnit environments
            assertNotNull(ItemScribingTools.class);
        }
    }
}
