package thaumcraft.common.blocks.essentia;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.common.tiles.essentia.TubeRestrictBlockEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BlockTubeRestrict Contract Tests")
public class BlockTubeRestrictTest {

    @Test
    @DisplayName("BlockTubeRestrict extends BlockTube and BaseEntityBlock")
    void testClassHierarchy() {
        assertTrue(BlockTube.class.isAssignableFrom(BlockTubeRestrict.class),
                "BlockTubeRestrict must extend BlockTube");
        assertTrue(BaseEntityBlock.class.isAssignableFrom(BlockTubeRestrict.class),
                "BlockTubeRestrict must extend BaseEntityBlock");
    }

    @Test
    @DisplayName("CODEC field is public, static, final, and returns MapCodec")
    void testCodecField() throws Exception {
        Field codecField = BlockTubeRestrict.class.getField("CODEC");
        assertNotNull(codecField, "CODEC field must exist");
        assertTrue(Modifier.isPublic(codecField.getModifiers()), "CODEC must be public");
        assertTrue(Modifier.isStatic(codecField.getModifiers()), "CODEC must be static");
        assertTrue(Modifier.isFinal(codecField.getModifiers()), "CODEC must be final");
        assertEquals(MapCodec.class, codecField.getType(), "CODEC must be of type MapCodec");
    }

    @Test
    @DisplayName("codec() method is overridden and returns BlockTubeRestrict.CODEC")
    void testCodecMethodOverride() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockTubeRestrict block = (BlockTubeRestrict) unsafe.allocateInstance(BlockTubeRestrict.class);

        Method codecMethod = BlockTubeRestrict.class.getDeclaredMethod("codec");
        codecMethod.setAccessible(true);
        assertEquals(BlockTubeRestrict.CODEC, codecMethod.invoke(block),
                "codec() must return BlockTubeRestrict.CODEC");
    }

    @Test
    @DisplayName("newBlockEntity: method exists and returns BlockEntity")
    void testNewBlockEntityMethod() throws Exception {
        Method m = BlockTubeRestrict.class.getMethod("newBlockEntity", BlockPos.class, BlockState.class);
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
