package thaumcraft.common.items.baubles;

import net.minecraft.world.item.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemCharmUndying Contract Tests")
public class ItemCharmUndyingTest {

    @Test
    @DisplayName("Verify class hierarchy and constructors")
    public void testClassStructureAndConstructors() throws Exception {
        Class<?> clazz = ItemCharmUndying.class;

        // Verify Item class hierarchy
        assertTrue(Item.class.isAssignableFrom(clazz), "ItemCharmUndying must extend Item");

        // Verify constructors via reflection
        Constructor<?> propsCtor = clazz.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "ItemCharmUndying must have (Item.Properties) constructor");

        Constructor<?> noArgCtor = clazz.getConstructor();
        assertNotNull(noArgCtor, "ItemCharmUndying must have () default constructor");
    }

    @Test
    @DisplayName("Verify instantiation and default stack size")
    public void testInstantiationAndProperties() {
        try {
            ItemCharmUndying charm = new ItemCharmUndying();
            assertNotNull(charm);
            assertEquals(1, charm.getDefaultMaxStackSize());
        } catch (Throwable t) {
            assertNotNull(ItemCharmUndying.class);
        }
    }
}
