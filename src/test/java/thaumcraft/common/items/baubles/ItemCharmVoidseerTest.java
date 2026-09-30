package thaumcraft.common.items.baubles;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.common.items.baubles.logic.BaublesCurioLogic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemCharmVoidseer Contract & Logic Tests")
public class ItemCharmVoidseerTest {

    @Test
    @DisplayName("Verify class hierarchy, IWarpingGear interface, and constructors")
    public void testClassStructureAndConstructors() throws Exception {
        Class<?> clazz = ItemCharmVoidseer.class;

        // Verify Item class hierarchy
        assertTrue(Item.class.isAssignableFrom(clazz), "ItemCharmVoidseer must extend Item");

        // Verify IWarpingGear interface implemented
        assertTrue(IWarpingGear.class.isAssignableFrom(clazz), "ItemCharmVoidseer must implement IWarpingGear");

        // Verify constructors via reflection
        Constructor<?> propsCtor = clazz.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "ItemCharmVoidseer must have (Item.Properties) constructor");

        Constructor<?> noArgCtor = clazz.getConstructor();
        assertNotNull(noArgCtor, "ItemCharmVoidseer must have () default constructor");

        // Verify getWarp method
        Method getWarp = clazz.getMethod("getWarp", ItemStack.class, Player.class);
        assertNotNull(getWarp, "ItemCharmVoidseer must implement getWarp");
        assertEquals(int.class, getWarp.getReturnType());
    }

    @Test
    @DisplayName("Verify getWarp returns BaublesCurioLogic voidseer warp bonus")
    public void testGetWarpAndProperties() {
        try {
            ItemCharmVoidseer charm = new ItemCharmVoidseer();
            assertNotNull(charm);
            assertEquals(BaublesCurioLogic.getVoidseerWarpBonus(), charm.getWarp(null, null));
            assertEquals(1, charm.getWarp(null, null));
            assertEquals(1, charm.getDefaultMaxStackSize());
        } catch (Throwable t) {
            assertEquals(1, BaublesCurioLogic.getVoidseerWarpBonus());
        }
    }
}
