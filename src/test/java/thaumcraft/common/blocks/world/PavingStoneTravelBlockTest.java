package thaumcraft.common.blocks.world;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PavingStoneTravelBlock Contract Tests")
public class PavingStoneTravelBlockTest {

    @Test
    @DisplayName("Verify class hierarchy and stepOn method signature")
    public void testClassStructureAndConstants() throws Exception {
        Class<?> clazz = PavingStoneTravelBlock.class;

        // Verify it extends Block
        assertTrue(Block.class.isAssignableFrom(clazz));

        // Verify stepOn method exists and has the correct signature
        Method stepOnMethod = clazz.getMethod("stepOn", Level.class, BlockPos.class, BlockState.class, Entity.class);
        assertNotNull(stepOnMethod);
    }

    @Test
    @DisplayName("CODEC field and codec() method are properly implemented")
    public void testCodec() throws Exception {
        Field codecField = PavingStoneTravelBlock.class.getField("CODEC");
        assertTrue(Modifier.isPublic(codecField.getModifiers()), "CODEC must be public");
        assertTrue(Modifier.isStatic(codecField.getModifiers()), "CODEC must be static");
        assertTrue(Modifier.isFinal(codecField.getModifiers()), "CODEC must be final");
        assertEquals(MapCodec.class, codecField.getType());

        sun.misc.Unsafe unsafe = getUnsafe();
        PavingStoneTravelBlock block = (PavingStoneTravelBlock) unsafe.allocateInstance(PavingStoneTravelBlock.class);

        Method codecMethod = PavingStoneTravelBlock.class.getDeclaredMethod("codec");
        codecMethod.setAccessible(true);
        assertEquals(PavingStoneTravelBlock.CODEC, codecMethod.invoke(block),
                "codec() must return PavingStoneTravelBlock.CODEC");
    }

    private static sun.misc.Unsafe getUnsafe() throws Exception {
        Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (sun.misc.Unsafe) f.get(null);
    }
}
