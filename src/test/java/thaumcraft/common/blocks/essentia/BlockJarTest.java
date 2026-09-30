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
import thaumcraft.common.blocks.essentia.logic.JarInteractionLogic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BlockJar Contract Tests")
public class BlockJarTest {

    @Test
    @DisplayName("BlockJar extends BaseEntityBlock")
    void testClassHierarchy() {
        assertTrue(BaseEntityBlock.class.isAssignableFrom(BlockJar.class),
                "BlockJar must extend BaseEntityBlock");
    }

    @Test
    @DisplayName("Registrations: jarNormal and jarVoid exist as DeferredBlock<BlockJar>")
    void testRegistrations() throws Exception {
        Field normalField = ThaumcraftBlocks.class.getField("jarNormal");
        assertNotNull(normalField, "ThaumcraftBlocks.jarNormal field must exist");
        assertTrue(Modifier.isPublic(normalField.getModifiers()));
        assertTrue(Modifier.isStatic(normalField.getModifiers()));
        assertEquals(DeferredBlock.class, normalField.getType());
        assertTrue(normalField.getGenericType().getTypeName().contains("BlockJar"));

        Field voidField = ThaumcraftBlocks.class.getField("jarVoid");
        assertNotNull(voidField, "ThaumcraftBlocks.jarVoid field must exist");
        assertTrue(Modifier.isPublic(voidField.getModifiers()));
        assertTrue(Modifier.isStatic(voidField.getModifiers()));
        assertEquals(DeferredBlock.class, voidField.getType());
        assertTrue(voidField.getGenericType().getTypeName().contains("BlockJar"));
    }

    @Test
    @DisplayName("CODEC field and codec() method are properly implemented")
    void testCodec() throws Exception {
        Field codecField = BlockJar.class.getField("CODEC");
        assertTrue(Modifier.isPublic(codecField.getModifiers()));
        assertTrue(Modifier.isStatic(codecField.getModifiers()));
        assertTrue(Modifier.isFinal(codecField.getModifiers()));
        assertEquals(MapCodec.class, codecField.getType());

        sun.misc.Unsafe unsafe = getUnsafe();
        BlockJar block = (BlockJar) unsafe.allocateInstance(BlockJar.class);

        Method codecMethod = BlockJar.class.getDeclaredMethod("codec");
        codecMethod.setAccessible(true);
        assertEquals(BlockJar.CODEC, codecMethod.invoke(block),
                "codec() must return BlockJar.CODEC");
    }

    @Test
    @DisplayName("getRenderShape returns MODEL")
    void testRenderShape() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockJar block = (BlockJar) unsafe.allocateInstance(BlockJar.class);
        assertEquals(RenderShape.MODEL, block.getRenderShape(null),
                "RenderShape must be MODEL");
    }

    @Test
    @DisplayName("VoxelShape: returns composite shape matching body, neck, and lid dimensions")
    void testVoxelShape() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockJar block = (BlockJar) unsafe.allocateInstance(BlockJar.class);

        Method getShapeMethod = BlockJar.class.getDeclaredMethod("getShape",
                BlockState.class, BlockGetter.class, BlockPos.class, CollisionContext.class);
        getShapeMethod.setAccessible(true);
        VoxelShape shape = (VoxelShape) getShapeMethod.invoke(block, null, null, null, null);
        assertNotNull(shape, "VoxelShape must not be null");
        assertFalse(shape.isEmpty(), "VoxelShape must not be empty");

        // Body: 3..13 x, 0..12 y, 3..13 z; Neck: 5..11 x, 12..14 y, 5..11 z; Lid: 4..12 x, 14..16 y, 4..12 z
        assertEquals(3.0 / 16.0, shape.bounds().minX, 1e-6);
        assertEquals(0.0, shape.bounds().minY, 1e-6);
        assertEquals(3.0 / 16.0, shape.bounds().minZ, 1e-6);
        assertEquals(13.0 / 16.0, shape.bounds().maxX, 1e-6);
        assertEquals(1.0, shape.bounds().maxY, 1e-6);
        assertEquals(13.0 / 16.0, shape.bounds().maxZ, 1e-6);

        Method getCollisionShapeMethod = BlockJar.class.getDeclaredMethod("getCollisionShape",
                BlockState.class, BlockGetter.class, BlockPos.class, CollisionContext.class);
        getCollisionShapeMethod.setAccessible(true);
        VoxelShape collisionShape = (VoxelShape) getCollisionShapeMethod.invoke(block, null, null, null, null);
        assertEquals(shape, collisionShape, "Collision shape must match visual shape");
    }

    @Test
    @DisplayName("newBlockEntity method exists and returns BlockEntity")
    void testNewBlockEntityMethod() throws Exception {
        Method m = BlockJar.class.getMethod("newBlockEntity", BlockPos.class, BlockState.class);
        assertEquals(BlockEntity.class, m.getReturnType(),
                "newBlockEntity return type must be BlockEntity");

        sun.misc.Unsafe unsafe = getUnsafe();
        BlockJar block = (BlockJar) unsafe.allocateInstance(BlockJar.class);

        try {
            block.newBlockEntity(BlockPos.ZERO, null);
            fail("Expected FML/registry loading exception in headless test");
        } catch (Throwable t) {
            assertTrue(t instanceof ExceptionInInitializerError || t instanceof NoClassDefFoundError || t instanceof IllegalStateException,
                    "Expected FML environment exception when instantiating JarBlockEntity in headless test");
        }
    }

    @Test
    @DisplayName("Removal methods: onRemove, destroy, and affectNeighborsAfterRemoval are implemented")
    void testRemovalMethods() throws Exception {
        Method onRemove = BlockJar.class.getMethod("onRemove",
                BlockState.class, Level.class, BlockPos.class, BlockState.class, boolean.class);
        assertNotNull(onRemove, "onRemove method must exist on BlockJar");

        Method destroy = BlockJar.class.getMethod("destroy",
                LevelAccessor.class, BlockPos.class, BlockState.class);
        assertNotNull(destroy, "destroy method must exist on BlockJar");

        Method affectNeighbors = BlockJar.class.getDeclaredMethod("affectNeighborsAfterRemoval",
                BlockState.class, ServerLevel.class, BlockPos.class, boolean.class);
        assertNotNull(affectNeighbors, "affectNeighborsAfterRemoval method must exist on BlockJar");
    }

    @Test
    @DisplayName("Comparator methods: hasAnalogOutputSignal and getAnalogOutputSignal are present and correct")
    void testComparatorMethods() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockJar block = (BlockJar) unsafe.allocateInstance(BlockJar.class);

        Method hasAnalogOutputSignal = BlockJar.class.getDeclaredMethod("hasAnalogOutputSignal", BlockState.class);
        hasAnalogOutputSignal.setAccessible(true);
        assertTrue((Boolean) hasAnalogOutputSignal.invoke(block, (BlockState) null));

        Method getAnalogOutputSignal = BlockJar.class.getDeclaredMethod("getAnalogOutputSignal",
                BlockState.class, Level.class, BlockPos.class, Direction.class);
        getAnalogOutputSignal.setAccessible(true);
        assertNotNull(getAnalogOutputSignal);

        // Comparator math delegates to JarInteractionLogic
        assertEquals(0, JarInteractionLogic.calculateComparatorSignal(0, 250));
        assertEquals(0, JarInteractionLogic.calculateComparatorSignal(1, 250));
        assertEquals(7, JarInteractionLogic.calculateComparatorSignal(125, 250));
        assertEquals(15, JarInteractionLogic.calculateComparatorSignal(250, 250));
    }

    @Test
    @DisplayName("Interaction methods: useItemOn and useWithoutItem exist with correct signatures")
    void testInteractionMethodSignatures() throws Exception {
        Method useItemOn = BlockJar.class.getDeclaredMethod("useItemOn",
                ItemStack.class, BlockState.class, Level.class, BlockPos.class, Player.class, InteractionHand.class, BlockHitResult.class);
        assertTrue(Modifier.isProtected(useItemOn.getModifiers()));
        assertEquals(InteractionResult.class, useItemOn.getReturnType());

        Method useWithoutItem = BlockJar.class.getDeclaredMethod("useWithoutItem",
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
