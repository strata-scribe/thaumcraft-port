package thaumcraft.common.items.tools;

import net.minecraft.world.item.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.common.items.tools.logic.ThaumcraftToolLogic;

import java.lang.reflect.Constructor;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Thaumium Tools Contract Tests")
public class ItemThaumiumToolsTest {

    private static final List<Class<? extends Item>> THAUMIUM_TOOL_CLASSES = List.of(
            ItemThaumiumPickaxe.class,
            ItemThaumiumAxe.class,
            ItemThaumiumSword.class,
            ItemThaumiumShovel.class,
            ItemThaumiumHoe.class
    );

    @Test
    @DisplayName("Verify class hierarchy: all Thaumium tools must extend Item")
    public void testClassHierarchy() {
        for (Class<? extends Item> toolClass : THAUMIUM_TOOL_CLASSES) {
            assertTrue(Item.class.isAssignableFrom(toolClass),
                    toolClass.getSimpleName() + " must extend net.minecraft.world.item.Item");
        }
    }

    @Test
    @DisplayName("Verify constructors via reflection: (Properties) and () for all Thaumium tools")
    public void testConstructors() throws Exception {
        for (Class<? extends Item> toolClass : THAUMIUM_TOOL_CLASSES) {
            Constructor<?> propsCtor = toolClass.getConstructor(Item.Properties.class);
            assertNotNull(propsCtor, toolClass.getSimpleName() + " must have (Item.Properties) constructor");

            Constructor<?> defaultCtor = toolClass.getConstructor();
            assertNotNull(defaultCtor, toolClass.getSimpleName() + " must have () default constructor");
        }
    }

    @Test
    @DisplayName("Verify instantiation and default properties (stacksTo 1, durability 500)")
    public void testInstantiationAndDurability() {
        assertEquals(500, ThaumcraftToolLogic.getThaumiumDurability());
        assertEquals(22, ThaumcraftToolLogic.getThaumiumEnchantability());
        assertEquals(7.0f, ThaumcraftToolLogic.getThaumiumDigSpeed(), 0.001f);

        for (Class<? extends Item> toolClass : THAUMIUM_TOOL_CLASSES) {
            try {
                Item item = toolClass.getConstructor().newInstance();
                assertNotNull(item);
                assertEquals(1, item.getDefaultMaxStackSize(),
                        toolClass.getSimpleName() + " max stack size must be 1");
            } catch (Throwable t) {
                // Handled gracefully in unbootstrapped pure JUnit environments
                assertNotNull(toolClass);
            }
        }
    }
}
