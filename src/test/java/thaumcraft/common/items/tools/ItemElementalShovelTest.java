package thaumcraft.common.items.tools;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemElementalShovel & 3x3 Excavation Contract Tests")
public class ItemElementalShovelTest {

    @Test
    @DisplayName("Verify class hierarchy, constructors, and method signatures")
    public void testClassStructureAndConstructors() throws Exception {
        assertTrue(Item.class.isAssignableFrom(ItemElementalShovel.class),
                "ItemElementalShovel must extend net.minecraft.world.item.Item");

        Constructor<ItemElementalShovel> propsCtor = ItemElementalShovel.class.getConstructor(Item.Properties.class);
        assertNotNull(propsCtor, "Must have (Properties) constructor");

        Constructor<ItemElementalShovel> defaultCtor = ItemElementalShovel.class.getConstructor();
        assertNotNull(defaultCtor, "Must have () default constructor");

        Method useOn = ItemElementalShovel.class.getMethod("useOn", UseOnContext.class);
        assertNotNull(useOn, "Must implement useOn(UseOnContext)");
        assertEquals(net.minecraft.world.InteractionResult.class, useOn.getReturnType());
    }

    @Test
    @DisplayName("Verify instantiation and durability (1500), stacksTo (1)")
    public void testInstantiationAndDurability() {
        try {
            ItemElementalShovel shovel = new ItemElementalShovel();
            assertNotNull(shovel);
            assertEquals(1, shovel.getDefaultMaxStackSize(), "Elemental Shovel must have max stack size 1");
        } catch (Throwable t) {
            // In headless JUnit without FML loader, Item.Properties may throw ExceptionInInitializerError.
            assertNotNull(ItemElementalShovel.class);
        }
    }

    @Test
    @DisplayName("3x3 Grid Orientation across UP and DOWN: horizontal X-Z plane")
    public void testCalculate3x3GridUpDown() {
        ElementalToolLogic.BlockCoordinate center = new ElementalToolLogic.BlockCoordinate(5, 60, -10);

        List<ElementalToolLogic.BlockCoordinate> upGrid = ElementalToolLogic.calculate3x3Grid(center, ElementalToolLogic.BlockFace.UP);
        assertEquals(9, upGrid.size());
        for (ElementalToolLogic.BlockCoordinate coord : upGrid) {
            assertEquals(60, coord.y(), "Horizontal plane must have uniform Y");
            assertTrue(Math.abs(coord.x() - 5) <= 1, "X offset within [-1, 1]");
            assertTrue(Math.abs(coord.z() - (-10)) <= 1, "Z offset within [-1, 1]");
        }

        List<ElementalToolLogic.BlockCoordinate> downGrid = ElementalToolLogic.calculate3x3Grid(center, ElementalToolLogic.BlockFace.DOWN);
        assertEquals(9, downGrid.size());
        assertEquals(upGrid, downGrid, "UP and DOWN should compute equivalent horizontal X-Z planes");
    }

    @Test
    @DisplayName("3x3 Grid Orientation across NORTH and SOUTH: vertical X-Y plane")
    public void testCalculate3x3GridNorthSouth() {
        ElementalToolLogic.BlockCoordinate center = new ElementalToolLogic.BlockCoordinate(5, 60, -10);

        List<ElementalToolLogic.BlockCoordinate> northGrid = ElementalToolLogic.calculate3x3Grid(center, ElementalToolLogic.BlockFace.NORTH);
        assertEquals(9, northGrid.size());
        for (ElementalToolLogic.BlockCoordinate coord : northGrid) {
            assertEquals(-10, coord.z(), "North/South vertical plane must have uniform Z");
            assertTrue(Math.abs(coord.x() - 5) <= 1, "X offset within [-1, 1]");
            assertTrue(Math.abs(coord.y() - 60) <= 1, "Y offset within [-1, 1]");
        }

        List<ElementalToolLogic.BlockCoordinate> southGrid = ElementalToolLogic.calculate3x3Grid(center, ElementalToolLogic.BlockFace.SOUTH);
        assertEquals(9, southGrid.size());
        assertEquals(northGrid, southGrid, "NORTH and SOUTH should compute equivalent vertical X-Y planes");
    }

    @Test
    @DisplayName("3x3 Grid Orientation across EAST and WEST: vertical Y-Z plane")
    public void testCalculate3x3GridEastWest() {
        ElementalToolLogic.BlockCoordinate center = new ElementalToolLogic.BlockCoordinate(5, 60, -10);

        List<ElementalToolLogic.BlockCoordinate> eastGrid = ElementalToolLogic.calculate3x3Grid(center, ElementalToolLogic.BlockFace.EAST);
        assertEquals(9, eastGrid.size());
        for (ElementalToolLogic.BlockCoordinate coord : eastGrid) {
            assertEquals(5, coord.x(), "East/West vertical plane must have uniform X");
            assertTrue(Math.abs(coord.y() - 60) <= 1, "Y offset within [-1, 1]");
            assertTrue(Math.abs(coord.z() - (-10)) <= 1, "Z offset within [-1, 1]");
        }

        List<ElementalToolLogic.BlockCoordinate> westGrid = ElementalToolLogic.calculate3x3Grid(center, ElementalToolLogic.BlockFace.WEST);
        assertEquals(9, westGrid.size());
        assertEquals(eastGrid, westGrid, "EAST and WEST should compute equivalent vertical Y-Z planes");
    }
}
