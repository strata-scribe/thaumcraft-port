package thaumcraft.common.blocks.devices;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

public class BlockLevitatorTest {

    @Test
    public void testCodec() throws Exception {
        Class<?> clazz = Class.forName("thaumcraft.common.blocks.devices.BlockLevitator", false, this.getClass().getClassLoader());
        Field codecField = clazz.getField("CODEC");

        assertTrue(Modifier.isPublic(codecField.getModifiers()));
        assertTrue(Modifier.isStatic(codecField.getModifiers()));
        assertTrue(Modifier.isFinal(codecField.getModifiers()));
        assertEquals(MapCodec.class, codecField.getType());
    }

    @Test
    public void testOverrides() throws Exception {
        Class<?> clazz = Class.forName("thaumcraft.common.blocks.devices.BlockLevitator", false, this.getClass().getClassLoader());

        Method codecMethod = clazz.getDeclaredMethod("codec");
        assertEquals(MapCodec.class, codecMethod.getReturnType());

        Method newBlockEntityMethod = clazz.getDeclaredMethod("newBlockEntity", BlockPos.class, BlockState.class);
        assertEquals(net.minecraft.world.level.block.entity.BlockEntity.class, newBlockEntityMethod.getReturnType());

        Method getRenderShapeMethod = clazz.getDeclaredMethod("getRenderShape", BlockState.class);
        assertEquals(RenderShape.class, getRenderShapeMethod.getReturnType());
    }
}
