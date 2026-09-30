package thaumcraft.common.blocks.devices;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.junit.jupiter.api.Test;
import thaumcraft.api.blocks.ThaumcraftBlocks;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;
import thaumcraft.common.tiles.devices.VisBatteryBlockEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

public class BlockVisBatteryTest {

    @Test
    public void testClassHierarchy() {
        assertTrue(BaseEntityBlock.class.isAssignableFrom(BlockVisBattery.class),
                "BlockVisBattery must extend BaseEntityBlock");
    }

    @Test
    public void testRegistration() throws Exception {
        Field field = ThaumcraftBlocks.class.getField("visBattery");
        assertNotNull(field, "ThaumcraftBlocks.visBattery field must exist");
        assertTrue(Modifier.isPublic(field.getModifiers()));
        assertTrue(Modifier.isStatic(field.getModifiers()));
        assertEquals(DeferredBlock.class, field.getType());
        assertTrue(field.getGenericType().getTypeName().contains("BlockVisBattery"),
                "ThaumcraftBlocks.visBattery should have generic type DeferredBlock<BlockVisBattery>");
    }

    @Test
    public void testCodec() throws Exception {
        Field codecField = BlockVisBattery.class.getField("CODEC");
        assertTrue(Modifier.isPublic(codecField.getModifiers()));
        assertTrue(Modifier.isStatic(codecField.getModifiers()));
        assertTrue(Modifier.isFinal(codecField.getModifiers()));
        assertEquals(MapCodec.class, codecField.getType());

        sun.misc.Unsafe unsafe = getUnsafe();
        BlockVisBattery block = (BlockVisBattery) unsafe.allocateInstance(BlockVisBattery.class);

        Method codecMethod = BlockVisBattery.class.getDeclaredMethod("codec");
        codecMethod.setAccessible(true);
        assertEquals(BlockVisBattery.CODEC, codecMethod.invoke(block),
                "codec() must return BlockVisBattery.CODEC");
    }

    @Test
    public void testRenderShape() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockVisBattery block = (BlockVisBattery) unsafe.allocateInstance(BlockVisBattery.class);
        assertEquals(RenderShape.MODEL, block.getRenderShape(null),
                "RenderShape must be MODEL");
    }

    @Test
    public void testNewBlockEntity() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockVisBattery block = (BlockVisBattery) unsafe.allocateInstance(BlockVisBattery.class);

        Method m = BlockVisBattery.class.getMethod("newBlockEntity", BlockPos.class, BlockState.class);
        assertEquals(BlockEntity.class, m.getReturnType(),
                "newBlockEntity return type must be BlockEntity");

        try {
            block.newBlockEntity(BlockPos.ZERO, null);
            fail("Expected FML/registry loading exception in headless test");
        } catch (Throwable t) {
            assertTrue(t instanceof ExceptionInInitializerError || t instanceof NoClassDefFoundError || t instanceof IllegalStateException,
                    "Expected FML environment exception when instantiating VisBatteryBlockEntity in headless test");
        }
    }

    @Test
    public void testTickerHelperWiring() throws Exception {
        Method getTickerMethod = BlockVisBattery.class.getMethod("getTicker", Level.class, BlockState.class, BlockEntityType.class);
        assertNotNull(getTickerMethod, "getTicker method must exist");
        assertEquals(net.minecraft.world.level.block.entity.BlockEntityTicker.class, getTickerMethod.getReturnType());

        // Check serverTick signature on VisBatteryBlockEntity
        Method serverTickMethod = VisBatteryBlockEntity.class.getMethod("serverTick", Level.class, BlockPos.class, BlockState.class, VisBatteryBlockEntity.class);
        assertTrue(Modifier.isStatic(serverTickMethod.getModifiers()));
    }

    @Test
    public void testComparatorSignalLogic() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockVisBattery block = (BlockVisBattery) unsafe.allocateInstance(BlockVisBattery.class);

        Method hasAnalogOutputSignal = BlockVisBattery.class.getDeclaredMethod("hasAnalogOutputSignal", BlockState.class);
        hasAnalogOutputSignal.setAccessible(true);
        assertTrue((Boolean) hasAnalogOutputSignal.invoke(block, (BlockState) null));

        Method getAnalogOutputSignal = BlockVisBattery.class.getDeclaredMethod("getAnalogOutputSignal", BlockState.class, Level.class, BlockPos.class, Direction.class);
        getAnalogOutputSignal.setAccessible(true);
        assertNotNull(getAnalogOutputSignal);

        // Test math mapping via VisBatteryStorageLogic: ratio * 15 bounded between 0 and 15 with NaN sanitization
        assertEquals(0, thaumcraft.common.tiles.devices.logic.VisBatteryStorageLogic.calculateComparatorSignal(0.0f));
        assertEquals(0, thaumcraft.common.tiles.devices.logic.VisBatteryStorageLogic.calculateComparatorSignal(-0.5f));
        assertEquals(0, thaumcraft.common.tiles.devices.logic.VisBatteryStorageLogic.calculateComparatorSignal(Float.NaN));
        assertEquals(1, thaumcraft.common.tiles.devices.logic.VisBatteryStorageLogic.calculateComparatorSignal(0.1f));
        assertEquals(7, thaumcraft.common.tiles.devices.logic.VisBatteryStorageLogic.calculateComparatorSignal(0.5f));
        assertEquals(14, thaumcraft.common.tiles.devices.logic.VisBatteryStorageLogic.calculateComparatorSignal(0.95f));
        assertEquals(15, thaumcraft.common.tiles.devices.logic.VisBatteryStorageLogic.calculateComparatorSignal(1.0f));
        assertEquals(15, thaumcraft.common.tiles.devices.logic.VisBatteryStorageLogic.calculateComparatorSignal(1.5f));
    }

    private static sun.misc.Unsafe getUnsafe() throws Exception {
        Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (sun.misc.Unsafe) f.get(null);
    }
}
