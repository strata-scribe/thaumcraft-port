package thaumcraft.common.blocks.devices;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import static org.junit.jupiter.api.Assertions.*;

public class BlockBellowsTest {
    @Test
    public void testBlockBellowsStructure() throws Exception {
        // Use reflection with initialize=false to prevent Block.box() from executing and causing a Bootstrap exception
        Class<?> clazz = Class.forName("thaumcraft.common.blocks.devices.BlockBellows", false, getClass().getClassLoader());

        // 1. Extends BaseEntityBlock
        assertTrue(BaseEntityBlock.class.isAssignableFrom(clazz), "BlockBellows should extend BaseEntityBlock");

        // 2. CODEC field exists and is of type MapCodec
        Field codecField = clazz.getField("CODEC");
        assertEquals(MapCodec.class, codecField.getType(), "CODEC field should be of type MapCodec");

        // 3. codec() method returns MapCodec
        Method codecMethod = clazz.getDeclaredMethod("codec");
        codecMethod.setAccessible(true);
        assertEquals(MapCodec.class, codecMethod.getReturnType(), "codec() method should return MapCodec");

        // 4. newBlockEntity method returns BellowsBlockEntity
        Method newBlockEntityMethod = clazz.getDeclaredMethod("newBlockEntity", BlockPos.class, BlockState.class);
        assertEquals(thaumcraft.common.tiles.devices.BellowsBlockEntity.class, newBlockEntityMethod.getReturnType(), "newBlockEntity() method should return BellowsBlockEntity");
    }
}
