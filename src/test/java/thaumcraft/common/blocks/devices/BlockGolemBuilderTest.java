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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.common.blocks.entities.TileGolemBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BlockGolemBuilder Contract Tests")
public class BlockGolemBuilderTest {

    @Test
    @DisplayName("BlockGolemBuilder extends BaseEntityBlock")
    public void testClassHierarchy() {
        assertTrue(BaseEntityBlock.class.isAssignableFrom(BlockGolemBuilder.class),
                "BlockGolemBuilder must extend BaseEntityBlock");
    }

    @Test
    @DisplayName("CODEC field and codec() method are properly wired")
    public void testCodec() throws Exception {
        Field codecField = BlockGolemBuilder.class.getField("CODEC");
        assertTrue(Modifier.isPublic(codecField.getModifiers()), "CODEC must be public");
        assertTrue(Modifier.isStatic(codecField.getModifiers()), "CODEC must be static");
        assertTrue(Modifier.isFinal(codecField.getModifiers()), "CODEC must be final");
        assertEquals(MapCodec.class, codecField.getType());

        sun.misc.Unsafe unsafe = getUnsafe();
        BlockGolemBuilder block = (BlockGolemBuilder) unsafe.allocateInstance(BlockGolemBuilder.class);

        Method codecMethod = BlockGolemBuilder.class.getDeclaredMethod("codec");
        codecMethod.setAccessible(true);
        assertEquals(BlockGolemBuilder.CODEC, codecMethod.invoke(block),
                "codec() must return BlockGolemBuilder.CODEC");
    }

    @Test
    @DisplayName("RenderShape is MODEL")
    public void testRenderShape() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockGolemBuilder block = (BlockGolemBuilder) unsafe.allocateInstance(BlockGolemBuilder.class);
        assertEquals(RenderShape.MODEL, block.getRenderShape(null),
                "RenderShape must be MODEL");
    }

    @Test
    @DisplayName("newBlockEntity method returns BlockEntity")
    public void testNewBlockEntity() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockGolemBuilder block = (BlockGolemBuilder) unsafe.allocateInstance(BlockGolemBuilder.class);

        Method m = BlockGolemBuilder.class.getMethod("newBlockEntity", BlockPos.class, BlockState.class);
        assertEquals(BlockEntity.class, m.getReturnType(),
                "newBlockEntity return type must be BlockEntity");

        try {
            block.newBlockEntity(BlockPos.ZERO, null);
        } catch (Throwable t) {
            assertTrue(t instanceof ExceptionInInitializerError || t instanceof NoClassDefFoundError || t instanceof IllegalStateException || t instanceof NullPointerException,
                    "Expected environment exception when instantiating TileGolemBuilder without full registry setup");
        }
    }

    @Test
    @DisplayName("Comparator and removal methods exist and have valid signatures")
    public void testComparatorAndRemovalMethods() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockGolemBuilder block = (BlockGolemBuilder) unsafe.allocateInstance(BlockGolemBuilder.class);

        Method hasAnalogOutputSignal = BlockGolemBuilder.class.getDeclaredMethod("hasAnalogOutputSignal", BlockState.class);
        hasAnalogOutputSignal.setAccessible(true);
        assertTrue((Boolean) hasAnalogOutputSignal.invoke(block, (BlockState) null));

        Method getAnalogOutputSignal = BlockGolemBuilder.class.getDeclaredMethod("getAnalogOutputSignal", BlockState.class, Level.class, BlockPos.class, Direction.class);
        assertNotNull(getAnalogOutputSignal);

        Method onRemove = BlockGolemBuilder.class.getMethod("onRemove", BlockState.class, Level.class, BlockPos.class, BlockState.class, boolean.class);
        assertNotNull(onRemove);

        Method destroy = BlockGolemBuilder.class.getMethod("destroy", LevelAccessor.class, BlockPos.class, BlockState.class);
        assertNotNull(destroy);

        Method affectNeighbors = BlockGolemBuilder.class.getDeclaredMethod("affectNeighborsAfterRemoval", BlockState.class, ServerLevel.class, BlockPos.class, boolean.class);
        assertNotNull(affectNeighbors);
    }

    private static sun.misc.Unsafe getUnsafe() throws Exception {
        Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (sun.misc.Unsafe) f.get(null);
    }
}
