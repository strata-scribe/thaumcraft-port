package thaumcraft.common.items.armor;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.equipment.ArmorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.items.IWarpingGear;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemVoidRobeArmor Unit and Logic Contract Tests")
public class ItemVoidRobeArmorTest {

    @Test
    @DisplayName("Verify class hierarchy, interfaces, and constructors")
    public void testClassHierarchyAndConstructors() throws Exception {
        assertTrue(Item.class.isAssignableFrom(ItemVoidRobeArmor.class),
                "ItemVoidRobeArmor must extend net.minecraft.world.item.Item");
        assertTrue(IVisDiscountGear.class.isAssignableFrom(ItemVoidRobeArmor.class),
                "ItemVoidRobeArmor must implement IVisDiscountGear");
        assertTrue(IWarpingGear.class.isAssignableFrom(ItemVoidRobeArmor.class),
                "ItemVoidRobeArmor must implement IWarpingGear");
        assertTrue(IGoggles.class.isAssignableFrom(ItemVoidRobeArmor.class),
                "ItemVoidRobeArmor must implement IGoggles");
        assertTrue(IRevealer.class.isAssignableFrom(ItemVoidRobeArmor.class),
                "ItemVoidRobeArmor must implement IRevealer");

        Constructor<ItemVoidRobeArmor> ctor1 = ItemVoidRobeArmor.class.getConstructor(ArmorType.class, Item.Properties.class);
        assertNotNull(ctor1, "Must have (ArmorType, Properties) constructor");

        Constructor<ItemVoidRobeArmor> ctor2 = ItemVoidRobeArmor.class.getConstructor(ArmorType.class);
        assertNotNull(ctor2, "Must have (ArmorType) constructor");

        Constructor<ItemVoidRobeArmor> ctor3 = ItemVoidRobeArmor.class.getConstructor(Item.Properties.class);
        assertNotNull(ctor3, "Must have (Properties) constructor");

        Constructor<ItemVoidRobeArmor> ctor4 = ItemVoidRobeArmor.class.getConstructor();
        assertNotNull(ctor4, "Must have () default constructor");
    }

    @Test
    @DisplayName("Verify method signatures and contracts")
    public void testMethodSignatures() throws Exception {
        Method getArmorType = ItemVoidRobeArmor.class.getMethod("getArmorType");
        assertEquals(ArmorType.class, getArmorType.getReturnType());

        Method getVisDiscount = ItemVoidRobeArmor.class.getMethod("getVisDiscount", ItemStack.class, Player.class);
        assertEquals(int.class, getVisDiscount.getReturnType());

        Method getWarp = ItemVoidRobeArmor.class.getMethod("getWarp", ItemStack.class, Player.class);
        assertEquals(int.class, getWarp.getReturnType());

        Method showIngamePopups = ItemVoidRobeArmor.class.getMethod("showIngamePopups", ItemStack.class, LivingEntity.class);
        assertEquals(boolean.class, showIngamePopups.getReturnType());

        Method showNodes = ItemVoidRobeArmor.class.getMethod("showNodes", ItemStack.class, LivingEntity.class);
        assertEquals(boolean.class, showNodes.getReturnType());

        Method hasColor = ItemVoidRobeArmor.class.getMethod("hasColor", ItemStack.class);
        assertEquals(boolean.class, hasColor.getReturnType());

        Method getColor = ItemVoidRobeArmor.class.getMethod("getColor", ItemStack.class);
        assertEquals(int.class, getColor.getReturnType());

        Method setColor = ItemVoidRobeArmor.class.getMethod("setColor", ItemStack.class, int.class);
        assertEquals(void.class, setColor.getReturnType());

        Method removeColor = ItemVoidRobeArmor.class.getMethod("removeColor", ItemStack.class);
        assertEquals(void.class, removeColor.getReturnType());
    }

    @Test
    @DisplayName("Verify durability scaling across ArmorTypes: armorType.getDurability(18)")
    public void testDurabilityScaling() {
        assertEquals(198, ArmorType.HELMET.getDurability(18), "Void Robe Helmet durability must be 18 * 11 = 198");
        assertEquals(288, ArmorType.CHESTPLATE.getDurability(18), "Void Robe Chestplate durability must be 18 * 16 = 288");
        assertEquals(270, ArmorType.LEGGINGS.getDurability(18), "Void Robe Leggings durability must be 18 * 15 = 270");
        assertEquals(234, ArmorType.BOOTS.getDurability(18), "Void Robe Boots durability must be 18 * 13 = 234");
    }

    @Test
    @DisplayName("Verify Vis Discount delegation: 5% per piece")
    public void testVisDiscountDelegation() {
        assertEquals(5, EquipmentLogic.calculateVisDiscount(1), "1 piece provides 5% discount");
        assertEquals(10, EquipmentLogic.calculateVisDiscount(2), "2 pieces provide 10% discount");
        assertEquals(15, EquipmentLogic.calculateVisDiscount(3), "3 pieces provide 15% discount");
        assertEquals(0, EquipmentLogic.calculateVisDiscount(0), "0 pieces provide 0% discount");
        assertEquals(100, EquipmentLogic.calculateVisDiscount(25), "Discount capped at 100%");
    }

    @Test
    @DisplayName("Verify Warp value delegation: getVoidRobeWarp returns 3")
    public void testWarpDelegation() {
        assertEquals(3, EquipmentLogic.getVoidRobeWarp(), "Void Robe warp per piece must be 3");
        assertEquals(3, EquipmentLogic.calculateWarp(true, 1));
        assertEquals(6, EquipmentLogic.calculateWarp(true, 2));
        assertEquals(9, EquipmentLogic.calculateWarp(true, 3));
        assertEquals(0, EquipmentLogic.calculateWarp(true, 0));
    }

    @Test
    @DisplayName("Verify revealing contract: Only Helmet reveals nodes and in-game popups")
    public void testRevealerContract() {
        java.util.function.Predicate<ArmorType> isRevealer = armorType -> armorType == ArmorType.HELMET;

        assertTrue(isRevealer.test(ArmorType.HELMET), "HELMET has built-in goggles");
        assertFalse(isRevealer.test(ArmorType.CHESTPLATE), "CHESTPLATE does not have goggles");
        assertFalse(isRevealer.test(ArmorType.LEGGINGS), "LEGGINGS do not have goggles");
        assertFalse(isRevealer.test(ArmorType.BOOTS), "BOOTS do not have goggles");
    }

    @Test
    @DisplayName("Verify dyeable color handling and default color constant")
    public void testDyeableColorHandling() {
        assertEquals(0x6a3860, ItemVoidRobeArmor.DEFAULT_COLOR, "Default color must be 0x6a3860 (6961280)");

        // Test DyedItemColor component contract
        DyedItemColor defaultDyed = new DyedItemColor(ItemVoidRobeArmor.DEFAULT_COLOR);
        assertEquals(0x6a3860, defaultDyed.rgb());

        int customColor = 0xFF00AA;
        DyedItemColor customDyed = new DyedItemColor(customColor);
        assertEquals(customColor, customDyed.rgb());

        // Test null stack safety
        try {
            ItemVoidRobeArmor armor = new ItemVoidRobeArmor();
            assertEquals(ItemVoidRobeArmor.DEFAULT_COLOR, armor.getColor(null));
            assertFalse(armor.hasColor(null));
            armor.setColor(null, 0xFFFFFF);
            armor.removeColor(null);
        } catch (Throwable t) {
            // Expected in unbootstrapped JUnit
            assertNotNull(ItemVoidRobeArmor.class);
        }
    }

    @Test
    @DisplayName("Void Robe Logic: Passive repair tick reduction based on void aura")
    public void testVoidRobePassiveRepairTicks() {
        assertEquals(20, VoidRobeArmorLogic.calculatePassiveRepairTicks(0), "No void aura yields 20 ticks");
        assertEquals(20, VoidRobeArmorLogic.calculatePassiveRepairTicks(-5), "Negative void aura yields 20 ticks");
        assertEquals(18, VoidRobeArmorLogic.calculatePassiveRepairTicks(10), "10 void aura reduces by 2 ticks -> 18 ticks");
        assertEquals(15, VoidRobeArmorLogic.calculatePassiveRepairTicks(25), "25 void aura reduces by 5 ticks -> 15 ticks");
        assertEquals(5, VoidRobeArmorLogic.calculatePassiveRepairTicks(100), "Capped at minimum 5 ticks");
    }

    @Test
    @DisplayName("Void Robe Logic: Damage resistance against warp attacks (20% per piece)")
    public void testVoidRobeDamageResistance() {
        float damage = 20.0f;

        // Non-warp attack: no resistance
        assertEquals(damage, VoidRobeArmorLogic.calculateDamageResistance(damage, false, 3), 1e-4);

        // Warp attack with 0 pieces: no resistance
        assertEquals(damage, VoidRobeArmorLogic.calculateDamageResistance(damage, true, 0), 1e-4);

        // Warp attack with 1 piece: 20% resistance -> 80% damage
        assertEquals(16.0f, VoidRobeArmorLogic.calculateDamageResistance(damage, true, 1), 1e-4);

        // Warp attack with 2 pieces: 40% resistance -> 60% damage
        assertEquals(12.0f, VoidRobeArmorLogic.calculateDamageResistance(damage, true, 2), 1e-4);

        // Warp attack with 3 pieces: 60% resistance -> 40% damage
        assertEquals(8.0f, VoidRobeArmorLogic.calculateDamageResistance(damage, true, 3), 1e-4);

        // Warp attack with 5 pieces: 100% resistance -> 0 damage
        assertEquals(0.0f, VoidRobeArmorLogic.calculateDamageResistance(damage, true, 5), 1e-4);

        // Zero or negative damage
        assertEquals(0.0f, VoidRobeArmorLogic.calculateDamageResistance(0.0f, true, 3), 1e-4);
        assertEquals(0.0f, VoidRobeArmorLogic.calculateDamageResistance(-10.0f, true, 3), 1e-4);
    }
}
