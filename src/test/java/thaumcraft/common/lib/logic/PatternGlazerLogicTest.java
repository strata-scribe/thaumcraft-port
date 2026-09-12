package thaumcraft.common.lib.logic;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PatternGlazerLogicTest {

    @Test
    public void testEncodeAspectPattern_ValidInputs() {
        String[] aspects = {"ignis", "aer", "terra"};
        String encoded = PatternGlazerLogic.encodeAspectPattern(aspects);
        assertEquals("ignis,aer,terra", encoded);
    }

    @Test
    public void testEncodeAspectPattern_WithNullElements() {
        String[] aspects = {"ignis", null, "terra"};
        String encoded = PatternGlazerLogic.encodeAspectPattern(aspects);
        assertEquals("ignis,,terra", encoded);
    }

    @Test
    public void testEncodeAspectPattern_EmptyOrNullArray() {
        assertEquals("", PatternGlazerLogic.encodeAspectPattern(new String[0]));
        assertEquals("", PatternGlazerLogic.encodeAspectPattern(null));
    }

    @Test
    public void testDecodeAspectPattern_ValidInputs() {
        String encoded = "ignis,aer,terra";
        String[] decoded = PatternGlazerLogic.decodeAspectPattern(encoded);
        assertArrayEquals(new String[]{"ignis", "aer", "terra"}, decoded);
    }

    @Test
    public void testDecodeAspectPattern_WithEmptyElements() {
        String encoded = "ignis,,terra";
        String[] decoded = PatternGlazerLogic.decodeAspectPattern(encoded);
        assertArrayEquals(new String[]{"ignis", "", "terra"}, decoded);
    }

    @Test
    public void testDecodeAspectPattern_EmptyOrNullString() {
        assertArrayEquals(new String[0], PatternGlazerLogic.decodeAspectPattern(""));
        assertArrayEquals(new String[0], PatternGlazerLogic.decodeAspectPattern(null));
        assertArrayEquals(new String[0], PatternGlazerLogic.decodeAspectPattern("   "));
    }

    @Test
    public void testValidateBlueprint_Valid() {
        String[] pattern = {"ignis", "aer", "terra", "aqua"};
        assertTrue(PatternGlazerLogic.validateBlueprint(2, 2, pattern));
    }

    @Test
    public void testValidateBlueprint_InvalidSize() {
        String[] pattern = {"ignis", "aer", "terra"};
        assertFalse(PatternGlazerLogic.validateBlueprint(2, 2, pattern));
    }

    @Test
    public void testValidateBlueprint_InvalidDimensions() {
        String[] pattern = {"ignis", "aer", "terra", "aqua"};
        assertFalse(PatternGlazerLogic.validateBlueprint(0, 2, pattern));
        assertFalse(PatternGlazerLogic.validateBlueprint(2, -1, pattern));
    }

    @Test
    public void testValidateBlueprint_NullPattern() {
        assertFalse(PatternGlazerLogic.validateBlueprint(2, 2, null));
    }

    @Test
    public void testCalculateVisRequirement_ValidCosts() {
        Map<String, Integer> costs = new HashMap<>();
        costs.put("ignis", 10);
        costs.put("aer", 5);
        costs.put("terra", 15);

        String[] pattern = {"ignis", "aer", "terra", "ignis"};
        int baseCost = 20;

        int totalCost = PatternGlazerLogic.calculateVisRequirement(pattern, costs, baseCost);
        assertEquals(60, totalCost); // 20 + 10 + 5 + 15 + 10
    }

    @Test
    public void testCalculateVisRequirement_UnknownAspects() {
        Map<String, Integer> costs = new HashMap<>();
        costs.put("ignis", 10);

        String[] pattern = {"ignis", "unknown", "aer"}; // "unknown" and "aer" have 0 cost
        int baseCost = 5;

        int totalCost = PatternGlazerLogic.calculateVisRequirement(pattern, costs, baseCost);
        assertEquals(15, totalCost); // 5 + 10 + 0 + 0
    }

    @Test
    public void testCalculateVisRequirement_NullOrEmptyPattern() {
        Map<String, Integer> costs = new HashMap<>();
        costs.put("ignis", 10);

        assertEquals(20, PatternGlazerLogic.calculateVisRequirement(null, costs, 20));
        assertEquals(20, PatternGlazerLogic.calculateVisRequirement(new String[0], costs, 20));
    }

    @Test
    public void testCalculateVisRequirement_NegativeResultReturnsZero() {
        Map<String, Integer> costs = new HashMap<>();
        costs.put("ignis", -10);

        String[] pattern = {"ignis", "ignis"};
        int baseCost = 5;

        int totalCost = PatternGlazerLogic.calculateVisRequirement(pattern, costs, baseCost);
        assertEquals(0, totalCost); // 5 - 10 - 10 = -15 -> 0
    }
}
