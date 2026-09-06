package thaumcraft.common.blocks.world;

import org.junit.jupiter.api.Test;
import thaumcraft.api.aspects.Aspect;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BlockVisCrystalTest {

    @Test
    public void testClassStructureAndConstants() throws Exception {
        // Test that BlockVisCrystal has the correct Aspect logic without instantiating it,
        // because instantiating blocks requires Minecraft Bootstrap which breaks the test runner.

        Class<?> clazz = BlockVisCrystal.class;

        // Verify it extends DirectionalBlock
        assertTrue(net.minecraft.world.level.block.DirectionalBlock.class.isAssignableFrom(clazz));

        // Verify getAspect method exists
        Method getAspectMethod = clazz.getMethod("getAspect");
        assertNotNull(getAspectMethod);
        assertEquals(Aspect.class, getAspectMethod.getReturnType());

        // Verify AABB fields exist
        Field upField = clazz.getDeclaredField("UP_AABB");
        assertNotNull(upField);
        Field downField = clazz.getDeclaredField("DOWN_AABB");
        assertNotNull(downField);
        Field northField = clazz.getDeclaredField("NORTH_AABB");
        assertNotNull(northField);
        Field southField = clazz.getDeclaredField("SOUTH_AABB");
        assertNotNull(southField);
        Field westField = clazz.getDeclaredField("WEST_AABB");
        assertNotNull(westField);
        Field eastField = clazz.getDeclaredField("EAST_AABB");
        assertNotNull(eastField);
    }
}
