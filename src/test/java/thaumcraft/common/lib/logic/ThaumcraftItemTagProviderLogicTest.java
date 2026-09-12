package thaumcraft.common.lib.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ThaumcraftItemTagProviderLogicTest {

    @Test
    public void testValidAspects() {
        assertEquals("thaumcraft:aspects/ignis", ThaumcraftItemTagProviderLogic.getTagForItem("aspects", "ignis"));
        assertEquals("thaumcraft:aspects/aer", ThaumcraftItemTagProviderLogic.getTagForItem("aspects", "aer"));
        assertEquals("thaumcraft:aspects/terra", ThaumcraftItemTagProviderLogic.getTagForItem("aspects", "Terra"));
    }

    @Test
    public void testInvalidAspects() {
        assertThrows(IllegalArgumentException.class, () -> {
            ThaumcraftItemTagProviderLogic.getTagForItem("aspects", "invalid_aspect");
        });
    }

    @Test
    public void testValidCrystals() {
        assertEquals("c:crystals/ordo", ThaumcraftItemTagProviderLogic.getTagForItem("crystals", "ordo"));
        assertEquals("c:crystals/perditio", ThaumcraftItemTagProviderLogic.getTagForItem("crystals", "Perditio"));
    }

    @Test
    public void testInvalidCrystals() {
        assertThrows(IllegalArgumentException.class, () -> {
            ThaumcraftItemTagProviderLogic.getTagForItem("crystals", "not_a_crystal");
        });
    }

    @Test
    public void testValidIngots() {
        assertEquals("c:ingots/thaumium", ThaumcraftItemTagProviderLogic.getTagForItem("ingots", "thaumium"));
        assertEquals("c:ingots/void", ThaumcraftItemTagProviderLogic.getTagForItem("ingots", "Void"));
        assertEquals("c:ingots/brass", ThaumcraftItemTagProviderLogic.getTagForItem("ingots", "BRASS"));
    }

    @Test
    public void testInvalidIngots() {
        assertThrows(IllegalArgumentException.class, () -> {
            ThaumcraftItemTagProviderLogic.getTagForItem("ingots", "iron");
        });
    }

    @Test
    public void testValidCurios() {
        assertEquals("thaumcraft:curios/arcane", ThaumcraftItemTagProviderLogic.getTagForItem("curios", "arcane"));
        assertEquals("thaumcraft:curios/eldritch", ThaumcraftItemTagProviderLogic.getTagForItem("curios", "eldritch"));
    }

    @Test
    public void testInvalidCurios() {
        assertThrows(IllegalArgumentException.class, () -> {
            ThaumcraftItemTagProviderLogic.getTagForItem("curios", "unknown");
        });
    }

    @Test
    public void testValidClusters() {
        assertEquals("c:clusters/iron", ThaumcraftItemTagProviderLogic.getTagForItem("clusters", "iron"));
        assertEquals("c:clusters/cinnabar", ThaumcraftItemTagProviderLogic.getTagForItem("clusters", "CINNABAR"));
    }

    @Test
    public void testInvalidClusters() {
        assertThrows(IllegalArgumentException.class, () -> {
            ThaumcraftItemTagProviderLogic.getTagForItem("clusters", "diamond");
        });
    }

    @Test
    public void testInvalidCategory() {
        assertThrows(IllegalArgumentException.class, () -> {
            ThaumcraftItemTagProviderLogic.getTagForItem("unknown_category", "iron");
        });
    }

    @Test
    public void testNullInputs() {
        assertThrows(IllegalArgumentException.class, () -> {
            ThaumcraftItemTagProviderLogic.getTagForItem(null, "iron");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ThaumcraftItemTagProviderLogic.getTagForItem("ingots", null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ThaumcraftItemTagProviderLogic.getTagForItem(null, null);
        });
    }
}
