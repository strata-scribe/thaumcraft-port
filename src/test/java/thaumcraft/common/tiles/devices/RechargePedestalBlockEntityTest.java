package thaumcraft.common.tiles.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

public class RechargePedestalBlockEntityTest {

    @Test
    public void testClassHierarchyAndConstructors() throws Exception {
        Class<?> clazz = RechargePedestalBlockEntity.class;

        // Check superclass is BlockEntity
        assertEquals(BlockEntity.class, clazz.getSuperclass(), "RechargePedestalBlockEntity must extend BlockEntity");

        // Check primary constructor (BlockPos, BlockState)
        Constructor<?> primaryCtor = clazz.getDeclaredConstructor(BlockPos.class, BlockState.class);
        assertNotNull(primaryCtor, "Primary constructor (BlockPos, BlockState) must exist");
        assertTrue(Modifier.isPublic(primaryCtor.getModifiers()), "Primary constructor must be public");

        // Check overloaded constructor (BlockPos, BlockState, int)
        Constructor<?> overloadedCtor = clazz.getDeclaredConstructor(BlockPos.class, BlockState.class, int.class);
        assertNotNull(overloadedCtor, "Overloaded constructor (BlockPos, BlockState, int) must exist");
        assertTrue(Modifier.isPublic(overloadedCtor.getModifiers()), "Overloaded constructor must be public");
    }

    @Test
    public void testMethodSignatures() throws Exception {
        Class<?> clazz = RechargePedestalBlockEntity.class;

        Method getItem = clazz.getDeclaredMethod("getItem");
        assertEquals(ItemStack.class, getItem.getReturnType());

        Method setItem = clazz.getDeclaredMethod("setItem", ItemStack.class);
        assertEquals(void.class, setItem.getReturnType());

        Method hasItem = clazz.getDeclaredMethod("hasItem");
        assertEquals(boolean.class, hasItem.getReturnType());

        Method getBaseTransferRate = clazz.getDeclaredMethod("getBaseTransferRate");
        assertEquals(int.class, getBaseTransferRate.getReturnType());

        Method tickRecharge = clazz.getDeclaredMethod("tickRecharge", Level.class, BlockPos.class);
        assertEquals(void.class, tickRecharge.getReturnType());

        Method serverTick = clazz.getDeclaredMethod("serverTick", Level.class, BlockPos.class, BlockState.class, RechargePedestalBlockEntity.class);
        assertTrue(Modifier.isStatic(serverTick.getModifiers()));

        Method saveAdditional = clazz.getDeclaredMethod("saveAdditional", ValueOutput.class);
        assertTrue(Modifier.isProtected(saveAdditional.getModifiers()));

        Method loadAdditional = clazz.getDeclaredMethod("loadAdditional", ValueInput.class);
        assertTrue(Modifier.isProtected(loadAdditional.getModifiers()));
    }

    @Test
    public void testHeadlessInstantiationThrowsFmlException() {
        try {
            new RechargePedestalBlockEntity(BlockPos.ZERO, null);
            fail("Expected FML loading exception when instantiating RechargePedestalBlockEntity in headless test");
        } catch (Throwable t) {
            assertTrue(t instanceof ExceptionInInitializerError || t instanceof NoClassDefFoundError || t instanceof IllegalStateException,
                    "Expected FML environment exception in headless JUnit runner");
        }
    }
}
