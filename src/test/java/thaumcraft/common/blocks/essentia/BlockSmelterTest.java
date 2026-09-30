package thaumcraft.common.blocks.essentia;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.common.tiles.essentia.SmelterBlockEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BlockSmelter Contract Tests")
public class BlockSmelterTest {

    @Test
    @DisplayName("BlockSmelter extends BaseEntityBlock")
    void testClassHierarchy() {
        assertTrue(BaseEntityBlock.class.isAssignableFrom(BlockSmelter.class),
                "BlockSmelter must extend BaseEntityBlock");
    }

    @Test
    @DisplayName("CODEC field and codec() method are properly implemented")
    void testCodec() throws Exception {
        Field codecField = BlockSmelter.class.getField("CODEC");
        assertTrue(Modifier.isPublic(codecField.getModifiers()));
        assertTrue(Modifier.isStatic(codecField.getModifiers()));
        assertTrue(Modifier.isFinal(codecField.getModifiers()));
        assertEquals(MapCodec.class, codecField.getType());

        sun.misc.Unsafe unsafe = getUnsafe();
        BlockSmelter block = (BlockSmelter) unsafe.allocateInstance(BlockSmelter.class);

        Method codecMethod = BlockSmelter.class.getDeclaredMethod("codec");
        codecMethod.setAccessible(true);
        assertEquals(BlockSmelter.CODEC, codecMethod.invoke(block),
                "codec() must return BlockSmelter.CODEC");
    }

    @Test
    @DisplayName("getRenderShape returns MODEL")
    void testRenderShape() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockSmelter block = (BlockSmelter) unsafe.allocateInstance(BlockSmelter.class);
        assertEquals(RenderShape.MODEL, block.getRenderShape(null),
                "RenderShape must be MODEL");
    }

    @Test
    @DisplayName("newBlockEntity method exists and returns BlockEntity")
    void testNewBlockEntityMethod() throws Exception {
        Method m = BlockSmelter.class.getMethod("newBlockEntity", BlockPos.class, BlockState.class);
        assertEquals(BlockEntity.class, m.getReturnType(),
                "newBlockEntity return type must be BlockEntity");
    }

    @Test
    @DisplayName("getTicker method exists and returns BlockEntityTicker")
    void testGetTickerMethod() throws Exception {
        Method getTickerMethod = BlockSmelter.class.getMethod("getTicker", Level.class, BlockState.class, BlockEntityType.class);
        assertNotNull(getTickerMethod, "getTicker method must exist");
        assertEquals(net.minecraft.world.level.block.entity.BlockEntityTicker.class, getTickerMethod.getReturnType());

        Method serverTickMethod = SmelterBlockEntity.class.getMethod("serverTick", Level.class, BlockPos.class, BlockState.class, SmelterBlockEntity.class);
        assertTrue(Modifier.isStatic(serverTickMethod.getModifiers()));
    }

    @Test
    @DisplayName("Removal methods: onRemove, destroy, and affectNeighborsAfterRemoval are implemented")
    void testRemovalMethods() throws Exception {
        Method onRemove = BlockSmelter.class.getMethod("onRemove",
                BlockState.class, Level.class, BlockPos.class, BlockState.class, boolean.class);
        assertNotNull(onRemove, "onRemove method must exist on BlockSmelter");

        Method destroy = BlockSmelter.class.getMethod("destroy",
                LevelAccessor.class, BlockPos.class, BlockState.class);
        assertNotNull(destroy, "destroy method must exist on BlockSmelter");

        Method affectNeighbors = BlockSmelter.class.getDeclaredMethod("affectNeighborsAfterRemoval",
                BlockState.class, ServerLevel.class, BlockPos.class, boolean.class);
        assertNotNull(affectNeighbors, "affectNeighborsAfterRemoval method must exist on BlockSmelter");
    }

    @Test
    @DisplayName("Comparator methods: hasAnalogOutputSignal and getAnalogOutputSignal are present and correct")
    void testComparatorMethods() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockSmelter block = (BlockSmelter) unsafe.allocateInstance(BlockSmelter.class);

        Method hasAnalogOutputSignal = BlockSmelter.class.getDeclaredMethod("hasAnalogOutputSignal", BlockState.class);
        hasAnalogOutputSignal.setAccessible(true);
        assertTrue((Boolean) hasAnalogOutputSignal.invoke(block, (BlockState) null));

        Method getAnalogOutputSignal = BlockSmelter.class.getDeclaredMethod("getAnalogOutputSignal",
                BlockState.class, Level.class, BlockPos.class, Direction.class);
        getAnalogOutputSignal.setAccessible(true);
        assertNotNull(getAnalogOutputSignal);
    }

    private static sun.misc.Unsafe getUnsafe() throws Exception {
        Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (sun.misc.Unsafe) f.get(null);
    }
}
