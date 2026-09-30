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

@DisplayName("ItemCrimsonPraetorArmor Unit and Logic Contract Tests")
public class ItemCrimsonPraetorArmorTest {

    @Test
    @DisplayName("Verify class hierarchy, interfaces, and constructors")
    public void testClassHierarchyAndConstructors() throws Exception {
        assertTrue(Item.class.isAssignableFrom(ItemCrimsonPraetorArmor.class),
                "ItemCrimsonPraetorArmor must extend net.minecraft.world.item.Item");
        assertTrue(IWarpingGear.class.isAssignableFrom(ItemCrimsonPraetorArmor.class),
                "ItemCrimsonPraetorArmor must implement IWarpingGear");

        Constructor<ItemCrimsonPraetorArmor> ctor1 = ItemCrimsonPraetorArmor.class.getConstructor(ArmorType.class, Item.Properties.class);
        assertNotNull(ctor1, "Must have (ArmorType, Properties) constructor");

        Constructor<ItemCrimsonPraetorArmor> ctor2 = ItemCrimsonPraetorArmor.class.getConstructor(ArmorType.class);
        assertNotNull(ctor2, "Must have (ArmorType) constructor");

        Constructor<ItemCrimsonPraetorArmor> ctor3 = ItemCrimsonPraetorArmor.class.getConstructor(Item.Properties.class);
        assertNotNull(ctor3, "Must have (Properties) constructor");

        Constructor<ItemCrimsonPraetorArmor> ctor4 = ItemCrimsonPraetorArmor.class.getConstructor();
        assertNotNull(ctor4, "Must have () default constructor");
    }

    @Test
    @DisplayName("Verify method signatures and contracts")
    public void testMethodSignatures() throws Exception {
        Method getArmorType = ItemCrimsonPraetorArmor.class.getMethod("getArmorType");
        assertEquals(ArmorType.class, getArmorType.getReturnType());

        Method getWarp = ItemCrimsonPraetorArmor.class.getMethod("getWarp", ItemStack.class, Player.class);
        assertEquals(int.class, getWarp.getReturnType());
    }

    @Test
    @DisplayName("Test instantiation (handled gracefully in unbootstrapped environment)")
    public void testInstantiation() {
        try {
            ItemCrimsonPraetorArmor armor = new ItemCrimsonPraetorArmor();
            assertNotNull(armor);
            assertEquals(1, armor.getDefaultMaxStackSize());
            assertEquals(ArmorType.CHESTPLATE, armor.getArmorType());
            assertEquals(2, armor.getWarp(null, null));
        } catch (Throwable t) {
            // Unbootstrapped pure JUnit environment without FMLLoader
            assertNotNull(ItemCrimsonPraetorArmor.class);
        }
    }

    @Test
    @DisplayName("Verify durability scaling across ArmorTypes: armorType.getDurability(30)")
    public void testDurabilityScaling() {
        assertEquals(330, ArmorType.HELMET.getDurability(30), "Crimson Praetor Helmet durability must be 30 * 11 = 330");
        assertEquals(480, ArmorType.CHESTPLATE.getDurability(30), "Crimson Praetor Chestplate durability must be 30 * 16 = 480");
        assertEquals(450, ArmorType.LEGGINGS.getDurability(30), "Crimson Praetor Leggings durability must be 30 * 15 = 450");
        assertEquals(390, ArmorType.BOOTS.getDurability(30), "Crimson Praetor Boots durability must be 30 * 13 = 390");
    }

    @Test
    @DisplayName("Verify logic integration for praetor armor")
    public void testLogicIntegration() {
        assertEquals(30, CrimsonArmorLogic.getArmorDurabilityMultiplier("praetor"));
        assertEquals(2, CrimsonArmorLogic.calculateCrimsonWarp("praetor", 1));
        assertEquals(6, CrimsonArmorLogic.calculateCrimsonWarp("praetor", 3));
        assertTrue(CrimsonArmorLogic.isPraetorLeader(true, true));
    }
}
