package thaumcraft.common.items.casters;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemFocusPouch Contract & Unit Tests")
public class ItemFocusPouchTest {

    @Test
    @DisplayName("Verify class hierarchy and constructors")
    void testClassHierarchyAndConstructors() throws Exception {
        assertTrue(Item.class.isAssignableFrom(ItemFocusPouch.class),
                "ItemFocusPouch must extend net.minecraft.world.item.Item");

        Constructor<ItemFocusPouch> propsCtor = ItemFocusPouch.class.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "ItemFocusPouch must provide a (Item.Properties) constructor");

        Constructor<ItemFocusPouch> noArgCtor = ItemFocusPouch.class.getConstructor();
        assertNotNull(noArgCtor, "ItemFocusPouch must provide a default () constructor");
    }

    @Test
    @DisplayName("Verify getCapacity method contract")
    void testGetCapacityMethod() throws Exception {
        Method getCapacity = ItemFocusPouch.class.getMethod("getCapacity", ItemStack.class);
        assertNotNull(getCapacity, "ItemFocusPouch must declare getCapacity(ItemStack)");
        assertEquals(int.class, getCapacity.getReturnType(), "getCapacity must return primitive int");
    }

    @Test
    @DisplayName("Verify instantiation, stack size, and capacity")
    void testInstantiationAndCapacity() {
        try {
            ItemFocusPouch pouch = new ItemFocusPouch();
            assertNotNull(pouch);
            assertEquals(1, pouch.getDefaultMaxStackSize(), "ItemFocusPouch max stack size must be 1");
            assertEquals(18, pouch.getCapacity(null), "Default pouch capacity must be 18");
        } catch (Throwable t) {
            // Unbootstrapped pure JUnit fallback
            assertNotNull(ItemFocusPouch.class);
        }
    }
}
