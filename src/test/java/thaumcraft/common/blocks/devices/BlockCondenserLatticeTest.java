package thaumcraft.common.blocks.devices;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BlockCondenserLattice Contract Tests")
public class BlockCondenserLatticeTest {

    @Test
    @DisplayName("BlockCondenserLattice extends Block")
    void testClassHierarchy() {
        assertTrue(Block.class.isAssignableFrom(BlockCondenserLattice.class),
                "BlockCondenserLattice must extend Block");
    }

    @Test
    @DisplayName("CODEC field and codec() method are properly implemented")
    void testCodec() throws Exception {
        Field codecField = BlockCondenserLattice.class.getField("CODEC");
        assertTrue(Modifier.isPublic(codecField.getModifiers()), "CODEC must be public");
        assertTrue(Modifier.isStatic(codecField.getModifiers()), "CODEC must be static");
        assertTrue(Modifier.isFinal(codecField.getModifiers()), "CODEC must be final");
        assertEquals(MapCodec.class, codecField.getType());

        sun.misc.Unsafe unsafe = getUnsafe();
        BlockCondenserLattice block = (BlockCondenserLattice) unsafe.allocateInstance(BlockCondenserLattice.class);

        Method codecMethod = BlockCondenserLattice.class.getDeclaredMethod("codec");
        codecMethod.setAccessible(true);
        assertEquals(BlockCondenserLattice.CODEC, codecMethod.invoke(block),
                "codec() must return BlockCondenserLattice.CODEC");
    }

    @Test
    @DisplayName("isDirty getter reflects dirty state for clean and dirty instances")
    void testIsDirty() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        Field dirtyField = BlockCondenserLattice.class.getDeclaredField("dirty");
        dirtyField.setAccessible(true);

        BlockCondenserLattice cleanInstance = (BlockCondenserLattice) unsafe.allocateInstance(BlockCondenserLattice.class);
        dirtyField.setBoolean(cleanInstance, false);
        assertFalse(cleanInstance.isDirty(), "clean instance should return false for isDirty");

        BlockCondenserLattice dirtyInstance = (BlockCondenserLattice) unsafe.allocateInstance(BlockCondenserLattice.class);
        dirtyField.setBoolean(dirtyInstance, true);
        assertTrue(dirtyInstance.isDirty(), "dirty instance should return true for isDirty");
    }

    private static sun.misc.Unsafe getUnsafe() throws Exception {
        Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (sun.misc.Unsafe) f.get(null);
    }
}
