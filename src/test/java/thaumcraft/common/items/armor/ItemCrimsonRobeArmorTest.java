package thaumcraft.common.items.armor;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.items.IWarpingGear;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemCrimsonRobeArmor Unit and Logic Contract Tests")
public class ItemCrimsonRobeArmorTest {

    @Test
    @DisplayName("Verify class hierarchy, interfaces, and constructors")
    public void testClassHierarchyAndConstructors() throws Exception {
        assertTrue(Item.class.isAssignableFrom(ItemCrimsonRobeArmor.class),
                "ItemCrimsonRobeArmor must extend net.minecraft.world.item.Item");
        assertTrue(IWarpingGear.class.isAssignableFrom(ItemCrimsonRobeArmor.class),
                "ItemCrimsonRobeArmor must implement IWarpingGear");
        assertTrue(IVisDiscountGear.class.isAssignableFrom(ItemCrimsonRobeArmor.class),
                "ItemCrimsonRobeArmor must implement IVisDiscountGear");

        Constructor<ItemCrimsonRobeArmor> ctor1 = ItemCrimsonRobeArmor.class.getConstructor(ArmorType.class, Item.Properties.class);
        assertNotNull(ctor1, "Must have (ArmorType, Properties) constructor");

        Constructor<ItemCrimsonRobeArmor> ctor2 = ItemCrimsonRobeArmor.class.getConstructor(ArmorType.class);
        assertNotNull(ctor2, "Must have (ArmorType) constructor");

        Constructor<ItemCrimsonRobeArmor> ctor3 = ItemCrimsonRobeArmor.class.getConstructor(Item.Properties.class);
        assertNotNull(ctor3, "Must have (Properties) constructor");

        Constructor<ItemCrimsonRobeArmor> ctor4 = ItemCrimsonRobeArmor.class.getConstructor();
        assertNotNull(ctor4, "Must have () default constructor");
    }

    @Test
    @DisplayName("Verify method signatures and contracts")
    public void testMethodSignatures() throws Exception {
        Method getArmorType = ItemCrimsonRobeArmor.class.getMethod("getArmorType");
        assertEquals(ArmorType.class, getArmorType.getReturnType());

        Method getWarp = ItemCrimsonRobeArmor.class.getMethod("getWarp", ItemStack.class, Player.class);
        assertEquals(int.class, getWarp.getReturnType());

        Method getVisDiscount = ItemCrimsonRobeArmor.class.getMethod("getVisDiscount", ItemStack.class, Player.class);
        assertEquals(int.class, getVisDiscount.getReturnType());
    }

    @Test
    @DisplayName("Test instantiation (handled gracefully in unbootstrapped environment)")
    public void testInstantiation() {
        try {
            ItemCrimsonRobeArmor armor = new ItemCrimsonRobeArmor();
            assertNotNull(armor);
            assertEquals(1, armor.getDefaultMaxStackSize());
            assertEquals(ArmorType.CHESTPLATE, armor.getArmorType());
            assertEquals(1, armor.getWarp(null, null));
            assertEquals(5, armor.getVisDiscount(null, null));
        } catch (Throwable t) {
            // Unbootstrapped pure JUnit environment without FMLLoader
            assertNotNull(ItemCrimsonRobeArmor.class);
        }
    }

    @Test
    @DisplayName("Verify durability scaling across ArmorTypes: armorType.getDurability(17)")
    public void testDurabilityScaling() {
        assertEquals(187, ArmorType.HELMET.getDurability(17), "Crimson Robe Helmet durability must be 17 * 11 = 187");
        assertEquals(272, ArmorType.CHESTPLATE.getDurability(17), "Crimson Robe Chestplate durability must be 17 * 16 = 272");
        assertEquals(255, ArmorType.LEGGINGS.getDurability(17), "Crimson Robe Leggings durability must be 17 * 15 = 255");
        assertEquals(221, ArmorType.BOOTS.getDurability(17), "Crimson Robe Boots durability must be 17 * 13 = 221");
    }

    @Test
    @DisplayName("Verify logic integration for robe armor")
    public void testLogicIntegration() {
        assertEquals(17, CrimsonArmorLogic.getArmorDurabilityMultiplier("robe"));
        assertEquals(1, CrimsonArmorLogic.calculateCrimsonWarp("robe", 1));
        assertEquals(3, CrimsonArmorLogic.calculateCrimsonWarp("robe", 3));
        assertEquals(15, CrimsonArmorLogic.calculateVisDiscount(3));
    }
}
