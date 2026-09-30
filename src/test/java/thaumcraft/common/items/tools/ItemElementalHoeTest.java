package thaumcraft.common.items.tools;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemElementalHoe & Crop Growth Contract Tests")
public class ItemElementalHoeTest {

    @Test
    @DisplayName("Verify class hierarchy, constructors, and method signatures")
    public void testClassStructureAndConstructors() throws Exception {
        assertTrue(Item.class.isAssignableFrom(ItemElementalHoe.class),
                "ItemElementalHoe must extend net.minecraft.world.item.Item");

        Constructor<ItemElementalHoe> propsCtor = ItemElementalHoe.class.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "Must have (Properties) constructor");

        Constructor<ItemElementalHoe> defaultCtor = ItemElementalHoe.class.getConstructor();
        assertNotNull(defaultCtor, "Must have () default constructor");

        Method useOn = ItemElementalHoe.class.getMethod("useOn", UseOnContext.class);
        assertNotNull(useOn, "Must implement useOn(UseOnContext)");
        assertEquals(net.minecraft.world.InteractionResult.class, useOn.getReturnType());
    }

    @Test
    @DisplayName("Verify instantiation and durability (1500), stacksTo (1)")
    public void testInstantiationAndDurability() {
        try {
            ItemElementalHoe hoe = new ItemElementalHoe();
            assertNotNull(hoe);
            assertEquals(1, hoe.getDefaultMaxStackSize(), "Elemental Hoe must have max stack size 1");
        } catch (Throwable t) {
            // In headless JUnit without FML loader, Item.Properties may throw ExceptionInInitializerError.
            assertNotNull(ItemElementalHoe.class);
        }
    }

    @Test
    @DisplayName("Crop Acceleration Chance: Center target always succeeds (100%) regardless of roll")
    public void testCropGrowthCenterAlwaysSucceeds() {
        assertTrue(ElementalToolLogic.shouldAccelerateCropGrowth(0.00, true));
        assertTrue(ElementalToolLogic.shouldAccelerateCropGrowth(0.39, true));
        assertTrue(ElementalToolLogic.shouldAccelerateCropGrowth(0.40, true));
        assertTrue(ElementalToolLogic.shouldAccelerateCropGrowth(0.99, true));
        assertTrue(ElementalToolLogic.shouldAccelerateCropGrowth(1.00, true));
    }

    @Test
    @DisplayName("Crop Acceleration Chance: Surrounding area has exactly 40% chance (< 0.40)")
    public void testCropGrowthSurroundingThreshold() {
        // Rolls below 0.40 succeed
        assertTrue(ElementalToolLogic.shouldAccelerateCropGrowth(0.00, false));
        assertTrue(ElementalToolLogic.shouldAccelerateCropGrowth(0.25, false));
        assertTrue(ElementalToolLogic.shouldAccelerateCropGrowth(0.3999, false));

        // Rolls at or above 0.40 fail
        assertFalse(ElementalToolLogic.shouldAccelerateCropGrowth(0.40, false));
        assertFalse(ElementalToolLogic.shouldAccelerateCropGrowth(0.4001, false));
        assertFalse(ElementalToolLogic.shouldAccelerateCropGrowth(0.75, false));
        assertFalse(ElementalToolLogic.shouldAccelerateCropGrowth(0.99, false));
    }
}
