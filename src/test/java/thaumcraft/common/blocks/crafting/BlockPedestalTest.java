package thaumcraft.common.blocks.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.api.blocks.ThaumcraftBlocks;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.common.blocks.crafting.logic.PedestalInteractionLogic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

public class BlockPedestalTest {

    @Test
    @DisplayName("Class hierarchy: extends BaseEntityBlock and implements IInfusionStabiliserExt")
    public void testClassHierarchy() {
        assertTrue(BaseEntityBlock.class.isAssignableFrom(BlockPedestal.class),
                "BlockPedestal must extend BaseEntityBlock");
        assertTrue(IInfusionStabiliserExt.class.isAssignableFrom(BlockPedestal.class),
                "BlockPedestal must implement IInfusionStabiliserExt");
    }

    @Test
    @DisplayName("Registration: pedestalArcane, pedestalAncient, and pedestalEldritch exist as DeferredBlock<BlockPedestal>")
    public void testRegistration() throws Exception {
        String[] fieldNames = {"pedestalArcane", "pedestalAncient", "pedestalEldritch"};
        for (String fieldName : fieldNames) {
            Field field = ThaumcraftBlocks.class.getField(fieldName);
            assertNotNull(field, "ThaumcraftBlocks." + fieldName + " field must exist");
            assertTrue(Modifier.isPublic(field.getModifiers()));
            assertTrue(Modifier.isStatic(field.getModifiers()));
            assertEquals(DeferredBlock.class, field.getType());
            assertTrue(field.getGenericType().getTypeName().contains("BlockPedestal"),
                    "ThaumcraftBlocks." + fieldName + " should have generic type DeferredBlock<BlockPedestal>");
        }
    }

    @Test
    @DisplayName("Codec: static CODEC field exists and codec() returns it")
    public void testCodec() throws Exception {
        Field codecField = BlockPedestal.class.getField("CODEC");
        assertTrue(Modifier.isPublic(codecField.getModifiers()));
        assertTrue(Modifier.isStatic(codecField.getModifiers()));
        assertTrue(Modifier.isFinal(codecField.getModifiers()));
        assertEquals(MapCodec.class, codecField.getType());

        sun.misc.Unsafe unsafe = getUnsafe();
        BlockPedestal block = (BlockPedestal) unsafe.allocateInstance(BlockPedestal.class);

        Method codecMethod = BlockPedestal.class.getDeclaredMethod("codec");
        codecMethod.setAccessible(true);
        assertEquals(BlockPedestal.CODEC, codecMethod.invoke(block),
                "codec() must return BlockPedestal.CODEC");
    }

    @Test
    @DisplayName("RenderShape and VoxelShape: returns MODEL and valid bounding box")
    public void testRenderShapeAndVoxelShape() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockPedestal block = (BlockPedestal) unsafe.allocateInstance(BlockPedestal.class);

        assertEquals(RenderShape.MODEL, block.getRenderShape(null),
                "RenderShape must be MODEL");

        Method getShapeMethod = BlockPedestal.class.getDeclaredMethod("getShape",
                BlockState.class, BlockGetter.class, BlockPos.class, CollisionContext.class);
        getShapeMethod.setAccessible(true);
        VoxelShape shape = (VoxelShape) getShapeMethod.invoke(block, null, null, null, null);
        assertNotNull(shape, "VoxelShape must not be null");
        assertFalse(shape.isEmpty(), "VoxelShape must not be empty");

        assertEquals(0.0, shape.bounds().minX, 1e-6);
        assertEquals(0.0, shape.bounds().minY, 1e-6);
        assertEquals(0.0, shape.bounds().minZ, 1e-6);
        assertEquals(1.0, shape.bounds().maxX, 1e-6);
        assertEquals(1.0, shape.bounds().maxY, 1e-6);
        assertEquals(1.0, shape.bounds().maxZ, 1e-6);
    }

    @Test
    @DisplayName("newBlockEntity: method exists and returns BlockEntity")
    public void testNewBlockEntity() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockPedestal block = (BlockPedestal) unsafe.allocateInstance(BlockPedestal.class);

        Method m = BlockPedestal.class.getMethod("newBlockEntity", BlockPos.class, BlockState.class);
        assertEquals(BlockEntity.class, m.getReturnType(),
                "newBlockEntity return type must be BlockEntity");

        try {
            block.newBlockEntity(BlockPos.ZERO, null);
            fail("Expected FML/registry loading exception in headless test");
        } catch (Throwable t) {
            assertTrue(t instanceof ExceptionInInitializerError || t instanceof NoClassDefFoundError || t instanceof IllegalStateException,
                    "Expected FML environment exception when instantiating PedestalBlockEntity in headless test");
        }
    }

    @Test
    @DisplayName("Removal methods: onRemove, destroy, and affectNeighborsAfterRemoval are implemented")
    public void testRemovalMethods() throws Exception {
        Method onRemove = BlockPedestal.class.getMethod("onRemove",
                BlockState.class, Level.class, BlockPos.class, BlockState.class, boolean.class);
        assertNotNull(onRemove, "onRemove method must exist on BlockPedestal");

        Method destroy = BlockPedestal.class.getMethod("destroy",
                LevelAccessor.class, BlockPos.class, BlockState.class);
        assertNotNull(destroy, "destroy method must exist on BlockPedestal");

        Method affectNeighbors = BlockPedestal.class.getDeclaredMethod("affectNeighborsAfterRemoval",
                BlockState.class, ServerLevel.class, BlockPos.class, boolean.class);
        assertNotNull(affectNeighbors, "affectNeighborsAfterRemoval method must exist on BlockPedestal");
    }

    @Test
    @DisplayName("Comparator methods: hasAnalogOutputSignal and getAnalogOutputSignal are present and correct")
    public void testComparatorMethods() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockPedestal block = (BlockPedestal) unsafe.allocateInstance(BlockPedestal.class);

        Method hasAnalogOutputSignal = BlockPedestal.class.getDeclaredMethod("hasAnalogOutputSignal", BlockState.class);
        hasAnalogOutputSignal.setAccessible(true);
        assertTrue((Boolean) hasAnalogOutputSignal.invoke(block, (BlockState) null));

        Method getAnalogOutputSignal = BlockPedestal.class.getDeclaredMethod("getAnalogOutputSignal",
                BlockState.class, Level.class, BlockPos.class, Direction.class);
        getAnalogOutputSignal.setAccessible(true);
        assertNotNull(getAnalogOutputSignal);

        assertEquals(15, PedestalInteractionLogic.calculateComparatorSignal(true));
        assertEquals(0, PedestalInteractionLogic.calculateComparatorSignal(false));
    }

    @Test
    @DisplayName("Interaction methods: useItemOn and useWithoutItem exist with correct signatures")
    public void testInteractionMethodSignatures() throws Exception {
        Method useItemOn = BlockPedestal.class.getDeclaredMethod("useItemOn",
                ItemStack.class, BlockState.class, Level.class, BlockPos.class, Player.class, InteractionHand.class, BlockHitResult.class);
        assertTrue(Modifier.isProtected(useItemOn.getModifiers()));
        assertEquals(InteractionResult.class, useItemOn.getReturnType());

        Method useWithoutItem = BlockPedestal.class.getDeclaredMethod("useWithoutItem",
                BlockState.class, Level.class, BlockPos.class, Player.class, BlockHitResult.class);
        assertTrue(Modifier.isProtected(useWithoutItem.getModifiers()));
        assertEquals(InteractionResult.class, useWithoutItem.getReturnType());
    }

    @Test
    @DisplayName("Infusion stabiliser methods: canStabaliseInfusion, getStabilizationAmount, and getSymmetryPenalty")
    public void testStabiliserMethods() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockPedestal block = (BlockPedestal) unsafe.allocateInstance(BlockPedestal.class);

        assertTrue(block.canStabaliseInfusion(null, BlockPos.ZERO));
        assertEquals(0.0f, block.getStabilizationAmount(null, BlockPos.ZERO), 1e-6);
        assertEquals(0.1f, block.getSymmetryPenalty(null, BlockPos.ZERO), 1e-6);

        Method hasSymmetryPenalty = BlockPedestal.class.getMethod("hasSymmetryPenalty",
                Level.class, BlockPos.class, BlockPos.class);
        assertNotNull(hasSymmetryPenalty);
    }

    private static sun.misc.Unsafe getUnsafe() throws Exception {
        Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (sun.misc.Unsafe) f.get(null);
    }
}
