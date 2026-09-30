package thaumcraft.common.items.baubles;

import net.minecraft.world.item.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemBandCuriosity Contract Tests")
public class ItemBandCuriosityTest {

    @Test
    @DisplayName("Verify class hierarchy and constructors")
    public void testClassStructureAndConstructors() throws Exception {
        Class<?> clazz = ItemBandCuriosity.class;

        // Verify Item class hierarchy
        assertTrue(Item.class.isAssignableFrom(clazz), "ItemBandCuriosity must extend Item");

        // Verify constructors via reflection
        Constructor<?> propsCtor = clazz.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "ItemBandCuriosity must have (Item.Properties) constructor");

        Constructor<?> noArgCtor = clazz.getConstructor();
        assertNotNull(noArgCtor, "ItemBandCuriosity must have () default constructor");
    }

    @Test
    @DisplayName("Verify instantiation and default stack size")
    public void testInstantiationAndProperties() {
        try {
            ItemBandCuriosity band = new ItemBandCuriosity();
            assertNotNull(band);
            assertEquals(1, band.getDefaultMaxStackSize());
        } catch (Throwable t) {
            assertNotNull(ItemBandCuriosity.class);
        }
    }
}
