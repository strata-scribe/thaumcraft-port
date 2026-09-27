package thaumcraft.common.golems.seals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.Registries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.IGolemProperties;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.tasks.TaskHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;

import static org.junit.jupiter.api.Assertions.*;

public class SealFillTest {

    private SealFill seal;

    @BeforeEach
    public void setup() {
        seal = new SealFill();
        TaskHandler.reset(); // clear tasks before tests
    }

    @Test
    public void testGetKey() {
        assertEquals("thaumcraft:fill", seal.getKey());
    }

    @Test
    public void testGetSealIcon() {
        Identifier icon = seal.getSealIcon();
        assertNotNull(icon);
        assertEquals("thaumcraft", icon.getNamespace());
        assertEquals("items/seals/seal_fill", icon.getPath());
    }

    @Test
    public void testOnTaskCompletion() {
        final boolean[] swingArmCalled = {false};
        final boolean[] addRankXpCalled = {false};

        IGolemAPI mockGolem = new IGolemAPI() {
            public LivingEntity getGolemEntity() { return null; }
            public IGolemProperties getProperties() { return null; }
            public void setProperties(IGolemProperties prop) {}
            public Level getGolemWorld() { return null; }
            public ItemStack holdItem(ItemStack stack) { return null; }
            public ItemStack dropItem(ItemStack stack) { return null; }
            public boolean canCarry(ItemStack stack, boolean partial) { return true; }
            public int canCarryAmount(ItemStack stack) { return 0; }
            public boolean isCarrying(ItemStack stack) { return false; }
            public NonNullList<ItemStack> getCarrying() { return null; }
            public void addRankXp(int xp) { if(xp == 1) addRankXpCalled[0] = true; }
            public byte getGolemColor() { return 0; }
            public void swingArm() { swingArmCalled[0] = true; }
            public boolean isInCombat() { return false; }
        };

        Task mockTask = new Task(new SealPos(new BlockPos(0, 0, 0), Direction.UP), new BlockPos(0, 0, 0));

        boolean result = seal.onTaskCompletion(null, mockGolem, mockTask);

        assertTrue(result);
        assertTrue(addRankXpCalled[0]);
        assertTrue(swingArmCalled[0]);
        assertTrue(mockTask.isSuspended());
    }
}
