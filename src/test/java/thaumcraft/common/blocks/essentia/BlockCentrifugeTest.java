package thaumcraft.common.blocks.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BlockCentrifugeTest {

    @Test
    public void testClassStructureAndConstants() throws Exception {
        // Test that BlockCentrifuge has the correct structure without instantiating it,
        // because instantiating blocks requires Minecraft Bootstrap which breaks the test runner
        // or causes issues with NeoForge mappings in a raw JUnit 5 environment.

        Class<?> clazz = BlockCentrifuge.class;

        // Verify it extends BaseEntityBlock
        assertTrue(BaseEntityBlock.class.isAssignableFrom(clazz));

        // Verify CODEC field exists
        Field codecField = clazz.getDeclaredField("CODEC");
        assertNotNull(codecField);

        // Verify codec method exists
        Method codecMethod = clazz.getDeclaredMethod("codec");
        assertNotNull(codecMethod);

        // Verify newBlockEntity method exists
        Method newBlockEntityMethod = clazz.getDeclaredMethod("newBlockEntity", BlockPos.class, BlockState.class);
        assertNotNull(newBlockEntityMethod);
    }
}
