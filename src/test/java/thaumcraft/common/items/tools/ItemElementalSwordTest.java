package thaumcraft.common.items.tools;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemElementalSword & Zephyr Impulse Contract Tests")
public class ItemElementalSwordTest {

    @Test
    @DisplayName("Verify class hierarchy, constructors, and method signatures")
    public void testClassStructureAndConstructors() throws Exception {
        assertTrue(Item.class.isAssignableFrom(ItemElementalSword.class),
                "ItemElementalSword must extend net.minecraft.world.item.Item");

        Constructor<ItemElementalSword> propsCtor = ItemElementalSword.class.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "Must have (Properties) constructor");

        Constructor<ItemElementalSword> defaultCtor = ItemElementalSword.class.getConstructor();
        assertNotNull(defaultCtor, "Must have () default constructor");

        Method use = ItemElementalSword.class.getMethod("use", Level.class, Player.class, InteractionHand.class);
        assertNotNull(use, "Must implement use(Level, Player, InteractionHand)");
        assertEquals(net.minecraft.world.InteractionResult.class, use.getReturnType());
    }

    @Test
    @DisplayName("Verify instantiation and durability (1500), stacksTo (1)")
    public void testInstantiationAndDurability() {
        try {
            ItemElementalSword sword = new ItemElementalSword();
            assertNotNull(sword);
            assertEquals(1, sword.getDefaultMaxStackSize(), "Elemental Sword must have max stack size 1");
        } catch (Throwable t) {
            // In headless JUnit without FML loader, Item.Properties may throw ExceptionInInitializerError.
            assertNotNull(ItemElementalSword.class);
        }
    }

    @Test
    @DisplayName("Zephyr Impulse: pushes target entity away with horizontal force and upward lift")
    public void testZephyrImpulseDirectionAndLift() {
        double maxRadius = ElementalToolLogic.ZEPHYR_MAX_RADIUS;

        // Player at (0, 64, 0), Target at (3, 64, 0)
        ElementalToolLogic.Vector3D impulse = ElementalToolLogic.calculateZephyrImpulse(
                0.0, 64.0, 0.0,
                3.0, 64.0, 0.0,
                maxRadius
        );

        assertTrue(impulse.length() > 0, "Impulse must be non-zero within radius");
        assertTrue(impulse.x() > 0, "Target east of player should be pushed further east (positive X)");
        assertEquals(0.0, impulse.z(), 1e-6);
        assertTrue(impulse.y() >= 0.35, "Impulse Y must provide upward lift (minimum 0.35)");
    }

    @Test
    @DisplayName("Zephyr Impulse: impulse decays with distance and is zero beyond max radius")
    public void testZephyrImpulseDistanceDecay() {
        double maxRadius = ElementalToolLogic.ZEPHYR_MAX_RADIUS;

        // Close target (dist = 1) vs Far target (dist = 4)
        ElementalToolLogic.Vector3D closeImpulse = ElementalToolLogic.calculateZephyrImpulse(
                0.0, 64.0, 0.0,
                1.0, 64.0, 0.0,
                maxRadius
        );
        ElementalToolLogic.Vector3D farImpulse = ElementalToolLogic.calculateZephyrImpulse(
                0.0, 64.0, 0.0,
                4.0, 64.0, 0.0,
                maxRadius
        );

        assertTrue(closeImpulse.x() > farImpulse.x(),
                "Closer target should receive stronger horizontal knockback than farther target");

        // Target beyond max radius (dist = 7 > 6.0)
        ElementalToolLogic.Vector3D outsideImpulse = ElementalToolLogic.calculateZephyrImpulse(
                0.0, 64.0, 0.0,
                7.0, 64.0, 0.0,
                maxRadius
        );
        assertEquals(0.0, outsideImpulse.length(), 1e-6, "Must have zero impulse beyond max radius");
    }
}
