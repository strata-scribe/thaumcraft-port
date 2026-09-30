package thaumcraft.common.blocks.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.api.blocks.ThaumcraftBlocks;
import thaumcraft.common.blocks.crafting.logic.InfusionMatrixLogic;
import thaumcraft.common.tiles.crafting.InfusionMatrixBlockEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

public class BlockInfusionMatrixTest {

    @Test
    @DisplayName("Class hierarchy: extends BaseEntityBlock")
    public void testClassHierarchy() {
        assertTrue(BaseEntityBlock.class.isAssignableFrom(BlockInfusionMatrix.class),
                "BlockInfusionMatrix must extend BaseEntityBlock");
    }

    @Test
    @DisplayName("Registration: infusionMatrix exists as DeferredBlock<BlockInfusionMatrix>")
    public void testRegistration() throws Exception {
        Field field = ThaumcraftBlocks.class.getField("infusionMatrix");
        assertNotNull(field, "ThaumcraftBlocks.infusionMatrix field must exist");
        assertTrue(Modifier.isPublic(field.getModifiers()));
        assertTrue(Modifier.isStatic(field.getModifiers()));
        assertEquals(DeferredBlock.class, field.getType());
        assertTrue(field.getGenericType().getTypeName().contains("BlockInfusionMatrix"),
                "ThaumcraftBlocks.infusionMatrix should have generic type DeferredBlock<BlockInfusionMatrix>");
    }

    @Test
    @DisplayName("Codec: static CODEC field exists and codec() returns it")
    public void testCodec() throws Exception {
        Field codecField = BlockInfusionMatrix.class.getField("CODEC");
        assertTrue(Modifier.isPublic(codecField.getModifiers()));
        assertTrue(Modifier.isStatic(codecField.getModifiers()));
        assertTrue(Modifier.isFinal(codecField.getModifiers()));
        assertEquals(MapCodec.class, codecField.getType());

        sun.misc.Unsafe unsafe = getUnsafe();
        BlockInfusionMatrix block = (BlockInfusionMatrix) unsafe.allocateInstance(BlockInfusionMatrix.class);

        Method codecMethod = BlockInfusionMatrix.class.getDeclaredMethod("codec");
        codecMethod.setAccessible(true);
        assertEquals(BlockInfusionMatrix.CODEC, codecMethod.invoke(block),
                "codec() must return BlockInfusionMatrix.CODEC");
    }

    @Test
    @DisplayName("RenderShape and VoxelShape: returns MODEL and 10px centered cube")
    public void testRenderShapeAndVoxelShape() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockInfusionMatrix block = (BlockInfusionMatrix) unsafe.allocateInstance(BlockInfusionMatrix.class);

        assertEquals(RenderShape.MODEL, block.getRenderShape(null),
                "RenderShape must be MODEL");

        Method getShapeMethod = BlockInfusionMatrix.class.getDeclaredMethod("getShape",
                BlockState.class, BlockGetter.class, BlockPos.class, CollisionContext.class);
        getShapeMethod.setAccessible(true);
        VoxelShape shape = (VoxelShape) getShapeMethod.invoke(block, null, null, null, null);
        assertNotNull(shape, "VoxelShape must not be null");
        assertFalse(shape.isEmpty(), "VoxelShape must not be empty");

        // 3..13 on 16-grid -> 3/16 = 0.1875, 13/16 = 0.8125
        assertEquals(3.0 / 16.0, shape.bounds().minX, 1e-6);
        assertEquals(3.0 / 16.0, shape.bounds().minY, 1e-6);
        assertEquals(3.0 / 16.0, shape.bounds().minZ, 1e-6);
        assertEquals(13.0 / 16.0, shape.bounds().maxX, 1e-6);
        assertEquals(13.0 / 16.0, shape.bounds().maxY, 1e-6);
        assertEquals(13.0 / 16.0, shape.bounds().maxZ, 1e-6);
    }

    @Test
    @DisplayName("newBlockEntity: method exists and returns BlockEntity")
    public void testNewBlockEntity() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockInfusionMatrix block = (BlockInfusionMatrix) unsafe.allocateInstance(BlockInfusionMatrix.class);

        Method m = BlockInfusionMatrix.class.getMethod("newBlockEntity", BlockPos.class, BlockState.class);
        assertEquals(BlockEntity.class, m.getReturnType(),
                "newBlockEntity return type must be BlockEntity");

        try {
            block.newBlockEntity(BlockPos.ZERO, null);
            fail("Expected FML/registry loading exception in headless test");
        } catch (Throwable t) {
            assertTrue(t instanceof ExceptionInInitializerError || t instanceof NoClassDefFoundError || t instanceof IllegalStateException,
                    "Expected FML environment exception when instantiating InfusionMatrixBlockEntity in headless test");
        }
    }

    @Test
    @DisplayName("Ticker wiring: getTicker exists and returns BlockEntityTicker")
    public void testTickerHelperWiring() throws Exception {
        Method getTickerMethod = BlockInfusionMatrix.class.getMethod("getTicker", Level.class, BlockState.class, BlockEntityType.class);
        assertNotNull(getTickerMethod, "getTicker method must exist");
        assertEquals(BlockEntityTicker.class, getTickerMethod.getReturnType());

        Method tickMethod = InfusionMatrixBlockEntity.class.getMethod("tick",
                Level.class, BlockPos.class, BlockState.class, InfusionMatrixBlockEntity.class);
        assertTrue(Modifier.isStatic(tickMethod.getModifiers()));
    }

    @Test
    @DisplayName("Comparator methods: hasAnalogOutputSignal and getAnalogOutputSignal are present and correct")
    public void testComparatorMethods() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockInfusionMatrix block = (BlockInfusionMatrix) unsafe.allocateInstance(BlockInfusionMatrix.class);

        Method hasAnalogOutputSignal = BlockInfusionMatrix.class.getDeclaredMethod("hasAnalogOutputSignal", BlockState.class);
        hasAnalogOutputSignal.setAccessible(true);
        assertTrue((Boolean) hasAnalogOutputSignal.invoke(block, (BlockState) null));

        Method getAnalogOutputSignal = BlockInfusionMatrix.class.getDeclaredMethod("getAnalogOutputSignal",
                BlockState.class, Level.class, BlockPos.class, Direction.class);
        getAnalogOutputSignal.setAccessible(true);
        assertNotNull(getAnalogOutputSignal);

        assertEquals(15, InfusionMatrixLogic.calculateComparatorSignal(true));
        assertEquals(0, InfusionMatrixLogic.calculateComparatorSignal(false));
    }

    @Test
    @DisplayName("Interaction methods: useItemOn and useWithoutItem exist with correct signatures")
    public void testInteractionMethodSignatures() throws Exception {
        Method useItemOn = BlockInfusionMatrix.class.getDeclaredMethod("useItemOn",
                ItemStack.class, BlockState.class, Level.class, BlockPos.class, Player.class, InteractionHand.class, BlockHitResult.class);
        assertTrue(Modifier.isProtected(useItemOn.getModifiers()));
        assertEquals(InteractionResult.class, useItemOn.getReturnType());

        Method useWithoutItem = BlockInfusionMatrix.class.getDeclaredMethod("useWithoutItem",
                BlockState.class, Level.class, BlockPos.class, Player.class, BlockHitResult.class);
        assertTrue(Modifier.isProtected(useWithoutItem.getModifiers()));
        assertEquals(InteractionResult.class, useWithoutItem.getReturnType());
    }

    @Test
    @DisplayName("InfusionMatrixBlockEntity: isCrafting getter exists and returns boolean")
    public void testIsCraftingGetter() throws Exception {
        Method isCrafting = InfusionMatrixBlockEntity.class.getMethod("isCrafting");
        assertNotNull(isCrafting, "isCrafting method must exist on InfusionMatrixBlockEntity");
        assertEquals(boolean.class, isCrafting.getReturnType());
    }

    private static sun.misc.Unsafe getUnsafe() throws Exception {
        Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (sun.misc.Unsafe) f.get(null);
    }
}
