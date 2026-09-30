package thaumcraft.common.blocks.devices;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.junit.jupiter.api.Test;
import thaumcraft.api.blocks.ThaumcraftBlocks;
import thaumcraft.api.items.IRechargable;
import thaumcraft.common.tiles.devices.RechargePedestalBlockEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

public class BlockRechargePedestalTest {

    @Test
    public void testClassHierarchy() {
        assertTrue(BaseEntityBlock.class.isAssignableFrom(BlockRechargePedestal.class),
                "BlockRechargePedestal must extend BaseEntityBlock");
    }

    @Test
    public void testRegistration() throws Exception {
        Field field = ThaumcraftBlocks.class.getField("rechargePedestal");
        assertNotNull(field, "ThaumcraftBlocks.rechargePedestal field must exist");
        assertTrue(Modifier.isPublic(field.getModifiers()));
        assertTrue(Modifier.isStatic(field.getModifiers()));
        assertEquals(DeferredBlock.class, field.getType());
        assertTrue(field.getGenericType().getTypeName().contains("BlockRechargePedestal"),
                "ThaumcraftBlocks.rechargePedestal should have generic type DeferredBlock<BlockRechargePedestal>");
    }

    @Test
    public void testCodec() throws Exception {
        Field codecField = BlockRechargePedestal.class.getField("CODEC");
        assertTrue(Modifier.isPublic(codecField.getModifiers()));
        assertTrue(Modifier.isStatic(codecField.getModifiers()));
        assertTrue(Modifier.isFinal(codecField.getModifiers()));
        assertEquals(MapCodec.class, codecField.getType());

        sun.misc.Unsafe unsafe = getUnsafe();
        BlockRechargePedestal block = (BlockRechargePedestal) unsafe.allocateInstance(BlockRechargePedestal.class);

        Method codecMethod = BlockRechargePedestal.class.getDeclaredMethod("codec");
        codecMethod.setAccessible(true);
        assertEquals(BlockRechargePedestal.CODEC, codecMethod.invoke(block),
                "codec() must return BlockRechargePedestal.CODEC");
    }

    @Test
    public void testRenderShapeAndVoxelShape() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockRechargePedestal block = (BlockRechargePedestal) unsafe.allocateInstance(BlockRechargePedestal.class);
        assertEquals(RenderShape.MODEL, block.getRenderShape(null),
                "RenderShape must be MODEL");

        Method getShapeMethod = BlockRechargePedestal.class.getDeclaredMethod("getShape",
                BlockState.class, net.minecraft.world.level.BlockGetter.class, BlockPos.class, net.minecraft.world.phys.shapes.CollisionContext.class);
        getShapeMethod.setAccessible(true);
        VoxelShape shape = (VoxelShape) getShapeMethod.invoke(block, null, null, null, null);
        assertNotNull(shape, "VoxelShape must not be null");
        assertFalse(shape.isEmpty(), "VoxelShape must not be empty");
        // Verify full unit bounds
        assertEquals(0.0, shape.bounds().minX, 1e-6);
        assertEquals(0.0, shape.bounds().minY, 1e-6);
        assertEquals(0.0, shape.bounds().minZ, 1e-6);
        assertEquals(1.0, shape.bounds().maxX, 1e-6);
        assertEquals(1.0, shape.bounds().maxY, 1e-6);
        assertEquals(1.0, shape.bounds().maxZ, 1e-6);
    }

    @Test
    public void testNewBlockEntity() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockRechargePedestal block = (BlockRechargePedestal) unsafe.allocateInstance(BlockRechargePedestal.class);

        Method m = BlockRechargePedestal.class.getMethod("newBlockEntity", BlockPos.class, BlockState.class);
        assertEquals(BlockEntity.class, m.getReturnType(),
                "newBlockEntity return type must be BlockEntity");

        try {
            block.newBlockEntity(BlockPos.ZERO, null);
            fail("Expected FML/registry loading exception in headless test");
        } catch (Throwable t) {
            assertTrue(t instanceof ExceptionInInitializerError || t instanceof NoClassDefFoundError || t instanceof IllegalStateException,
                    "Expected FML environment exception when instantiating RechargePedestalBlockEntity in headless test");
        }
    }

    @Test
    public void testTickerHelperWiring() throws Exception {
        Method getTickerMethod = BlockRechargePedestal.class.getMethod("getTicker", Level.class, BlockState.class, BlockEntityType.class);
        assertNotNull(getTickerMethod, "getTicker method must exist");
        assertEquals(net.minecraft.world.level.block.entity.BlockEntityTicker.class, getTickerMethod.getReturnType());

        Method serverTickMethod = RechargePedestalBlockEntity.class.getMethod("serverTick", Level.class, BlockPos.class, BlockState.class, RechargePedestalBlockEntity.class);
        assertTrue(Modifier.isStatic(serverTickMethod.getModifiers()));
    }

    @Test
    public void testInteractionMethodSignatures() throws Exception {
        Method useItemOn = BlockRechargePedestal.class.getDeclaredMethod("useItemOn",
                ItemStack.class, BlockState.class, Level.class, BlockPos.class, Player.class, InteractionHand.class, BlockHitResult.class);
        assertTrue(Modifier.isProtected(useItemOn.getModifiers()));
        assertEquals(InteractionResult.class, useItemOn.getReturnType());

        Method useWithoutItem = BlockRechargePedestal.class.getDeclaredMethod("useWithoutItem",
                BlockState.class, Level.class, BlockPos.class, Player.class, BlockHitResult.class);
        assertTrue(Modifier.isProtected(useWithoutItem.getModifiers()));
        assertEquals(InteractionResult.class, useWithoutItem.getReturnType());
    }

    @Test
    public void testInteractionGuardsAndDeliverySafety() {
        // Null pedestal / null stack guard checks
        assertFalse(BlockRechargePedestal.insertItem(null, null, null, null));
        assertFalse(BlockRechargePedestal.extractItem(null, null, null, null));

        // Delivery destination safety guard: player is null AND world location is null
        // Even if extraction was attempted, item must not be deleted if it cannot be delivered
        assertFalse(thaumcraft.common.blocks.devices.logic.RechargePedestalInteractionLogic.canDeliverExtractedItem(false, false));
    }

    @Test
    public void testComparatorMethods() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockRechargePedestal block = (BlockRechargePedestal) unsafe.allocateInstance(BlockRechargePedestal.class);

        Method hasAnalogOutputSignal = BlockRechargePedestal.class.getDeclaredMethod("hasAnalogOutputSignal", BlockState.class);
        hasAnalogOutputSignal.setAccessible(true);
        assertTrue((Boolean) hasAnalogOutputSignal.invoke(block, (BlockState) null));

        Method getAnalogOutputSignal = BlockRechargePedestal.class.getDeclaredMethod("getAnalogOutputSignal",
                BlockState.class, Level.class, BlockPos.class, Direction.class);
        getAnalogOutputSignal.setAccessible(true);
        assertNotNull(getAnalogOutputSignal);
    }

    private static sun.misc.Unsafe getUnsafe() throws Exception {
        Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (sun.misc.Unsafe) f.get(null);
    }
}
