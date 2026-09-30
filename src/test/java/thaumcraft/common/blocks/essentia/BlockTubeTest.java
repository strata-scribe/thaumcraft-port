package thaumcraft.common.blocks.essentia;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BlockTube Contract Tests")
public class BlockTubeTest {

    @Test
    @DisplayName("BlockTube extends BaseEntityBlock")
    void testClassHierarchy() {
        assertTrue(BaseEntityBlock.class.isAssignableFrom(BlockTube.class),
                "BlockTube must extend BaseEntityBlock");
    }

    @Test
    @DisplayName("CODEC field is public, static, final, and returns MapCodec")
    void testCodecField() throws Exception {
        Field codecField = BlockTube.class.getField("CODEC");
        assertNotNull(codecField, "CODEC field must exist");
        assertTrue(Modifier.isPublic(codecField.getModifiers()), "CODEC must be public");
        assertTrue(Modifier.isStatic(codecField.getModifiers()), "CODEC must be static");
        assertTrue(Modifier.isFinal(codecField.getModifiers()), "CODEC must be final");
        assertEquals(MapCodec.class, codecField.getType(), "CODEC must be of type MapCodec");
    }

    @Test
    @DisplayName("codec() method is overridden and returns BlockTube.CODEC")
    void testCodecMethodOverride() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockTube block = (BlockTube) unsafe.allocateInstance(BlockTube.class);

        Method codecMethod = BlockTube.class.getDeclaredMethod("codec");
        codecMethod.setAccessible(true);
        assertEquals(BlockTube.CODEC, codecMethod.invoke(block),
                "codec() must return BlockTube.CODEC");
    }

    @Test
    @DisplayName("getRenderShape returns MODEL")
    void testRenderShape() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockTube block = (BlockTube) unsafe.allocateInstance(BlockTube.class);
        assertEquals(RenderShape.MODEL, block.getRenderShape(null),
                "RenderShape must be MODEL");
    }

    @Test
    @DisplayName("VoxelShape: central core bounding box matches 4..12 on all axes")
    void testVoxelShape() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockTube block = (BlockTube) unsafe.allocateInstance(BlockTube.class);

        Method getShapeMethod = BlockTube.class.getDeclaredMethod("getShape",
                BlockState.class, BlockGetter.class, BlockPos.class, CollisionContext.class);
        getShapeMethod.setAccessible(true);
        VoxelShape shape = (VoxelShape) getShapeMethod.invoke(block, null, null, null, null);
        assertNotNull(shape, "VoxelShape must not be null");
        assertFalse(shape.isEmpty(), "VoxelShape must not be empty");

        assertEquals(4.0 / 16.0, shape.bounds().minX, 1e-6);
        assertEquals(4.0 / 16.0, shape.bounds().minY, 1e-6);
        assertEquals(4.0 / 16.0, shape.bounds().minZ, 1e-6);
        assertEquals(12.0 / 16.0, shape.bounds().maxX, 1e-6);
        assertEquals(12.0 / 16.0, shape.bounds().maxY, 1e-6);
        assertEquals(12.0 / 16.0, shape.bounds().maxZ, 1e-6);
    }

    @Test
    @DisplayName("getPropertyForDirection maps all 6 cardinal directions to their BooleanProperty")
    void testGetPropertyForDirection() {
        assertSame(BlockTube.NORTH, BlockTube.getPropertyForDirection(Direction.NORTH));
        assertSame(BlockTube.SOUTH, BlockTube.getPropertyForDirection(Direction.SOUTH));
        assertSame(BlockTube.EAST, BlockTube.getPropertyForDirection(Direction.EAST));
        assertSame(BlockTube.WEST, BlockTube.getPropertyForDirection(Direction.WEST));
        assertSame(BlockTube.UP, BlockTube.getPropertyForDirection(Direction.UP));
        assertSame(BlockTube.DOWN, BlockTube.getPropertyForDirection(Direction.DOWN));
    }

    @Test
    @DisplayName("newBlockEntity method is declared with correct signature")
    void testNewBlockEntitySignature() throws Exception {
        Method m = BlockTube.class.getMethod("newBlockEntity", BlockPos.class, BlockState.class);
        assertNotNull(m);
        assertEquals(BlockEntity.class, m.getReturnType(),
                "newBlockEntity return type must be BlockEntity");
    }

    private static sun.misc.Unsafe getUnsafe() throws Exception {
        Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (sun.misc.Unsafe) f.get(null);
    }
}
