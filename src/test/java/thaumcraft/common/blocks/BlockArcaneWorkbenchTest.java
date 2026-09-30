package thaumcraft.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.api.blocks.ThaumcraftBlocks;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Headless unit tests for {@link BlockArcaneWorkbench}.
 */
public class BlockArcaneWorkbenchTest {

    @Test
    @DisplayName("BlockArcaneWorkbench extends BaseEntityBlock")
    public void testClassHierarchy() {
        assertTrue(BaseEntityBlock.class.isAssignableFrom(BlockArcaneWorkbench.class),
                "BlockArcaneWorkbench must extend BaseEntityBlock");
    }

    @Test
    @DisplayName("ThaumcraftBlocks registers BlockArcaneWorkbench as DeferredBlock")
    public void testRegistration() throws Exception {
        Field field = ThaumcraftBlocks.class.getField("arcaneWorkbench");
        assertNotNull(field, "ThaumcraftBlocks.arcaneWorkbench field must exist");
        assertTrue(Modifier.isPublic(field.getModifiers()));
        assertTrue(Modifier.isStatic(field.getModifiers()));
        assertEquals(DeferredBlock.class, field.getType());
        assertTrue(field.getGenericType().getTypeName().contains("BlockArcaneWorkbench"),
                "ThaumcraftBlocks.arcaneWorkbench should have generic type DeferredBlock<BlockArcaneWorkbench>");
    }

    @Test
    @DisplayName("CODEC field and codec() method are properly wired")
    public void testCodec() throws Exception {
        Field codecField = BlockArcaneWorkbench.class.getField("CODEC");
        assertTrue(Modifier.isPublic(codecField.getModifiers()));
        assertTrue(Modifier.isStatic(codecField.getModifiers()));
        assertTrue(Modifier.isFinal(codecField.getModifiers()));
        assertEquals(MapCodec.class, codecField.getType());

        sun.misc.Unsafe unsafe = getUnsafe();
        BlockArcaneWorkbench block = (BlockArcaneWorkbench) unsafe.allocateInstance(BlockArcaneWorkbench.class);

        Method codecMethod = BlockArcaneWorkbench.class.getDeclaredMethod("codec");
        codecMethod.setAccessible(true);
        assertEquals(BlockArcaneWorkbench.CODEC, codecMethod.invoke(block),
                "codec() must return BlockArcaneWorkbench.CODEC");
    }

    @Test
    @DisplayName("RenderShape is MODEL")
    public void testRenderShape() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockArcaneWorkbench block = (BlockArcaneWorkbench) unsafe.allocateInstance(BlockArcaneWorkbench.class);
        assertEquals(RenderShape.MODEL, block.getRenderShape(null),
                "RenderShape must be MODEL");
    }

    @Test
    @DisplayName("newBlockEntity returns BlockEntity")
    public void testNewBlockEntityMethod() throws Exception {
        Method m = BlockArcaneWorkbench.class.getMethod("newBlockEntity", BlockPos.class, BlockState.class);
        assertEquals(BlockEntity.class, m.getReturnType(),
                "newBlockEntity return type must be BlockEntity");
    }

    @Test
    @DisplayName("Comparator output signal is enabled")
    public void testComparatorSignalSupport() throws Exception {
        sun.misc.Unsafe unsafe = getUnsafe();
        BlockArcaneWorkbench block = (BlockArcaneWorkbench) unsafe.allocateInstance(BlockArcaneWorkbench.class);

        Method hasAnalogOutputSignal = BlockArcaneWorkbench.class.getDeclaredMethod("hasAnalogOutputSignal", BlockState.class);
        hasAnalogOutputSignal.setAccessible(true);
        assertTrue((Boolean) hasAnalogOutputSignal.invoke(block, (BlockState) null));

        Method getAnalogOutputSignal = BlockArcaneWorkbench.class.getDeclaredMethod("getAnalogOutputSignal",
                BlockState.class, Level.class, BlockPos.class, Direction.class);
        assertNotNull(getAnalogOutputSignal);
    }

    @Test
    @DisplayName("onRemove, destroy, and affectNeighborsAfterRemoval methods are implemented")
    public void testRemovalMethods() throws Exception {
        Method onRemove = BlockArcaneWorkbench.class.getMethod("onRemove",
                BlockState.class, Level.class, BlockPos.class, BlockState.class, boolean.class);
        assertNotNull(onRemove, "onRemove method must exist on BlockArcaneWorkbench");

        Method destroy = BlockArcaneWorkbench.class.getMethod("destroy",
                LevelAccessor.class, BlockPos.class, BlockState.class);
        assertNotNull(destroy, "destroy method must exist on BlockArcaneWorkbench");

        Method affectNeighbors = BlockArcaneWorkbench.class.getDeclaredMethod("affectNeighborsAfterRemoval",
                BlockState.class, ServerLevel.class, BlockPos.class, boolean.class);
        assertNotNull(affectNeighbors, "affectNeighborsAfterRemoval method must exist on BlockArcaneWorkbench");
    }

    @Test
    @DisplayName("Interaction methods useItemOn and useWithoutItem exist")
    public void testInteractionMethods() throws Exception {
        Method useItemOn = BlockArcaneWorkbench.class.getDeclaredMethod("useItemOn",
                ItemStack.class, BlockState.class, Level.class, BlockPos.class,
                Player.class, InteractionHand.class, BlockHitResult.class);
        assertNotNull(useItemOn, "useItemOn method must exist");

        Method useWithoutItem = BlockArcaneWorkbench.class.getDeclaredMethod("useWithoutItem",
                BlockState.class, Level.class, BlockPos.class, Player.class, BlockHitResult.class);
        assertNotNull(useWithoutItem, "useWithoutItem method must exist");
    }

    private static sun.misc.Unsafe getUnsafe() throws Exception {
        Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (sun.misc.Unsafe) f.get(null);
    }
}
