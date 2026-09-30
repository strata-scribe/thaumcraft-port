package thaumcraft.common.blocks.world;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PavingStoneBarrierBlock Contract Tests")
public class PavingStoneBarrierBlockTest {

    @Test
    @DisplayName("Verify class hierarchy and structural methods")
    void testBlockStructureAndConstants() throws Exception {
        Class<?> clazz = PavingStoneBarrierBlock.class;

        // Verify it extends Block
        assertTrue(Block.class.isAssignableFrom(clazz));

        // Verify getCollisionShape method is overridden
        Method getCollisionShapeMethod = clazz.getDeclaredMethod("getCollisionShape", BlockState.class, BlockGetter.class, BlockPos.class, CollisionContext.class);
        assertNotNull(getCollisionShapeMethod);
        assertEquals(VoxelShape.class, getCollisionShapeMethod.getReturnType());

        // Verify BARRIER_SHAPE field exists
        Field barrierShapeField = clazz.getDeclaredField("BARRIER_SHAPE");
        assertNotNull(barrierShapeField);
        assertTrue(Modifier.isStatic(barrierShapeField.getModifiers()));
        assertTrue(Modifier.isProtected(barrierShapeField.getModifiers()));
    }

    @Test
    @DisplayName("CODEC field and codec() method are properly implemented")
    void testCodec() throws Exception {
        Field codecField = PavingStoneBarrierBlock.class.getField("CODEC");
        assertTrue(Modifier.isPublic(codecField.getModifiers()), "CODEC must be public");
        assertTrue(Modifier.isStatic(codecField.getModifiers()), "CODEC must be static");
        assertTrue(Modifier.isFinal(codecField.getModifiers()), "CODEC must be final");
        assertEquals(MapCodec.class, codecField.getType());

        sun.misc.Unsafe unsafe = getUnsafe();
        PavingStoneBarrierBlock block = (PavingStoneBarrierBlock) unsafe.allocateInstance(PavingStoneBarrierBlock.class);

        Method codecMethod = PavingStoneBarrierBlock.class.getDeclaredMethod("codec");
        codecMethod.setAccessible(true);
        assertEquals(PavingStoneBarrierBlock.CODEC, codecMethod.invoke(block),
                "codec() must return PavingStoneBarrierBlock.CODEC");
    }

    @Test
    @DisplayName("BARRIER_SHAPE bounds match 0..16, 0..24, 0..16 pixels (0..1, 0..1.5, 0..1 blocks)")
    void testBarrierShapeBounds() {
        VoxelShape shape = PavingStoneBarrierBlock.BARRIER_SHAPE;
        assertNotNull(shape);

        assertEquals(0.0, shape.min(Direction.Axis.X), 1e-6, "minX must be 0.0");
        assertEquals(1.0, shape.max(Direction.Axis.X), 1e-6, "maxX must be 1.0 (16px)");
        assertEquals(0.0, shape.min(Direction.Axis.Y), 1e-6, "minY must be 0.0");
        assertEquals(1.5, shape.max(Direction.Axis.Y), 1e-6, "maxY must be 1.5 (24px)");
        assertEquals(0.0, shape.min(Direction.Axis.Z), 1e-6, "minZ must be 0.0");
        assertEquals(1.0, shape.max(Direction.Axis.Z), 1e-6, "maxZ must be 1.0 (16px)");
    }

    private static sun.misc.Unsafe getUnsafe() throws Exception {
        Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (sun.misc.Unsafe) f.get(null);
    }
}
