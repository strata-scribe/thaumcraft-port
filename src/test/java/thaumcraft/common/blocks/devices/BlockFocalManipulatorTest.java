package thaumcraft.common.blocks.devices;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BlockFocalManipulator Contract Tests")
public class BlockFocalManipulatorTest {

    private static Class<?> getTargetClass() throws Exception {
        // Use initialize=false to prevent BlockStateProperties static initializer from triggering
        // unbootstrapped Minecraft registries in a raw JUnit 5 headless test environment.
        return Class.forName("thaumcraft.common.blocks.devices.BlockFocalManipulator", false,
                BlockFocalManipulatorTest.class.getClassLoader());
    }

    @Test
    @DisplayName("BlockFocalManipulator extends BaseEntityBlock")
    public void testClassHierarchy() throws Exception {
        Class<?> clazz = getTargetClass();
        assertTrue(BaseEntityBlock.class.isAssignableFrom(clazz),
                "BlockFocalManipulator must extend BaseEntityBlock");
    }

    @Test
    @DisplayName("CODEC field and codec() method are properly wired")
    public void testCodec() throws Exception {
        Class<?> clazz = getTargetClass();
        Field codecField = clazz.getField("CODEC");
        assertTrue(Modifier.isPublic(codecField.getModifiers()), "CODEC must be public");
        assertTrue(Modifier.isStatic(codecField.getModifiers()), "CODEC must be static");
        assertTrue(Modifier.isFinal(codecField.getModifiers()), "CODEC must be final");
        assertEquals(MapCodec.class, codecField.getType());

        Method codecMethod = clazz.getDeclaredMethod("codec");
        assertEquals(MapCodec.class, codecMethod.getReturnType(),
                "codec() must return MapCodec");
    }

    @Test
    @DisplayName("RenderShape is MODEL")
    public void testRenderShape() throws Exception {
        Class<?> clazz = getTargetClass();
        Method renderShapeMethod = clazz.getDeclaredMethod("getRenderShape", BlockState.class);
        assertEquals(RenderShape.class, renderShapeMethod.getReturnType(),
                "getRenderShape must return RenderShape");
    }

    @Test
    @DisplayName("FACING property is HorizontalFacing EnumProperty<Direction>")
    public void testFacingProperty() throws Exception {
        Class<?> clazz = getTargetClass();
        Field facingField = clazz.getField("FACING");
        assertTrue(Modifier.isPublic(facingField.getModifiers()), "FACING must be public");
        assertTrue(Modifier.isStatic(facingField.getModifiers()), "FACING must be static");
        assertTrue(Modifier.isFinal(facingField.getModifiers()), "FACING must be final");
        assertEquals(EnumProperty.class, facingField.getType(), "FACING must be an EnumProperty");
        assertTrue(facingField.getGenericType().getTypeName().contains("Direction"),
                "FACING property must have generic type Direction");
    }

    @Test
    @DisplayName("newBlockEntity method returns BlockEntity")
    public void testNewBlockEntity() throws Exception {
        Class<?> clazz = getTargetClass();
        Method m = clazz.getMethod("newBlockEntity", BlockPos.class, BlockState.class);
        assertEquals(BlockEntity.class, m.getReturnType(),
                "newBlockEntity return type must be BlockEntity");
    }

    @Test
    @DisplayName("Comparator and removal methods exist and have valid signatures")
    public void testComparatorAndRemovalMethods() throws Exception {
        Class<?> clazz = getTargetClass();

        Method hasAnalogOutputSignal = clazz.getDeclaredMethod("hasAnalogOutputSignal", BlockState.class);
        assertEquals(boolean.class, hasAnalogOutputSignal.getReturnType());

        Method getAnalogOutputSignal = clazz.getDeclaredMethod("getAnalogOutputSignal", BlockState.class, Level.class, BlockPos.class, Direction.class);
        assertEquals(int.class, getAnalogOutputSignal.getReturnType());

        Method onRemove = clazz.getMethod("onRemove", BlockState.class, Level.class, BlockPos.class, BlockState.class, boolean.class);
        assertNotNull(onRemove);

        Method destroy = clazz.getMethod("destroy", LevelAccessor.class, BlockPos.class, BlockState.class);
        assertNotNull(destroy);

        Method affectNeighbors = clazz.getDeclaredMethod("affectNeighborsAfterRemoval", BlockState.class, ServerLevel.class, BlockPos.class, boolean.class);
        assertNotNull(affectNeighbors);
    }
}
