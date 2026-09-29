package thaumcraft.common.items.curios;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import thaumcraft.common.items.baubles.logic.VerdantCharmLogic;

import static org.junit.jupiter.api.Assertions.*;

public class ItemVerdantCharmTest {

    @Test
    public void testClassStructure() throws Exception {
        // Use Class.forName with initialize=false to prevent Bootstrap exceptions from static block execution
        Class<?> clazz = Class.forName("thaumcraft.common.items.curios.ItemVerdantCharm", false, getClass().getClassLoader());

        // Verify it extends Item
        Class<?> superclass = clazz.getSuperclass();
        assertEquals("net.minecraft.world.item.Item", superclass.getName());

        // Verify it implements IRechargable
        boolean implementsIRechargable = false;
        for (Class<?> iface : clazz.getInterfaces()) {
            if (iface.getName().equals("thaumcraft.api.items.IRechargable")) {
                implementsIRechargable = true;
                break;
            }
        }
        assertTrue(implementsIRechargable, "ItemVerdantCharm should implement IRechargable");

        // Verify inventoryTick is overridden
        boolean hasInventoryTick = false;
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals("inventoryTick")) {
                hasInventoryTick = true;
                break;
            }
        }
        assertTrue(hasInventoryTick, "ItemVerdantCharm should override inventoryTick");
    }

    @Test
    public void testVerdantCharmLogic() {
        // Test health intervals
        assertEquals(VerdantCharmLogic.BASE_HEAL_INTERVAL, VerdantCharmLogic.calculateHealingPulseInterval(20.0f, 20.0f));
        assertEquals(VerdantCharmLogic.MIN_HEAL_INTERVAL, VerdantCharmLogic.calculateHealingPulseInterval(0.0f, 20.0f));
        int expected = VerdantCharmLogic.MIN_HEAL_INTERVAL + (VerdantCharmLogic.BASE_HEAL_INTERVAL - VerdantCharmLogic.MIN_HEAL_INTERVAL) / 2;
        assertEquals(expected, VerdantCharmLogic.calculateHealingPulseInterval(10.0f, 20.0f));

        // Test saturation calculation
        assertEquals(0.5f, VerdantCharmLogic.calculateSaturationConversionRatio(20, 20), 0.001f);
        assertEquals(1.5f, VerdantCharmLogic.calculateSaturationConversionRatio(0, 20), 0.001f);
        assertEquals(1.0f, VerdantCharmLogic.calculateSaturationConversionRatio(10, 20), 0.001f);
    }
}
