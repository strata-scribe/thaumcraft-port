package thaumcraft.common.blocks.essentia;

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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.common.blocks.essentia.logic.AlembicInteractionLogic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BlockAlembic Contract Tests")
public class BlockAlembicTest {

    @Test
    @DisplayName("BlockAlembic extends BaseEntityBlock")
    void testClassHierarchy() {
        assertTrue(BaseEntityBlock.class.isAssignableFrom(BlockAlembic.class),
                "BlockAlembic must extend BaseEntityBlock");
    }

    @Test
    @DisplayName("CODEC field and codec() method are properly implemented")
    void testCodec() throws Exception {
        Field codecField = BlockAlembic.class.getField("CODEC");
        assertTrue(Modifier.isPublic(codecField.getModifiers()));
        assertTrue(Modifier.isStatic(codecField.getModifiers()));
        assertTrue(Modifier.isFinal(codecField.getModifiers()));
        assertEquals(MapCodec.class, codecField.getType());

        sun.misc.Unsafe unsafe = getUnsafe();
        BlockAlembic block = (BlockAlembic) unsafe.allocateInstance(BlockAlembic.class);

        Method codecMethod = BlockAlembic.class.getDeclaredMethod("codec");
        codecMethod.setAccessible(true);
        assertEquals(BlockAlembic.CODEC, codecMethod.invoke(block),
                "codec() must return BlockAlembic.CODEC");
    }

    @Test
    @DisplayName("getRenderShape returns MODEL")
    void testRenderShape() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockAlembic block = (BlockAlembic) unsafe.allocateInstance(BlockAlembic.class);
        assertEquals(RenderShape.MODEL, block.getRenderShape(null),
                "RenderShape must be MODEL");
    }

    @Test
    @DisplayName("VoxelShape: returns valid 12x16x12 cylindrical bounding box")
    void testVoxelShape() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockAlembic block = (BlockAlembic) unsafe.allocateInstance(BlockAlembic.class);

        Method getShapeMethod = BlockAlembic.class.getDeclaredMethod("getShape",
                BlockState.class, BlockGetter.class, BlockPos.class, CollisionContext.class);
        getShapeMethod.setAccessible(true);
        VoxelShape shape = (VoxelShape) getShapeMethod.invoke(block, null, null, null, null);
        assertNotNull(shape, "VoxelShape must not be null");
        assertFalse(shape.isEmpty(), "VoxelShape must not be empty");

        assertEquals(2.0 / 16.0, shape.bounds().minX, 1e-6);
        assertEquals(0.0, shape.bounds().minY, 1e-6);
        assertEquals(2.0 / 16.0, shape.bounds().minZ, 1e-6);
        assertEquals(14.0 / 16.0, shape.bounds().maxX, 1e-6);
        assertEquals(1.0, shape.bounds().maxY, 1e-6);
        assertEquals(14.0 / 16.0, shape.bounds().maxZ, 1e-6);

        Method getCollisionShapeMethod = BlockAlembic.class.getDeclaredMethod("getCollisionShape",
                BlockState.class, BlockGetter.class, BlockPos.class, CollisionContext.class);
        getCollisionShapeMethod.setAccessible(true);
        VoxelShape collisionShape = (VoxelShape) getCollisionShapeMethod.invoke(block, null, null, null, null);
        assertEquals(shape, collisionShape, "Collision shape must match visual shape");
    }

    @Test
    @DisplayName("newBlockEntity method exists and returns BlockEntity")
    void testNewBlockEntityMethod() throws Exception {
        Method m = BlockAlembic.class.getMethod("newBlockEntity", BlockPos.class, BlockState.class);
        assertEquals(BlockEntity.class, m.getReturnType(),
                "newBlockEntity return type must be BlockEntity");

        sun.misc.Unsafe unsafe = getUnsafe();
        BlockAlembic block = (BlockAlembic) unsafe.allocateInstance(BlockAlembic.class);

        try {
            block.newBlockEntity(BlockPos.ZERO, null);
            fail("Expected FML/registry loading exception in headless test");
        } catch (Throwable t) {
            assertTrue(t instanceof ExceptionInInitializerError || t instanceof NoClassDefFoundError || t instanceof IllegalStateException,
                    "Expected FML environment exception when instantiating AlembicBlockEntity in headless test");
        }
    }

    @Test
    @DisplayName("canSurvive method is declared with correct signature and handles null parameters")
    void testCanSurvive() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockAlembic block = (BlockAlembic) unsafe.allocateInstance(BlockAlembic.class);

        Method canSurviveMethod = BlockAlembic.class.getDeclaredMethod("canSurvive",
                BlockState.class, LevelReader.class, BlockPos.class);
        canSurviveMethod.setAccessible(true);
        assertNotNull(canSurviveMethod);
        assertEquals(boolean.class, canSurviveMethod.getReturnType());

        // Level or pos null -> returns false safely
        assertFalse((Boolean) canSurviveMethod.invoke(block, null, null, null));

        // Invariant: survival delegates to AlembicInteractionLogic
        assertTrue(AlembicInteractionLogic.canSurviveOn("thaumcraft:smelter_basic"));
        assertTrue(AlembicInteractionLogic.canSurviveOn("thaumcraft:alembic"));
        assertFalse(AlembicInteractionLogic.canSurviveOn("minecraft:dirt"));
        assertFalse(AlembicInteractionLogic.canSurviveOn(null));
    }

    @Test
    @DisplayName("Removal methods: onRemove, destroy, and affectNeighborsAfterRemoval are implemented")
    void testRemovalMethods() throws Exception {
        Method onRemove = BlockAlembic.class.getMethod("onRemove",
                BlockState.class, Level.class, BlockPos.class, BlockState.class, boolean.class);
        assertNotNull(onRemove, "onRemove method must exist on BlockAlembic");

        Method destroy = BlockAlembic.class.getMethod("destroy",
                LevelAccessor.class, BlockPos.class, BlockState.class);
        assertNotNull(destroy, "destroy method must exist on BlockAlembic");

        Method affectNeighbors = BlockAlembic.class.getDeclaredMethod("affectNeighborsAfterRemoval",
                BlockState.class, ServerLevel.class, BlockPos.class, boolean.class);
        assertNotNull(affectNeighbors, "affectNeighborsAfterRemoval method must exist on BlockAlembic");
    }

    @Test
    @DisplayName("Comparator methods: hasAnalogOutputSignal and getAnalogOutputSignal are present and correct")
    void testComparatorMethods() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockAlembic block = (BlockAlembic) unsafe.allocateInstance(BlockAlembic.class);

        Method hasAnalogOutputSignal = BlockAlembic.class.getDeclaredMethod("hasAnalogOutputSignal", BlockState.class);
        hasAnalogOutputSignal.setAccessible(true);
        assertTrue((Boolean) hasAnalogOutputSignal.invoke(block, (BlockState) null));

        Method getAnalogOutputSignal = BlockAlembic.class.getDeclaredMethod("getAnalogOutputSignal",
                BlockState.class, Level.class, BlockPos.class, Direction.class);
        getAnalogOutputSignal.setAccessible(true);
        assertNotNull(getAnalogOutputSignal);

        assertEquals(0, AlembicInteractionLogic.calculateComparatorSignal(0, 32));
        assertEquals(1, AlembicInteractionLogic.calculateComparatorSignal(1, 32));
        assertEquals(8, AlembicInteractionLogic.calculateComparatorSignal(16, 32));
        assertEquals(15, AlembicInteractionLogic.calculateComparatorSignal(32, 32));
    }

    @Test
    @DisplayName("Interaction methods: useItemOn and useWithoutItem exist with correct signatures")
    void testInteractionMethodSignatures() throws Exception {
        Method useItemOn = BlockAlembic.class.getDeclaredMethod("useItemOn",
                ItemStack.class, BlockState.class, Level.class, BlockPos.class, Player.class, InteractionHand.class, BlockHitResult.class);
        assertTrue(Modifier.isProtected(useItemOn.getModifiers()));
        assertEquals(InteractionResult.class, useItemOn.getReturnType());

        Method useWithoutItem = BlockAlembic.class.getDeclaredMethod("useWithoutItem",
                BlockState.class, Level.class, BlockPos.class, Player.class, BlockHitResult.class);
        assertTrue(Modifier.isProtected(useWithoutItem.getModifiers()));
        assertEquals(InteractionResult.class, useWithoutItem.getReturnType());
    }

    private static sun.misc.Unsafe getUnsafe() throws Exception {
        Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (sun.misc.Unsafe) f.get(null);
    }
}
