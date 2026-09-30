package thaumcraft.common.blocks.essentia;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
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

@DisplayName("BlockSmelterVent Contract Tests")
public class BlockSmelterVentTest {

    @Test
    @DisplayName("BlockSmelterVent extends Block")
    void testClassHierarchy() {
        assertTrue(Block.class.isAssignableFrom(BlockSmelterVent.class),
                "BlockSmelterVent must extend Block");
    }

    @Test
    @DisplayName("CODEC field and codec() method are properly implemented")
    void testCodec() throws Exception {
        Field codecField = BlockSmelterVent.class.getField("CODEC");
        assertTrue(Modifier.isPublic(codecField.getModifiers()));
        assertTrue(Modifier.isStatic(codecField.getModifiers()));
        assertTrue(Modifier.isFinal(codecField.getModifiers()));
        assertEquals(MapCodec.class, codecField.getType());

        sun.misc.Unsafe unsafe = getUnsafe();
        BlockSmelterVent block = (BlockSmelterVent) unsafe.allocateInstance(BlockSmelterVent.class);

        Method codecMethod = BlockSmelterVent.class.getDeclaredMethod("codec");
        codecMethod.setAccessible(true);
        assertEquals(BlockSmelterVent.CODEC, codecMethod.invoke(block),
                "codec() must return BlockSmelterVent.CODEC");
    }

    @Test
    @DisplayName("canSurvive method is declared with correct signature")
    void testCanSurviveSignature() throws Exception {
        Method canSurviveMethod = BlockSmelterVent.class.getDeclaredMethod("canSurvive",
                BlockState.class, LevelReader.class, BlockPos.class);
        assertNotNull(canSurviveMethod);
        assertEquals(boolean.class, canSurviveMethod.getReturnType());
    }

    @Test
    @DisplayName("getShape method is declared with correct signature")
    void testGetShapeSignature() throws Exception {
        Method getShapeMethod = BlockSmelterVent.class.getDeclaredMethod("getShape",
                BlockState.class, BlockGetter.class, BlockPos.class, CollisionContext.class);
        assertNotNull(getShapeMethod);
        assertEquals(VoxelShape.class, getShapeMethod.getReturnType());
    }

    private static sun.misc.Unsafe getUnsafe() throws Exception {
        Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (sun.misc.Unsafe) f.get(null);
    }
}
