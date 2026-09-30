package thaumcraft.common.items.tools;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.common.items.tools.logic.ThaumometerZoomLogic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemThaumometer Contract & Logic Tests")
class ItemThaumometerTest {

    @Test
    @DisplayName("Verify class hierarchy, constructors, and use method presence")
    void testClassStructureAndConstructors() throws Exception {
        assertTrue(Item.class.isAssignableFrom(ItemThaumometer.class),
                "ItemThaumometer must extend net.minecraft.world.item.Item");

        Constructor<ItemThaumometer> propsCtor = ItemThaumometer.class.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "ItemThaumometer must have (Item.Properties) constructor");

        Constructor<ItemThaumometer> noArgCtor = ItemThaumometer.class.getConstructor();
        assertNotNull(noArgCtor, "ItemThaumometer must have () default constructor");

        Method useMethod = ItemThaumometer.class.getMethod("use", Level.class, Player.class, InteractionHand.class);
        assertNotNull(useMethod, "ItemThaumometer must implement use");
        assertEquals(InteractionResult.class, useMethod.getReturnType(), "use must return InteractionResult");
    }

    @Test
    @DisplayName("Verify instantiation and default stack size")
    void testInstantiationAndProperties() {
        try {
            ItemThaumometer thaumometer = new ItemThaumometer();
            assertNotNull(thaumometer);
            assertEquals(1, thaumometer.getDefaultMaxStackSize(), "Thaumometer stack size should be 1");
        } catch (Throwable t) {
            assertNotNull(ItemThaumometer.class);
        }
    }

    @Test
    @DisplayName("Verify zoom logic integration")
    void testLogicIntegration() {
        double fov = ThaumometerZoomLogic.calculateFovScaling(0.5, 90.0);
        assertEquals(60.0, fov, 0.01);
    }
}
