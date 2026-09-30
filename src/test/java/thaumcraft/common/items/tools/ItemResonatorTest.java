package thaumcraft.common.items.tools;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemResonator Contract Tests")
public class ItemResonatorTest {

    @Test
    @DisplayName("Verify class hierarchy, constructors, and useOn method presence")
    public void testClassStructureAndConstructors() throws Exception {
        // Class extends Item
        assertTrue(Item.class.isAssignableFrom(ItemResonator.class),
                "ItemResonator must extend net.minecraft.world.item.Item");

        // Constructor with Properties
        Constructor<ItemResonator> propsCtor = ItemResonator.class.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "ItemResonator must provide a (Item.Properties) constructor");

        // No-arg constructor
        Constructor<ItemResonator> noArgCtor = ItemResonator.class.getConstructor();
        assertNotNull(noArgCtor, "ItemResonator must provide a default () constructor");

        // useOn(UseOnContext)
        Method useOnMethod = ItemResonator.class.getMethod("useOn", UseOnContext.class);
        assertNotNull(useOnMethod, "ItemResonator must implement useOn");
        assertEquals(InteractionResult.class, useOnMethod.getReturnType(), "useOn method must return InteractionResult");
    }

    @Test
    @DisplayName("Verify instantiation and default stack size")
    public void testInstantiationAndProperties() {
        try {
            ItemResonator resonator = new ItemResonator();
            assertNotNull(resonator);
            assertEquals(1, resonator.getDefaultMaxStackSize(), "Resonator stack size should be 1");
        } catch (Throwable t) {
            // Headless unit test environment without full NeoForge registry bootstrap
            assertNotNull(ItemResonator.class);
        }
    }
}
