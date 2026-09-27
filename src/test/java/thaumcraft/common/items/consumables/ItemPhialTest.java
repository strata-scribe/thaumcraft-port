package thaumcraft.common.items.consumables;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Abilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import static org.junit.jupiter.api.Assertions.*;

public class ItemPhialTest {

    @BeforeEach
    public void setup() {
        Aspect.aspects.put(Aspect.FIRE.getTag(), Aspect.FIRE);
    }

    @Test
    public void testAspectStorageLogic() {
        Aspect.aspects.put(Aspect.FIRE.getTag(), Aspect.FIRE);
        AspectList aspects = new AspectList().add(Aspect.FIRE, 10);

        CompoundTag tag = new CompoundTag();
        aspects.writeToNBT(tag);

        AspectList storedAspects = new AspectList();
        storedAspects.readFromNBT(tag);

        assertNotNull(storedAspects);
        assertEquals(1, storedAspects.size());
        assertEquals(10, storedAspects.getAmount(Aspect.FIRE));
    }
}
