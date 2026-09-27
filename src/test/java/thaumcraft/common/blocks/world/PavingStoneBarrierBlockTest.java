package thaumcraft.common.blocks.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PavingStoneBarrierBlockTest {

    @Test
    void testBlockStructureAndConstants() throws Exception {
        // Since instantiating blocks requires Minecraft Bootstrap which breaks the test runner,
        // and passing null properties to Block constructor throws NPE,
        // we test the structure of the block exactly like in BlockVisCrystalTest.

        Class<?> clazz = PavingStoneBarrierBlock.class;

        // Verify it extends Block
        assertTrue(Block.class.isAssignableFrom(clazz));

        // Verify getCollisionShape method is overridden
        java.lang.reflect.Method getCollisionShapeMethod = clazz.getDeclaredMethod("getCollisionShape", BlockState.class, BlockGetter.class, BlockPos.class, CollisionContext.class);
        assertNotNull(getCollisionShapeMethod);
        assertEquals(VoxelShape.class, getCollisionShapeMethod.getReturnType());

        // Verify BARRIER_SHAPE field exists
        Field barrierShapeField = clazz.getDeclaredField("BARRIER_SHAPE");
        assertNotNull(barrierShapeField);
        assertTrue(java.lang.reflect.Modifier.isStatic(barrierShapeField.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isProtected(barrierShapeField.getModifiers()));
    }
}
