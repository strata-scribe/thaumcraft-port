package thaumcraft.common.blocks.entities;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class ThaumcraftBlockEntitiesRegistrationTest {

    @Test
    public void testBlockEntitiesRegistrationFieldsPresent() throws Exception {
        Class<?> clazz = Class.forName("thaumcraft.common.blocks.entities.ThaumcraftBlockEntities", false, getClass().getClassLoader());

        Field visBatteryField = clazz.getDeclaredField("VIS_BATTERY");
        assertNotNull(visBatteryField, "VIS_BATTERY field must exist on ThaumcraftBlockEntities");
        int visBatteryMods = visBatteryField.getModifiers();
        assertTrue(Modifier.isPublic(visBatteryMods), "VIS_BATTERY must be public");
        assertTrue(Modifier.isStatic(visBatteryMods), "VIS_BATTERY must be static");
        assertTrue(Modifier.isFinal(visBatteryMods), "VIS_BATTERY must be final");
        assertTrue(Supplier.class.isAssignableFrom(visBatteryField.getType()), "VIS_BATTERY must implement Supplier");

        Field rechargePedestalField = clazz.getDeclaredField("RECHARGE_PEDESTAL");
        assertNotNull(rechargePedestalField, "RECHARGE_PEDESTAL field must exist on ThaumcraftBlockEntities");
        int pedestalMods = rechargePedestalField.getModifiers();
        assertTrue(Modifier.isPublic(pedestalMods), "RECHARGE_PEDESTAL must be public");
        assertTrue(Modifier.isStatic(pedestalMods), "RECHARGE_PEDESTAL must be static");
        assertTrue(Modifier.isFinal(pedestalMods), "RECHARGE_PEDESTAL must be final");
        assertTrue(Supplier.class.isAssignableFrom(rechargePedestalField.getType()), "RECHARGE_PEDESTAL must implement Supplier");

        Field blockEntitiesField = clazz.getDeclaredField("BLOCK_ENTITIES");
        assertNotNull(blockEntitiesField, "BLOCK_ENTITIES field must exist on ThaumcraftBlockEntities");
        int regMods = blockEntitiesField.getModifiers();
        assertTrue(Modifier.isPublic(regMods), "BLOCK_ENTITIES must be public");
        assertTrue(Modifier.isStatic(regMods), "BLOCK_ENTITIES must be static");
        assertTrue(Modifier.isFinal(regMods), "BLOCK_ENTITIES must be final");

        java.lang.reflect.Type visBatteryGenericType = visBatteryField.getGenericType();
        assertTrue(visBatteryGenericType.getTypeName().contains("VisBatteryBlockEntity"),
                "VIS_BATTERY generic type should reference VisBatteryBlockEntity");

        java.lang.reflect.Type pedestalGenericType = rechargePedestalField.getGenericType();
        assertTrue(pedestalGenericType.getTypeName().contains("RechargePedestalBlockEntity"),
                "RECHARGE_PEDESTAL generic type should reference RechargePedestalBlockEntity");
    }
}
