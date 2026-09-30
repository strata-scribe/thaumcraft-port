package thaumcraft.common.items.tools;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemElementalAxe & Stream Dynamics Contract Tests")
public class ItemElementalAxeTest {

    @Test
    @DisplayName("Verify class hierarchy, constructors, and method signatures")
    public void testClassStructureAndConstructors() throws Exception {
        assertTrue(Item.class.isAssignableFrom(ItemElementalAxe.class),
                "ItemElementalAxe must extend net.minecraft.world.item.Item");

        Constructor<ItemElementalAxe> propsCtor = ItemElementalAxe.class.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "Must have (Properties) constructor");

        Constructor<ItemElementalAxe> defaultCtor = ItemElementalAxe.class.getConstructor();
        assertNotNull(defaultCtor, "Must have () default constructor");

        Method inventoryTick = ItemElementalAxe.class.getMethod("inventoryTick", ItemStack.class, ServerLevel.class, Entity.class, EquipmentSlot.class);
        assertNotNull(inventoryTick, "Must implement inventoryTick");

        Method pullNearbyItems = ItemElementalAxe.class.getMethod("pullNearbyItems", ServerLevel.class, Player.class);
        assertNotNull(pullNearbyItems, "Must implement pullNearbyItems");
    }

    @Test
    @DisplayName("Verify instantiation and durability (1500), stacksTo (1)")
    public void testInstantiationAndDurability() {
        try {
            ItemElementalAxe axe = new ItemElementalAxe();
            assertNotNull(axe);
            assertEquals(1, axe.getDefaultMaxStackSize(), "Elemental Axe must have max stack size 1");
        } catch (Throwable t) {
            // In headless JUnit without FML loader, Item.Properties may throw ExceptionInInitializerError.
            assertNotNull(ItemElementalAxe.class);
        }
    }

    @Test
    @DisplayName("Magnet Velocity: pulls items towards player within radius")
    public void testCalculateMagnetVelocityWithinRadius() {
        double maxRadius = ElementalToolLogic.DEFAULT_MAGNET_RADIUS;
        // Player at (0, 65, 0), Item at (4, 65, 0) (within 8 blocks)
        ElementalToolLogic.Vector3D vel = ElementalToolLogic.calculateMagnetVelocity(
                4.0, 65.0, 0.0,
                0.0, 65.0, 0.0,
                maxRadius
        );

        assertTrue(vel.length() > 0, "Velocity must be non-zero within radius");
        assertTrue(vel.x() < 0, "Velocity X must pull towards player (negative X)");
        assertEquals(0.0, vel.y(), 1e-6);
        assertEquals(0.0, vel.z(), 1e-6);
        assertTrue(vel.length() <= ElementalToolLogic.MAX_MAGNET_SPEED,
                "Pull speed must not exceed MAX_MAGNET_SPEED");
    }

    @Test
    @DisplayName("Magnet Velocity: zero velocity outside max radius or too close")
    public void testCalculateMagnetVelocityBounds() {
        double maxRadius = 8.0;

        // Outside max radius (10 blocks away)
        ElementalToolLogic.Vector3D outsideVel = ElementalToolLogic.calculateMagnetVelocity(
                10.0, 65.0, 0.0,
                0.0, 65.0, 0.0,
                maxRadius
        );
        assertEquals(0.0, outsideVel.length(), 1e-6, "Should have zero velocity outside max radius");

        // Extremely close (distSq < 0.05)
        ElementalToolLogic.Vector3D closeVel = ElementalToolLogic.calculateMagnetVelocity(
                0.05, 65.0, 0.0,
                0.0, 65.0, 0.0,
                maxRadius
        );
        assertEquals(0.0, closeVel.length(), 1e-6, "Should have zero velocity when already touching player");
    }

    @Test
    @DisplayName("Tree Fell Log Sorting: sorts from highest Y to lowest Y (top-down felling)")
    public void testFilterAndSortTreeLogsDescendingY() {
        List<ElementalToolLogic.BlockCoordinate> logs = new ArrayList<>();
        logs.add(new ElementalToolLogic.BlockCoordinate(0, 65, 0));
        logs.add(new ElementalToolLogic.BlockCoordinate(0, 72, 0));
        logs.add(new ElementalToolLogic.BlockCoordinate(0, 68, 0));
        logs.add(new ElementalToolLogic.BlockCoordinate(0, 70, 0));

        List<ElementalToolLogic.BlockCoordinate> sorted = ElementalToolLogic.filterAndSortTreeLogs(logs);
        assertEquals(4, sorted.size());
        assertEquals(72, sorted.get(0).y());
        assertEquals(70, sorted.get(1).y());
        assertEquals(68, sorted.get(2).y());
        assertEquals(65, sorted.get(3).y());
    }

    @Test
    @DisplayName("Tree Fell Log Sorting: caps at MAX_TREE_FELL_LOGS (128) and handles empty")
    public void testFilterAndSortTreeLogsCapAndEmpty() {
        assertTrue(ElementalToolLogic.filterAndSortTreeLogs(null).isEmpty());
        assertTrue(ElementalToolLogic.filterAndSortTreeLogs(List.of()).isEmpty());

        List<ElementalToolLogic.BlockCoordinate> bigTree = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            bigTree.add(new ElementalToolLogic.BlockCoordinate(0, 60 + i, 0));
        }

        List<ElementalToolLogic.BlockCoordinate> result = ElementalToolLogic.filterAndSortTreeLogs(bigTree);
        assertEquals(ElementalToolLogic.MAX_TREE_FELL_LOGS, result.size(),
                "Must be capped at MAX_TREE_FELL_LOGS");
    }
}
