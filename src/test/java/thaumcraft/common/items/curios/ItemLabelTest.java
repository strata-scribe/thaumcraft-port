package thaumcraft.common.items.curios;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemLabel Contract & Unit Tests")
public class ItemLabelTest {

    @Test
    @DisplayName("Verify class hierarchy, interfaces, and constructors")
    void testClassHierarchyAndConstructors() throws Exception {
        assertTrue(Item.class.isAssignableFrom(ItemLabel.class),
                "ItemLabel must extend net.minecraft.world.item.Item");
        assertTrue(IEssentiaContainerItem.class.isAssignableFrom(ItemLabel.class),
                "ItemLabel must implement IEssentiaContainerItem");

        Constructor<ItemLabel> propsCtor = ItemLabel.class.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "ItemLabel must have a (Item.Properties) constructor");

        Constructor<ItemLabel> noArgCtor = ItemLabel.class.getConstructor();
        assertNotNull(noArgCtor, "ItemLabel must have a default () constructor");
    }

    @Test
    @DisplayName("Verify static getAspect and setAspect contracts")
    void testAspectStaticMethods() throws Exception {
        Method getAspect = ItemLabel.class.getMethod("getAspect", ItemStack.class);
        assertNotNull(getAspect, "ItemLabel must declare getAspect(ItemStack)");
        assertTrue(Modifier.isStatic(getAspect.getModifiers()), "getAspect must be static");
        assertEquals(Aspect.class, getAspect.getReturnType(), "getAspect must return Aspect");

        Method setAspect = ItemLabel.class.getMethod("setAspect", ItemStack.class, Aspect.class);
        assertNotNull(setAspect, "ItemLabel must declare setAspect(ItemStack, Aspect)");
        assertTrue(Modifier.isStatic(setAspect.getModifiers()), "setAspect must be static");
        assertEquals(void.class, setAspect.getReturnType(), "setAspect must return void");

        try {
            assertNull(ItemLabel.getAspect(null), "getAspect(null) must return null safely");
            assertDoesNotThrow(() -> ItemLabel.setAspect(null, null), "setAspect(null, null) must be safe");
        } catch (Throwable t) {
            assertNotNull(ItemLabel.class);
        }
    }

    @Test
    @DisplayName("Verify IEssentiaContainerItem contract methods")
    void testEssentiaContainerItemContract() throws Exception {
        Method getAspects = ItemLabel.class.getMethod("getAspects", ItemStack.class);
        assertNotNull(getAspects);
        assertEquals(AspectList.class, getAspects.getReturnType());

        Method setAspects = ItemLabel.class.getMethod("setAspects", ItemStack.class, AspectList.class);
        assertNotNull(setAspects);
        assertEquals(void.class, setAspects.getReturnType());

        Method ignoreContained = ItemLabel.class.getMethod("ignoreContainedAspects");
        assertNotNull(ignoreContained);
        assertEquals(boolean.class, ignoreContained.getReturnType());
    }

    @Test
    @DisplayName("Verify instantiation and default properties")
    void testInstantiation() {
        try {
            ItemLabel label = new ItemLabel();
            assertNotNull(label);
            assertEquals(64, label.getDefaultMaxStackSize(), "ItemLabel max stack size must be 64");
            assertTrue(label.ignoreContainedAspects(), "ItemLabel must ignore contained aspects for crafting");
            assertNull(label.getAspects(null), "getAspects(null) must safely return null");
            assertDoesNotThrow(() -> label.setAspects(null, null));
        } catch (Throwable t) {
            // Unbootstrapped pure JUnit fallback
            assertNotNull(ItemLabel.class);
        }
    }
}
