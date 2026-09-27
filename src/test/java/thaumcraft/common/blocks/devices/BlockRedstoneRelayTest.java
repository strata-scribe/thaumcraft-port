package thaumcraft.common.blocks.devices;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

public class BlockRedstoneRelayTest {

    @Test
    public void testInheritance() {
        assertTrue(BaseEntityBlock.class.isAssignableFrom(BlockRedstoneRelay.class), "BlockRedstoneRelay should extend BaseEntityBlock");
    }

    @Test
    public void testCodecFieldExists() throws NoSuchFieldException {
        Field codecField = BlockRedstoneRelay.class.getDeclaredField("CODEC");
        assertNotNull(codecField, "CODEC field should exist");
        assertTrue(Modifier.isStatic(codecField.getModifiers()), "CODEC field should be static");
        assertTrue(Modifier.isFinal(codecField.getModifiers()), "CODEC field should be final");
        assertTrue(Modifier.isPublic(codecField.getModifiers()), "CODEC field should be public");
        assertEquals(MapCodec.class, codecField.getType(), "CODEC field should be of type MapCodec");
    }

    @Test
    public void testCodecMethodExists() throws NoSuchMethodException {
        Method codecMethod = BlockRedstoneRelay.class.getDeclaredMethod("codec");
        assertNotNull(codecMethod, "codec method should exist");
        assertTrue(Modifier.isProtected(codecMethod.getModifiers()), "codec method should be protected");
        assertEquals(MapCodec.class, codecMethod.getReturnType(), "codec method should return MapCodec");
    }
}
