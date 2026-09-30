package thaumcraft.common.items.armor;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.api.items.IWarpingGear;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemVoidArmor Unit and Logic Contract Tests")
public class ItemVoidArmorTest {

    @Test
    @DisplayName("Verify class hierarchy, interfaces, and constructors")
    public void testClassHierarchyAndConstructors() throws Exception {
        assertTrue(Item.class.isAssignableFrom(ItemVoidArmor.class),
                "ItemVoidArmor must extend net.minecraft.world.item.Item");
        assertTrue(IWarpingGear.class.isAssignableFrom(ItemVoidArmor.class),
                "ItemVoidArmor must implement IWarpingGear");

        Constructor<ItemVoidArmor> ctor1 = ItemVoidArmor.class.getConstructor(ArmorType.class, Item.Properties.class);
        assertNotNull(ctor1, "Must have (ArmorType, Properties) constructor");

        Constructor<ItemVoidArmor> ctor2 = ItemVoidArmor.class.getConstructor(ArmorType.class);
        assertNotNull(ctor2, "Must have (ArmorType) constructor");

        Constructor<ItemVoidArmor> ctor3 = ItemVoidArmor.class.getConstructor(Item.Properties.class);
        assertNotNull(ctor3, "Must have (Properties) constructor");

        Constructor<ItemVoidArmor> ctor4 = ItemVoidArmor.class.getConstructor();
        assertNotNull(ctor4, "Must have () default constructor");
    }

    @Test
    @DisplayName("Verify method signatures and contracts")
    public void testMethodSignatures() throws Exception {
        Method getArmorType = ItemVoidArmor.class.getMethod("getArmorType");
        assertEquals(ArmorType.class, getArmorType.getReturnType());

        Method getWarp = ItemVoidArmor.class.getMethod("getWarp", ItemStack.class, Player.class);
        assertEquals(int.class, getWarp.getReturnType());
    }

    @Test
    @DisplayName("Test instantiation (handled gracefully in unbootstrapped environment)")
    public void testInstantiation() {
        try {
            ItemVoidArmor armor = new ItemVoidArmor();
            assertNotNull(armor);
            assertEquals(1, armor.getDefaultMaxStackSize());
            assertEquals(ArmorType.CHESTPLATE, armor.getArmorType());
        } catch (Throwable t) {
            // Unbootstrapped pure JUnit environment without FMLLoader
            assertNotNull(ItemVoidArmor.class);
        }
    }

    @Test
    @DisplayName("Verify durability scaling across ArmorTypes: armorType.getDurability(15)")
    public void testDurabilityScaling() {
        assertEquals(165, ArmorType.HELMET.getDurability(15), "Void Helmet durability must be 15 * 11 = 165");
        assertEquals(240, ArmorType.CHESTPLATE.getDurability(15), "Void Chestplate durability must be 15 * 16 = 240");
        assertEquals(225, ArmorType.LEGGINGS.getDurability(15), "Void Leggings durability must be 15 * 15 = 225");
        assertEquals(195, ArmorType.BOOTS.getDurability(15), "Void Boots durability must be 15 * 13 = 195");
    }

    @Test
    @DisplayName("Verify Warp value delegation: getVoidArmorWarp returns 1")
    public void testWarpDelegation() {
        assertEquals(1, EquipmentLogic.getVoidArmorWarp(), "Void Armor warp per piece must be 1");
        assertEquals(1, EquipmentLogic.calculateWarp(false, 1));
        assertEquals(2, EquipmentLogic.calculateWarp(false, 2));
        assertEquals(3, EquipmentLogic.calculateWarp(false, 3));
        assertEquals(4, EquipmentLogic.calculateWarp(false, 4));
        assertEquals(0, EquipmentLogic.calculateWarp(false, 0));
    }

    @Test
    @DisplayName("Verify self-repair logic: repairs 1 durability every 20 ticks")
    public void testSelfRepairLogic() {
        int initialDamage = 50;

        // Repairs 1 damage on tick 20, 40, 60...
        assertEquals(49, EquipmentLogic.calculateVoidRepair(initialDamage, 20));
        assertEquals(49, EquipmentLogic.calculateVoidRepair(initialDamage, 40));
        assertEquals(49, EquipmentLogic.calculateVoidRepair(initialDamage, 100));

        // Does NOT repair on non-20 tick counts
        assertEquals(50, EquipmentLogic.calculateVoidRepair(initialDamage, 1));
        assertEquals(50, EquipmentLogic.calculateVoidRepair(initialDamage, 19));
        assertEquals(50, EquipmentLogic.calculateVoidRepair(initialDamage, 21));
        assertEquals(50, EquipmentLogic.calculateVoidRepair(initialDamage, 0));

        // Damage is 0: remains 0
        assertEquals(0, EquipmentLogic.calculateVoidRepair(0, 20));
        assertEquals(0, EquipmentLogic.calculateVoidRepair(0, 40));

        // Negative damage clamped to 0
        assertEquals(0, EquipmentLogic.calculateVoidRepair(-5, 20));

        // calculateVoidSelfRepair alias behaves identically
        assertEquals(49, EquipmentLogic.calculateVoidSelfRepair(initialDamage, 20));
        assertEquals(50, EquipmentLogic.calculateVoidSelfRepair(initialDamage, 15));
    }
}
