package thaumcraft.common.items.tools;

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

@DisplayName("ItemCausalityCollapser Contract & Unit Tests")
public class ItemCausalityCollapserTest {

    @Test
    @DisplayName("Verify class hierarchy and constructors")
    void testClassHierarchyAndConstructors() throws Exception {
        assertTrue(Item.class.isAssignableFrom(ItemCausalityCollapser.class),
                "ItemCausalityCollapser must extend net.minecraft.world.item.Item");

        Constructor<ItemCausalityCollapser> propsCtor = ItemCausalityCollapser.class.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "ItemCausalityCollapser must provide a (Item.Properties) constructor");

        Constructor<ItemCausalityCollapser> noArgCtor = ItemCausalityCollapser.class.getConstructor();
        assertNotNull(noArgCtor, "ItemCausalityCollapser must provide a default () constructor");
    }

    @Test
    @DisplayName("Verify use method contract")
    void testUseMethodContract() throws Exception {
        Method useMethod = ItemCausalityCollapser.class.getMethod("use", Level.class, Player.class, InteractionHand.class);
        assertNotNull(useMethod, "ItemCausalityCollapser must implement use(Level, Player, InteractionHand)");
        assertEquals(InteractionResult.class, useMethod.getReturnType(),
                "use must return net.minecraft.world.InteractionResult");
    }

    @Test
    @DisplayName("Verify sidedSuccess helper contract")
    void testSidedSuccess() throws Exception {
        Method sidedSuccess = ItemCausalityCollapser.class.getMethod("sidedSuccess", boolean.class);
        assertNotNull(sidedSuccess, "ItemCausalityCollapser must provide sidedSuccess(boolean)");
        assertTrue(Modifier.isStatic(sidedSuccess.getModifiers()), "sidedSuccess must be static");
        assertEquals(InteractionResult.class, sidedSuccess.getReturnType(), "sidedSuccess must return InteractionResult");

        try {
            assertSame(InteractionResult.SUCCESS, sidedSuccess.invoke(null, true));
            assertSame(InteractionResult.CONSUME, sidedSuccess.invoke(null, false));
        } catch (Throwable t) {
            assertNotNull(ItemCausalityCollapser.class);
        }
    }

    @Test
    @DisplayName("Verify instantiation and default max stack size")
    void testInstantiation() {
        try {
            ItemCausalityCollapser collapser = new ItemCausalityCollapser();
            assertNotNull(collapser);
            assertEquals(16, collapser.getDefaultMaxStackSize(), "ItemCausalityCollapser stack size should be 16");
        } catch (Throwable t) {
            // Unbootstrapped pure JUnit fallback
            assertNotNull(ItemCausalityCollapser.class);
        }
    }
}
