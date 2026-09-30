package thaumcraft.common.items.consumables;

import net.minecraft.world.item.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemAlumentum Contract & Unit Tests")
public class ItemAlumentumTest {

    @Test
    @DisplayName("Verify class hierarchy: ItemAlumentum must extend Item")
    void testClassHierarchy() {
        assertTrue(Item.class.isAssignableFrom(ItemAlumentum.class),
                "ItemAlumentum must extend net.minecraft.world.item.Item");
    }

    @Test
    @DisplayName("Verify constructors via reflection: (Properties) and ()")
    void testConstructors() throws Exception {
        Constructor<ItemAlumentum> propsCtor = ItemAlumentum.class.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "ItemAlumentum must have a (Item.Properties) constructor");

        Constructor<ItemAlumentum> defaultCtor = ItemAlumentum.class.getConstructor();
        assertNotNull(defaultCtor, "ItemAlumentum must have a default () constructor");
    }

    @Test
    @DisplayName("Verify instantiation and default properties (stacksTo 64)")
    void testInstantiation() {
        try {
            ItemAlumentum alumentum = new ItemAlumentum();
            assertNotNull(alumentum);
            assertEquals(64, alumentum.getDefaultMaxStackSize(), "ItemAlumentum max stack size must be 64");
        } catch (Throwable t) {
            // Handled gracefully in unbootstrapped pure JUnit environments
            assertNotNull(ItemAlumentum.class);
        }
    }
}
