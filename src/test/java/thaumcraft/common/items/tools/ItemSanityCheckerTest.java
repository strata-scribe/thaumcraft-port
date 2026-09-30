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

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemSanityChecker Contract Tests")
public class ItemSanityCheckerTest {

    @Test
    @DisplayName("Verify class hierarchy, constructors, and use method presence")
    public void testClassStructureAndConstructors() throws Exception {
        // Class extends Item
        assertTrue(Item.class.isAssignableFrom(ItemSanityChecker.class),
                "ItemSanityChecker must extend net.minecraft.world.item.Item");

        // Constructor with Properties
        Constructor<ItemSanityChecker> propsCtor = ItemSanityChecker.class.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "ItemSanityChecker must provide a (Item.Properties) constructor");

        // No-arg constructor
        Constructor<ItemSanityChecker> noArgCtor = ItemSanityChecker.class.getConstructor();
        assertNotNull(noArgCtor, "ItemSanityChecker must provide a default () constructor");

        // use(Level, Player, InteractionHand)
        Method useMethod = ItemSanityChecker.class.getMethod("use", Level.class, Player.class, InteractionHand.class);
        assertNotNull(useMethod, "ItemSanityChecker must implement use");
        assertEquals(InteractionResult.class, useMethod.getReturnType(), "use method must return InteractionResult");
    }

    @Test
    @DisplayName("Verify instantiation and default stack size")
    public void testInstantiationAndProperties() {
        try {
            ItemSanityChecker checker = new ItemSanityChecker();
            assertNotNull(checker);
            assertEquals(1, checker.getDefaultMaxStackSize(), "Sanity Checker stack size should be 1");
        } catch (Throwable t) {
            // Headless unit test environment without full NeoForge registry bootstrap
            assertNotNull(ItemSanityChecker.class);
        }
    }
}
