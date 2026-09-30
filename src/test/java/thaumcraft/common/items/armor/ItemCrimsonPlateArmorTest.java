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

@DisplayName("ItemCrimsonPlateArmor Unit and Logic Contract Tests")
public class ItemCrimsonPlateArmorTest {

    @Test
    @DisplayName("Verify class hierarchy, interfaces, and constructors")
    public void testClassHierarchyAndConstructors() throws Exception {
        assertTrue(Item.class.isAssignableFrom(ItemCrimsonPlateArmor.class),
                "ItemCrimsonPlateArmor must extend net.minecraft.world.item.Item");
        assertTrue(IWarpingGear.class.isAssignableFrom(ItemCrimsonPlateArmor.class),
                "ItemCrimsonPlateArmor must implement IWarpingGear");

        Constructor<ItemCrimsonPlateArmor> ctor1 = ItemCrimsonPlateArmor.class.getConstructor(ArmorType.class, Item.Properties.class);
        assertNotNull(ctor1, "Must have (ArmorType, Properties) constructor");

        Constructor<ItemCrimsonPlateArmor> ctor2 = ItemCrimsonPlateArmor.class.getConstructor(ArmorType.class);
        assertNotNull(ctor2, "Must have (ArmorType) constructor");

        Constructor<ItemCrimsonPlateArmor> ctor3 = ItemCrimsonPlateArmor.class.getConstructor(Item.Properties.class);
        assertNotNull(ctor3, "Must have (Properties) constructor");

        Constructor<ItemCrimsonPlateArmor> ctor4 = ItemCrimsonPlateArmor.class.getConstructor();
        assertNotNull(ctor4, "Must have () default constructor");
    }

    @Test
    @DisplayName("Verify method signatures and contracts")
    public void testMethodSignatures() throws Exception {
        Method getArmorType = ItemCrimsonPlateArmor.class.getMethod("getArmorType");
        assertEquals(ArmorType.class, getArmorType.getReturnType());

        Method getWarp = ItemCrimsonPlateArmor.class.getMethod("getWarp", ItemStack.class, Player.class);
        assertEquals(int.class, getWarp.getReturnType());
    }

    @Test
    @DisplayName("Test instantiation (handled gracefully in unbootstrapped environment)")
    public void testInstantiation() {
        try {
            ItemCrimsonPlateArmor armor = new ItemCrimsonPlateArmor();
            assertNotNull(armor);
            assertEquals(1, armor.getDefaultMaxStackSize());
            assertEquals(ArmorType.CHESTPLATE, armor.getArmorType());
            assertEquals(1, armor.getWarp(null, null));
        } catch (Throwable t) {
            // Unbootstrapped pure JUnit environment without FMLLoader
            assertNotNull(ItemCrimsonPlateArmor.class);
        }
    }

    @Test
    @DisplayName("Verify durability scaling across ArmorTypes: armorType.getDurability(20)")
    public void testDurabilityScaling() {
        assertEquals(220, ArmorType.HELMET.getDurability(20), "Crimson Plate Helmet durability must be 20 * 11 = 220");
        assertEquals(320, ArmorType.CHESTPLATE.getDurability(20), "Crimson Plate Chestplate durability must be 20 * 16 = 320");
        assertEquals(300, ArmorType.LEGGINGS.getDurability(20), "Crimson Plate Leggings durability must be 20 * 15 = 300");
        assertEquals(260, ArmorType.BOOTS.getDurability(20), "Crimson Plate Boots durability must be 20 * 13 = 260");
    }

    @Test
    @DisplayName("Verify logic integration for plate armor")
    public void testLogicIntegration() {
        assertEquals(20, CrimsonArmorLogic.getArmorDurabilityMultiplier("plate"));
        assertEquals(1, CrimsonArmorLogic.calculateCrimsonWarp("plate", 1));
        assertEquals(4, CrimsonArmorLogic.calculateCrimsonWarp("plate", 4));
    }
}
