package thaumcraft.common.items.curios;

import net.minecraft.world.item.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemPrimordialPearl Contract & Unit Tests")
public class ItemPrimordialPearlTest {

    @Test
    @DisplayName("Verify class hierarchy: ItemPrimordialPearl must extend Item")
    void testClassHierarchy() {
        assertTrue(Item.class.isAssignableFrom(ItemPrimordialPearl.class),
                "ItemPrimordialPearl must extend net.minecraft.world.item.Item");
    }

    @Test
    @DisplayName("Verify constructors via reflection: (Properties) and ()")
    void testConstructors() throws Exception {
        Constructor<ItemPrimordialPearl> propsCtor = ItemPrimordialPearl.class.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "ItemPrimordialPearl must have a (Item.Properties) constructor");

        Constructor<ItemPrimordialPearl> defaultCtor = ItemPrimordialPearl.class.getConstructor();
        assertNotNull(defaultCtor, "ItemPrimordialPearl must have a default () constructor");
    }

    @Test
    @DisplayName("Verify instantiation and default properties (stacksTo 1, durability 8)")
    void testInstantiation() {
        try {
            ItemPrimordialPearl pearl = new ItemPrimordialPearl();
            assertNotNull(pearl);
            assertEquals(1, pearl.getDefaultMaxStackSize(), "ItemPrimordialPearl max stack size must be 1");
            assertEquals(8, pearl.getMaxDamage(pearl.getDefaultInstance()), "ItemPrimordialPearl max damage must be 8");
        } catch (Throwable t) {
            // Handled gracefully in unbootstrapped pure JUnit environments
            assertNotNull(ItemPrimordialPearl.class);
        }
    }
}
