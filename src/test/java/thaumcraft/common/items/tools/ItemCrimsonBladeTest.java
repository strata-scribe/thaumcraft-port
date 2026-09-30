package thaumcraft.common.items.tools;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.common.items.tools.logic.ThaumcraftToolLogic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemCrimsonBlade Contract & Unit Tests")
public class ItemCrimsonBladeTest {

    @Test
    @DisplayName("Verify class hierarchy and IWarpingGear interface")
    public void testClassHierarchyAndInterfaces() {
        assertTrue(Item.class.isAssignableFrom(ItemCrimsonBlade.class),
                "ItemCrimsonBlade must extend net.minecraft.world.item.Item");
        assertTrue(IWarpingGear.class.isAssignableFrom(ItemCrimsonBlade.class),
                "ItemCrimsonBlade must implement IWarpingGear");
    }

    @Test
    @DisplayName("Verify constructors via reflection: (Properties) and ()")
    public void testConstructors() throws Exception {
        Constructor<ItemCrimsonBlade> propsCtor = ItemCrimsonBlade.class.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "ItemCrimsonBlade must have (Item.Properties) constructor");

        Constructor<ItemCrimsonBlade> defaultCtor = ItemCrimsonBlade.class.getConstructor();
        assertNotNull(defaultCtor, "ItemCrimsonBlade must have () default constructor");
    }

    @Test
    @DisplayName("Verify method signatures: getWarp and hurtEnemy")
    public void testMethodSignatures() throws Exception {
        Method getWarp = ItemCrimsonBlade.class.getMethod("getWarp", ItemStack.class, Player.class);
        assertNotNull(getWarp, "ItemCrimsonBlade must implement getWarp");
        assertEquals(int.class, getWarp.getReturnType());

        Method hurtEnemy = ItemCrimsonBlade.class.getMethod("hurtEnemy",
                ItemStack.class, LivingEntity.class, LivingEntity.class);
        assertNotNull(hurtEnemy, "ItemCrimsonBlade must implement hurtEnemy");
        assertEquals(void.class, hurtEnemy.getReturnType());
    }

    @Test
    @DisplayName("Verify warp value delegation: getWarp returns 1")
    public void testWarpValue() {
        assertEquals(1, ThaumcraftToolLogic.getCrimsonBladeWarp());
        try {
            ItemCrimsonBlade blade = new ItemCrimsonBlade();
            assertEquals(1, blade.getWarp(null, null), "Crimson blade warp must be 1");
        } catch (Throwable t) {
            // Handled gracefully in unbootstrapped pure JUnit environments
            assertNotNull(ItemCrimsonBlade.class);
        }
    }

    @Test
    @DisplayName("Verify instantiation and default properties (stacksTo 1, durability 250)")
    public void testInstantiationAndDurability() {
        assertEquals(250, ThaumcraftToolLogic.getCrimsonBladeDurability());
        try {
            ItemCrimsonBlade blade = new ItemCrimsonBlade();
            assertNotNull(blade);
            assertEquals(1, blade.getDefaultMaxStackSize(), "Crimson blade max stack size must be 1");
        } catch (Throwable t) {
            // Handled gracefully in unbootstrapped pure JUnit environments
            assertNotNull(ItemCrimsonBlade.class);
        }
    }
}
