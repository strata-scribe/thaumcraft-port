package thaumcraft.common.items.armor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CrimsonArmorLogic Unit Tests")
public class CrimsonArmorLogicTest {

    @Test
    @DisplayName("Verify Crimson warp calculation across armor families and piece counts")
    public void testCalculateCrimsonWarp() {
        // Praetor armor: 2 warp per piece
        assertEquals(0, CrimsonArmorLogic.calculateCrimsonWarp("praetor", 0));
        assertEquals(2, CrimsonArmorLogic.calculateCrimsonWarp("praetor", 1));
        assertEquals(4, CrimsonArmorLogic.calculateCrimsonWarp("praetor", 2));
        assertEquals(6, CrimsonArmorLogic.calculateCrimsonWarp("praetor", 3));
        assertEquals(8, CrimsonArmorLogic.calculateCrimsonWarp("PRAETOR", 4));
        assertEquals(0, CrimsonArmorLogic.calculateCrimsonWarp("praetor", -1));
        assertEquals(0, CrimsonArmorLogic.calculateCrimsonWarp("praetor", -5));

        // Plate armor: 1 warp per piece
        assertEquals(0, CrimsonArmorLogic.calculateCrimsonWarp("plate", 0));
        assertEquals(1, CrimsonArmorLogic.calculateCrimsonWarp("plate", 1));
        assertEquals(2, CrimsonArmorLogic.calculateCrimsonWarp("plate", 2));
        assertEquals(3, CrimsonArmorLogic.calculateCrimsonWarp("plate", 3));
        assertEquals(4, CrimsonArmorLogic.calculateCrimsonWarp("PLATE", 4));
        assertEquals(0, CrimsonArmorLogic.calculateCrimsonWarp("plate", -1));

        // Robe armor: 1 warp per piece
        assertEquals(0, CrimsonArmorLogic.calculateCrimsonWarp("robe", 0));
        assertEquals(1, CrimsonArmorLogic.calculateCrimsonWarp("robe", 1));
        assertEquals(2, CrimsonArmorLogic.calculateCrimsonWarp("robe", 2));
        assertEquals(3, CrimsonArmorLogic.calculateCrimsonWarp("robe", 3));
        assertEquals(4, CrimsonArmorLogic.calculateCrimsonWarp("ROBE", 4));
        assertEquals(0, CrimsonArmorLogic.calculateCrimsonWarp("robe", -2));

        // Unknown or null families: 0 warp
        assertEquals(0, CrimsonArmorLogic.calculateCrimsonWarp("unknown", 4));
        assertEquals(0, CrimsonArmorLogic.calculateCrimsonWarp("leather", 4));
        assertEquals(0, CrimsonArmorLogic.calculateCrimsonWarp(null, 4));
        assertEquals(0, CrimsonArmorLogic.calculateCrimsonWarp("", 4));
    }

    @Test
    @DisplayName("Verify Vis discount calculation and clamping")
    public void testCalculateVisDiscount() {
        assertEquals(0, CrimsonArmorLogic.calculateVisDiscount(0));
        assertEquals(5, CrimsonArmorLogic.calculateVisDiscount(1));
        assertEquals(10, CrimsonArmorLogic.calculateVisDiscount(2));
        assertEquals(15, CrimsonArmorLogic.calculateVisDiscount(3));
        assertEquals(20, CrimsonArmorLogic.calculateVisDiscount(4));

        // Negative pieces clamped to 0%
        assertEquals(0, CrimsonArmorLogic.calculateVisDiscount(-1));
        assertEquals(0, CrimsonArmorLogic.calculateVisDiscount(-10));

        // Extreme piece count clamped to 100%
        assertEquals(100, CrimsonArmorLogic.calculateVisDiscount(20));
        assertEquals(100, CrimsonArmorLogic.calculateVisDiscount(25));
        assertEquals(100, CrimsonArmorLogic.calculateVisDiscount(50));
    }

    @Test
    @DisplayName("Verify Cultist disguise status requiring at least 3 pieces")
    public void testIsCultistDisguised() {
        assertFalse(CrimsonArmorLogic.isCultistDisguised(-1));
        assertFalse(CrimsonArmorLogic.isCultistDisguised(0));
        assertFalse(CrimsonArmorLogic.isCultistDisguised(1));
        assertFalse(CrimsonArmorLogic.isCultistDisguised(2));

        assertTrue(CrimsonArmorLogic.isCultistDisguised(3));
        assertTrue(CrimsonArmorLogic.isCultistDisguised(4));
        assertTrue(CrimsonArmorLogic.isCultistDisguised(5));
    }

    @Test
    @DisplayName("Verify Praetor leader status requiring both helm and chest")
    public void testIsPraetorLeader() {
        assertTrue(CrimsonArmorLogic.isPraetorLeader(true, true));
        assertFalse(CrimsonArmorLogic.isPraetorLeader(true, false));
        assertFalse(CrimsonArmorLogic.isPraetorLeader(false, true));
        assertFalse(CrimsonArmorLogic.isPraetorLeader(false, false));
    }

    @Test
    @DisplayName("Verify durability multipliers for armor families")
    public void testGetArmorDurabilityMultiplier() {
        assertEquals(30, CrimsonArmorLogic.getArmorDurabilityMultiplier("praetor"));
        assertEquals(30, CrimsonArmorLogic.getArmorDurabilityMultiplier("PRAETOR"));

        assertEquals(20, CrimsonArmorLogic.getArmorDurabilityMultiplier("plate"));
        assertEquals(20, CrimsonArmorLogic.getArmorDurabilityMultiplier("PLATE"));

        assertEquals(17, CrimsonArmorLogic.getArmorDurabilityMultiplier("robe"));
        assertEquals(17, CrimsonArmorLogic.getArmorDurabilityMultiplier("ROBE"));

        assertEquals(15, CrimsonArmorLogic.getArmorDurabilityMultiplier("unknown"));
        assertEquals(15, CrimsonArmorLogic.getArmorDurabilityMultiplier("leather"));
        assertEquals(15, CrimsonArmorLogic.getArmorDurabilityMultiplier(null));
        assertEquals(15, CrimsonArmorLogic.getArmorDurabilityMultiplier(""));
    }
}
