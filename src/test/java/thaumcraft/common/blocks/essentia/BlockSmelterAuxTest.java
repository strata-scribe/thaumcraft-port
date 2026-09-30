package thaumcraft.common.blocks.essentia;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BlockSmelterAux Contract Tests")
public class BlockSmelterAuxTest {

    @Test
    @DisplayName("BlockSmelterAux extends Block")
    void testClassHierarchy() {
        assertTrue(Block.class.isAssignableFrom(BlockSmelterAux.class),
                "BlockSmelterAux must extend Block");
    }

    @Test
    @DisplayName("CODEC field and codec() method are properly implemented")
    void testCodec() throws Exception {
        Field codecField = BlockSmelterAux.class.getField("CODEC");
        assertTrue(Modifier.isPublic(codecField.getModifiers()));
        assertTrue(Modifier.isStatic(codecField.getModifiers()));
        assertTrue(Modifier.isFinal(codecField.getModifiers()));
        assertEquals(MapCodec.class, codecField.getType());

        sun.misc.Unsafe unsafe = getUnsafe();
        BlockSmelterAux block = (BlockSmelterAux) unsafe.allocateInstance(BlockSmelterAux.class);

        Method codecMethod = BlockSmelterAux.class.getDeclaredMethod("codec");
        codecMethod.setAccessible(true);
        assertEquals(BlockSmelterAux.CODEC, codecMethod.invoke(block),
                "codec() must return BlockSmelterAux.CODEC");
    }

    @Test
    @DisplayName("canSurvive method is declared with correct signature")
    void testCanSurviveSignature() throws Exception {
        Method canSurviveMethod = BlockSmelterAux.class.getDeclaredMethod("canSurvive",
                BlockState.class, LevelReader.class, BlockPos.class);
        assertNotNull(canSurviveMethod);
        assertEquals(boolean.class, canSurviveMethod.getReturnType());
    }

    private static sun.misc.Unsafe getUnsafe() throws Exception {
        Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (sun.misc.Unsafe) f.get(null);
    }
}
