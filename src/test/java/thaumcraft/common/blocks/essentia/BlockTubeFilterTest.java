package thaumcraft.common.blocks.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.Test;
import thaumcraft.common.tiles.essentia.TileTubeFilter;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class BlockTubeFilterTest {

    @Test
    public void testCodecIsDefined() throws Exception {
        Field codecField = BlockTubeFilter.class.getDeclaredField("CODEC");
        assertNotNull(codecField, "CODEC field should exist");
        assertTrue(java.lang.reflect.Modifier.isStatic(codecField.getModifiers()), "CODEC should be static");
        assertTrue(java.lang.reflect.Modifier.isPublic(codecField.getModifiers()), "CODEC should be public");
        assertTrue(java.lang.reflect.Modifier.isFinal(codecField.getModifiers()), "CODEC should be final");
        assertEquals(MapCodec.class, codecField.getType(), "CODEC should be of type MapCodec");
    }

    @Test
    public void testCodecMethodOverride() throws Exception {
        Method codecMethod = BlockTubeFilter.class.getDeclaredMethod("codec");
        codecMethod.setAccessible(true);
        assertNotNull(codecMethod, "codec() method should exist");
        assertEquals(MapCodec.class, codecMethod.getReturnType(), "codec() should return MapCodec");
    }

    @Test
    public void testNewBlockEntityOverride() throws Exception {
        Method newBlockEntityMethod = BlockTubeFilter.class.getDeclaredMethod("newBlockEntity", BlockPos.class, BlockState.class);
        assertNotNull(newBlockEntityMethod, "newBlockEntity() method should exist");
    }
}
