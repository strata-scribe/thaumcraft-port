package thaumcraft.common.tiles.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.junit.jupiter.api.Test;
import thaumcraft.common.tiles.devices.logic.VisBatteryStorageLogic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

public class VisBatteryBlockEntityTest {

    @Test
    public void testClassHierarchyAndConstructors() throws Exception {
        Class<?> clazz = VisBatteryBlockEntity.class;

        // Check superclass is BlockEntity
        assertEquals(BlockEntity.class, clazz.getSuperclass(), "VisBatteryBlockEntity must extend BlockEntity");

        // Check primary constructor (BlockPos, BlockState)
        Constructor<?> primaryCtor = clazz.getDeclaredConstructor(BlockPos.class, BlockState.class);
        assertNotNull(primaryCtor, "Primary constructor (BlockPos, BlockState) must exist");
        assertTrue(Modifier.isPublic(primaryCtor.getModifiers()), "Primary constructor must be public");

        // Check overloaded constructor (BlockPos, BlockState, float, float, float)
        Constructor<?> overloadedCtor = clazz.getDeclaredConstructor(BlockPos.class, BlockState.class, float.class, float.class, float.class);
        assertNotNull(overloadedCtor, "Overloaded constructor must exist");
        assertTrue(Modifier.isPublic(overloadedCtor.getModifiers()), "Overloaded constructor must be public");
    }

    @Test
    public void testMethodSignatures() throws Exception {
        Class<?> clazz = VisBatteryBlockEntity.class;

        Method getLogic = clazz.getDeclaredMethod("getLogic");
        assertEquals(VisBatteryStorageLogic.class, getLogic.getReturnType());

        Method getStoredVis = clazz.getDeclaredMethod("getStoredVis");
        assertEquals(float.class, getStoredVis.getReturnType());

        Method setStoredVis = clazz.getDeclaredMethod("setStoredVis", float.class);
        assertEquals(void.class, setStoredVis.getReturnType());

        Method getMaxCapacity = clazz.getDeclaredMethod("getMaxCapacity");
        assertEquals(float.class, getMaxCapacity.getReturnType());

        Method isFull = clazz.getDeclaredMethod("isFull");
        assertEquals(boolean.class, isFull.getReturnType());

        Method isEmpty = clazz.getDeclaredMethod("isEmpty");
        assertEquals(boolean.class, isEmpty.getReturnType());

        Method getFillRatio = clazz.getDeclaredMethod("getFillRatio");
        assertEquals(float.class, getFillRatio.getReturnType());

        Method getRemainingCapacity = clazz.getDeclaredMethod("getRemainingCapacity");
        assertEquals(float.class, getRemainingCapacity.getReturnType());

        Method siphon = clazz.getDeclaredMethod("siphonFromAura", float.class);
        assertEquals(float.class, siphon.getReturnType());

        Method discharge = clazz.getDeclaredMethod("dischargeToMachine", float.class);
        assertEquals(float.class, discharge.getReturnType());

        Method tick = clazz.getDeclaredMethod("tickBattery", Level.class, BlockPos.class);
        assertEquals(void.class, tick.getReturnType());

        Method serverTick = clazz.getDeclaredMethod("serverTick", Level.class, BlockPos.class, BlockState.class, VisBatteryBlockEntity.class);
        assertTrue(Modifier.isStatic(serverTick.getModifiers()));

        Method saveAdditional = clazz.getDeclaredMethod("saveAdditional", ValueOutput.class);
        assertTrue(Modifier.isProtected(saveAdditional.getModifiers()));

        Method loadAdditional = clazz.getDeclaredMethod("loadAdditional", ValueInput.class);
        assertTrue(Modifier.isProtected(loadAdditional.getModifiers()));
    }

    @Test
    public void testHeadlessInstantiationThrowsFmlException() {
        try {
            new VisBatteryBlockEntity(BlockPos.ZERO, null);
            fail("Expected FML loading exception when instantiating VisBatteryBlockEntity in headless test");
        } catch (Throwable t) {
            assertTrue(t instanceof ExceptionInInitializerError || t instanceof NoClassDefFoundError || t instanceof IllegalStateException,
                    "Expected FML environment exception in headless JUnit runner");
        }
    }
}
