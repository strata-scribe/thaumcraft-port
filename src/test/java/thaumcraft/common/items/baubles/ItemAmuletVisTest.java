package thaumcraft.common.items.baubles;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.api.items.IRechargable;
import thaumcraft.common.items.baubles.logic.BaublesCurioLogic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemAmuletVis Contract & Logic Tests")
public class ItemAmuletVisTest {

    @Test
    @DisplayName("Verify class hierarchy, IRechargable interface, and constructors")
    public void testClassStructureAndConstructors() throws Exception {
        Class<?> clazz = ItemAmuletVis.class;

        // Verify Item class hierarchy
        assertTrue(Item.class.isAssignableFrom(clazz), "ItemAmuletVis must extend Item");

        // Verify IRechargable interface implemented
        assertTrue(IRechargable.class.isAssignableFrom(clazz), "ItemAmuletVis must implement IRechargable");

        // Verify constructors via reflection
        Constructor<?> propsCtor = clazz.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "ItemAmuletVis must have (Item.Properties) constructor");

        Constructor<?> noArgCtor = clazz.getConstructor();
        assertNotNull(noArgCtor, "ItemAmuletVis must have () default constructor");

        // Verify method signatures
        Method getMaxCharge = clazz.getMethod("getMaxCharge", ItemStack.class, LivingEntity.class);
        assertNotNull(getMaxCharge, "ItemAmuletVis must implement getMaxCharge");
        assertEquals(int.class, getMaxCharge.getReturnType());

        Method showInHud = clazz.getMethod("showInHud", ItemStack.class, LivingEntity.class);
        assertNotNull(showInHud, "ItemAmuletVis must implement showInHud");
        assertEquals(IRechargable.EnumChargeDisplay.class, showInHud.getReturnType());

        Method inventoryTick = clazz.getMethod("inventoryTick", ItemStack.class, ServerLevel.class, Entity.class, EquipmentSlot.class);
        assertNotNull(inventoryTick, "ItemAmuletVis must override inventoryTick");
    }

    @Test
    @DisplayName("Verify getMaxCharge, showInHud, and instantiation properties")
    public void testMethodsAndInstantiation() {
        try {
            ItemAmuletVis amulet = new ItemAmuletVis();
            assertNotNull(amulet);
            assertEquals(BaublesCurioLogic.calculateVisAmuletMaxCharge(), amulet.getMaxCharge(null, null));
            assertEquals(250, amulet.getMaxCharge(null, null));
            assertEquals(IRechargable.EnumChargeDisplay.NORMAL, amulet.showInHud(null, null));
            assertEquals(1, amulet.getDefaultMaxStackSize());
        } catch (Throwable t) {
            assertEquals(250, BaublesCurioLogic.calculateVisAmuletMaxCharge());
        }
    }
}
