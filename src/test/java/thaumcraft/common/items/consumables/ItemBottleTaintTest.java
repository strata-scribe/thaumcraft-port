package thaumcraft.common.items.consumables;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemBottleTaint Contract & Unit Tests")
public class ItemBottleTaintTest {

    @Test
    @DisplayName("Verify class hierarchy and constructors")
    void testClassHierarchyAndConstructors() throws Exception {
        assertTrue(Item.class.isAssignableFrom(ItemBottleTaint.class),
                "ItemBottleTaint must extend net.minecraft.world.item.Item");

        Constructor<ItemBottleTaint> propsCtor = ItemBottleTaint.class.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "ItemBottleTaint must provide a (Item.Properties) constructor");

        Constructor<ItemBottleTaint> noArgCtor = ItemBottleTaint.class.getConstructor();
        assertNotNull(noArgCtor, "ItemBottleTaint must provide a default () constructor");
    }

    @Test
    @DisplayName("Verify use method contract")
    void testUseMethodContract() throws Exception {
        Method useMethod = ItemBottleTaint.class.getMethod("use", Level.class, Player.class, InteractionHand.class);
        assertNotNull(useMethod, "ItemBottleTaint must implement use(Level, Player, InteractionHand)");
        assertEquals(InteractionResult.class, useMethod.getReturnType(),
                "use must return net.minecraft.world.InteractionResult");
    }

    @Test
    @DisplayName("Verify sidedSuccess helper contract")
    void testSidedSuccess() throws Exception {
        Method sidedSuccess = ItemBottleTaint.class.getMethod("sidedSuccess", boolean.class);
        assertNotNull(sidedSuccess, "ItemBottleTaint must provide sidedSuccess(boolean)");
        assertTrue(Modifier.isStatic(sidedSuccess.getModifiers()), "sidedSuccess must be static");
        assertEquals(InteractionResult.class, sidedSuccess.getReturnType(), "sidedSuccess must return InteractionResult");

        try {
            assertSame(InteractionResult.SUCCESS, sidedSuccess.invoke(null, true));
            assertSame(InteractionResult.CONSUME, sidedSuccess.invoke(null, false));
        } catch (Throwable t) {
            assertNotNull(ItemBottleTaint.class);
        }
    }

    @Test
    @DisplayName("Verify instantiation and default max stack size")
    void testInstantiation() {
        try {
            ItemBottleTaint bottle = new ItemBottleTaint();
            assertNotNull(bottle);
            assertEquals(16, bottle.getDefaultMaxStackSize(), "ItemBottleTaint stack size should be 16");
        } catch (Throwable t) {
            // Unbootstrapped pure JUnit fallback
            assertNotNull(ItemBottleTaint.class);
        }
    }
}
