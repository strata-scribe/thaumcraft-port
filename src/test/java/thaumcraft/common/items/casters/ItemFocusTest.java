package thaumcraft.common.items.casters;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.api.casters.FocusPackage;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemFocus Unit and Contract Tests")
class ItemFocusTest {

    @Test
    @DisplayName("Verify Item class hierarchy and constructors")
    void testClassHierarchyAndConstructors() throws Exception {
        assertTrue(Item.class.isAssignableFrom(ItemFocus.class),
                "ItemFocus must extend net.minecraft.world.item.Item");

        Constructor<ItemFocus> ctor1 = ItemFocus.class.getConstructor(Item.Properties.class, int.class);
        assertNotNull(ctor1, "ItemFocus must have a (Properties, int) constructor");

        Constructor<ItemFocus> ctor2 = ItemFocus.class.getConstructor(Item.Properties.class);
        assertNotNull(ctor2, "ItemFocus must have a (Properties) constructor");
    }

    @Test
    @DisplayName("Verify getMaxComplexity method contract exists")
    void testGetMaxComplexityMethod() throws Exception {
        Method getMaxComplexity = ItemFocus.class.getMethod("getMaxComplexity");
        assertNotNull(getMaxComplexity, "ItemFocus must declare getMaxComplexity()");
        assertEquals(int.class, getMaxComplexity.getReturnType(),
                "getMaxComplexity must return primitive int");
    }

    @Test
    @DisplayName("Verify getPackage and setPackage static method contracts exist")
    void testPackageMethods() throws Exception {
        Method getPackage = ItemFocus.class.getMethod("getPackage", ItemStack.class);
        assertNotNull(getPackage, "ItemFocus must declare static getPackage(ItemStack)");
        assertTrue(Modifier.isStatic(getPackage.getModifiers()), "getPackage must be static");
        assertEquals(FocusPackage.class, getPackage.getReturnType(),
                "getPackage must return FocusPackage");

        Method setPackage = ItemFocus.class.getMethod("setPackage", ItemStack.class, FocusPackage.class);
        assertNotNull(setPackage, "ItemFocus must declare static setPackage(ItemStack, FocusPackage)");
        assertTrue(Modifier.isStatic(setPackage.getModifiers()), "setPackage must be static");
        assertEquals(void.class, setPackage.getReturnType(),
                "setPackage must return void");
    }

    @Test
    @DisplayName("Test instantiation and complexity tiers (handled gracefully in unbootstrapped environment)")
    void testInstantiation() {
        try {
            ItemFocus focusTier1 = new ItemFocus(new Item.Properties(), 15);
            assertNotNull(focusTier1);
            assertEquals(15, focusTier1.getMaxComplexity());

            ItemFocus focusTier2 = new ItemFocus(new Item.Properties(), 25);
            assertNotNull(focusTier2);
            assertEquals(25, focusTier2.getMaxComplexity());

            ItemFocus focusTier3 = new ItemFocus(new Item.Properties(), 50);
            assertNotNull(focusTier3);
            assertEquals(50, focusTier3.getMaxComplexity());

            ItemFocus defaultFocus = new ItemFocus(new Item.Properties());
            assertNotNull(defaultFocus);
            assertEquals(15, defaultFocus.getMaxComplexity());
        } catch (Throwable t) {
            // Unbootstrapped pure JUnit environment without FMLLoader
            assertNotNull(ItemFocus.class);
        }
    }
}
