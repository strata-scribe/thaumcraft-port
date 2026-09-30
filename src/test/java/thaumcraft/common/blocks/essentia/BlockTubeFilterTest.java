package thaumcraft.common.blocks.essentia;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BlockTubeFilter Contract Tests")
public class BlockTubeFilterTest {

    @Test
    @DisplayName("BlockTubeFilter extends BlockTube and BaseEntityBlock")
    void testClassHierarchy() {
        assertTrue(BlockTube.class.isAssignableFrom(BlockTubeFilter.class),
                "BlockTubeFilter must extend BlockTube");
        assertTrue(BaseEntityBlock.class.isAssignableFrom(BlockTubeFilter.class),
                "BlockTubeFilter must extend BaseEntityBlock");
    }

    @Test
    @DisplayName("CODEC field is public, static, final, and returns MapCodec")
    void testCodecIsDefined() throws Exception {
        Field codecField = BlockTubeFilter.class.getDeclaredField("CODEC");
        assertNotNull(codecField, "CODEC field should exist");
        assertTrue(Modifier.isStatic(codecField.getModifiers()), "CODEC should be static");
        assertTrue(Modifier.isPublic(codecField.getModifiers()), "CODEC should be public");
        assertTrue(Modifier.isFinal(codecField.getModifiers()), "CODEC should be final");
        assertEquals(MapCodec.class, codecField.getType(), "CODEC should be of type MapCodec");
    }

    @Test
    @DisplayName("codec() method is overridden and returns BlockTubeFilter.CODEC")
    void testCodecMethodOverride() throws Exception {
        Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        sun.misc.Unsafe unsafe = (sun.misc.Unsafe) f.get(null);
        BlockTubeFilter block = (BlockTubeFilter) unsafe.allocateInstance(BlockTubeFilter.class);

        Method codecMethod = BlockTubeFilter.class.getDeclaredMethod("codec");
        codecMethod.setAccessible(true);
        assertNotNull(codecMethod, "codec() method should exist");
        assertEquals(MapCodec.class, codecMethod.getReturnType(), "codec() should return MapCodec");
        assertEquals(BlockTubeFilter.CODEC, codecMethod.invoke(block), "codec() should return BlockTubeFilter.CODEC");
    }

    @Test
    @DisplayName("newBlockEntity() method exists and overrides TileTubeFilter creation")
    void testNewBlockEntityOverride() throws Exception {
        Method newBlockEntityMethod = BlockTubeFilter.class.getDeclaredMethod("newBlockEntity", BlockPos.class, BlockState.class);
        assertNotNull(newBlockEntityMethod, "newBlockEntity() method should exist");
    }
}
