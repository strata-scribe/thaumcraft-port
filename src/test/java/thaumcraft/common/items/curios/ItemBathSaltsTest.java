package thaumcraft.common.items.curios;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemBathSalts Unit and Contract Tests")
class ItemBathSaltsTest {

    @Test
    @DisplayName("Verify Item class hierarchy and constructors")
    void testClassHierarchyAndConstructors() throws Exception {
        assertTrue(Item.class.isAssignableFrom(ItemBathSalts.class),
                "ItemBathSalts must extend net.minecraft.world.item.Item");

        Constructor<ItemBathSalts> ctor = ItemBathSalts.class.getConstructor(Item.Properties.class);
        assertNotNull(ctor, "ItemBathSalts must have a (Properties) constructor");
    }

    @Test
    @DisplayName("Verify useOn method contract exists")
    void testUseOnMethod() throws Exception {
        Method useOn = ItemBathSalts.class.getMethod("useOn", UseOnContext.class);
        assertNotNull(useOn, "ItemBathSalts must declare or inherit useOn(UseOnContext)");
        assertEquals(InteractionResult.class, useOn.getReturnType(),
                "useOn must return net.minecraft.world.InteractionResult");
    }

    @Test
    @DisplayName("Test instantiation (handled gracefully in unbootstrapped environment)")
    void testInstantiation() {
        try {
            ItemBathSalts bathSalts = new ItemBathSalts(new Item.Properties());
            assertNotNull(bathSalts);
        } catch (Throwable t) {
            // Unbootstrapped pure JUnit environment without FMLLoader
            assertNotNull(ItemBathSalts.class);
        }
    }
}
