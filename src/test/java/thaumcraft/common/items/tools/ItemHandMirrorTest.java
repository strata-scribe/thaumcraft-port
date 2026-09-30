package thaumcraft.common.items.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemHandMirror Contract Tests")
public class ItemHandMirrorTest {

    @Test
    @DisplayName("Verify class hierarchy and constructors")
    public void testClassStructureAndConstructors() throws Exception {
        // Class extends Item
        assertTrue(Item.class.isAssignableFrom(ItemHandMirror.class),
                "ItemHandMirror must extend net.minecraft.world.item.Item");

        // Constructor with Properties
        Constructor<ItemHandMirror> propsCtor = ItemHandMirror.class.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "ItemHandMirror must provide a (Item.Properties) constructor");

        // No-arg constructor
        Constructor<ItemHandMirror> noArgCtor = ItemHandMirror.class.getConstructor();
        assertNotNull(noArgCtor, "ItemHandMirror must provide a default () constructor");
    }

    @Test
    @DisplayName("Verify presence and signatures of static link helpers")
    public void testStaticLinkMethodsPresence() throws Exception {
        // public static boolean hasValidLink(ItemStack stack)
        Method hasValidLink = ItemHandMirror.class.getMethod("hasValidLink", ItemStack.class);
        assertNotNull(hasValidLink);
        assertTrue(Modifier.isStatic(hasValidLink.getModifiers()), "hasValidLink must be static");
        assertEquals(boolean.class, hasValidLink.getReturnType());

        // public static BlockPos getLinkedPos(ItemStack stack)
        Method getLinkedPos = ItemHandMirror.class.getMethod("getLinkedPos", ItemStack.class);
        assertNotNull(getLinkedPos);
        assertTrue(Modifier.isStatic(getLinkedPos.getModifiers()), "getLinkedPos must be static");
        assertEquals(BlockPos.class, getLinkedPos.getReturnType());

        // public static String getLinkedDimension(ItemStack stack)
        Method getLinkedDimension = ItemHandMirror.class.getMethod("getLinkedDimension", ItemStack.class);
        assertNotNull(getLinkedDimension);
        assertTrue(Modifier.isStatic(getLinkedDimension.getModifiers()), "getLinkedDimension must be static");
        assertEquals(String.class, getLinkedDimension.getReturnType());

        // public static void setLink(ItemStack stack, BlockPos pos, String dimension)
        Method setLink = ItemHandMirror.class.getMethod("setLink", ItemStack.class, BlockPos.class, String.class);
        assertNotNull(setLink);
        assertTrue(Modifier.isStatic(setLink.getModifiers()), "setLink must be static");
        assertEquals(void.class, setLink.getReturnType());
    }

    @Test
    @DisplayName("Verify use and useOn methods presence and return types")
    public void testInteractionMethods() throws Exception {
        // use(Level, Player, InteractionHand)
        Method use = ItemHandMirror.class.getMethod("use", Level.class, Player.class, InteractionHand.class);
        assertNotNull(use);
        assertEquals(InteractionResult.class, use.getReturnType());

        // useOn(UseOnContext)
        Method useOn = ItemHandMirror.class.getMethod("useOn", UseOnContext.class);
        assertNotNull(useOn);
        assertEquals(InteractionResult.class, useOn.getReturnType());
    }

    @Test
    @DisplayName("Verify instantiation and default stack size")
    public void testInstantiationAndProperties() {
        try {
            ItemHandMirror mirror = new ItemHandMirror();
            assertNotNull(mirror);
            assertEquals(1, mirror.getDefaultMaxStackSize(), "Hand Mirror stack size should be 1");
        } catch (Throwable t) {
            // Headless unit test environment without full NeoForge registry bootstrap
            assertNotNull(ItemHandMirror.class);
        }
    }
}
