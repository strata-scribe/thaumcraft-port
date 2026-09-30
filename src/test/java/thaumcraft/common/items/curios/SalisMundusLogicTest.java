package thaumcraft.common.items.curios;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class SalisMundusLogicTest {

    @Test
    public void testCraftingTableTransformation() {
        assertEquals(SalisMundusLogic.TransformationTarget.TRANSFORM_WORKBENCH,
                SalisMundusLogic.getTarget("minecraft:crafting_table"));
        assertEquals(SalisMundusLogic.TransformationTarget.TRANSFORM_WORKBENCH,
                SalisMundusLogic.getTarget("crafting_table"));
    }

    @Test
    public void testCauldronTransformation() {
        assertEquals(SalisMundusLogic.TransformationTarget.TRANSFORM_CRUCIBLE,
                SalisMundusLogic.getTarget("minecraft:cauldron"));
        assertEquals(SalisMundusLogic.TransformationTarget.TRANSFORM_CRUCIBLE,
                SalisMundusLogic.getTarget("cauldron"));
    }

    @Test
    public void testBookshelfTransformation() {
        assertEquals(SalisMundusLogic.TransformationTarget.TRANSFORM_THAUMONOMICON,
                SalisMundusLogic.getTarget("minecraft:bookshelf"));
        assertEquals(SalisMundusLogic.TransformationTarget.TRANSFORM_THAUMONOMICON,
                SalisMundusLogic.getTarget("bookshelf"));
    }

    @Test
    public void testOtherBlocksReturnNone() {
        assertEquals(SalisMundusLogic.TransformationTarget.NONE, SalisMundusLogic.getTarget("minecraft:dirt"));
        assertEquals(SalisMundusLogic.TransformationTarget.NONE, SalisMundusLogic.getTarget("minecraft:stone"));
        assertEquals(SalisMundusLogic.TransformationTarget.NONE, SalisMundusLogic.getTarget("minecraft:oak_planks"));
        assertEquals(SalisMundusLogic.TransformationTarget.NONE, SalisMundusLogic.getTarget("minecraft:water_cauldron"));
        assertEquals(SalisMundusLogic.TransformationTarget.NONE, SalisMundusLogic.getTarget(""));
        assertEquals(SalisMundusLogic.TransformationTarget.NONE, SalisMundusLogic.getTarget(null));
    }

    @Test
    public void testClassStructureAndConstructors() throws Exception {
        Class<?> clazz = Class.forName("thaumcraft.common.items.curios.ItemSalisMundus", false, getClass().getClassLoader());
        assertEquals("net.minecraft.world.item.Item", clazz.getSuperclass().getName());

        boolean hasPropertiesConstructor = false;
        for (var ctor : clazz.getDeclaredConstructors()) {
            Class<?>[] params = ctor.getParameterTypes();
            if (params.length == 1 && params[0].getName().equals("net.minecraft.world.item.Item$Properties")) {
                hasPropertiesConstructor = true;
                break;
            }
        }
        assertTrue(hasPropertiesConstructor, "ItemSalisMundus must have (Item.Properties) constructor");

        boolean hasUseOn = false;
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals("useOn")) {
                hasUseOn = true;
                break;
            }
        }
        assertTrue(hasUseOn, "ItemSalisMundus must override useOn");
    }
}
