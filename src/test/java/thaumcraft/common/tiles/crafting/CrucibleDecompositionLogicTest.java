package thaumcraft.common.tiles.crafting;

import org.junit.jupiter.api.Test;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CrucibleDecompositionLogicTest {

    @Test
    public void testCalculateDecomposition_NullInput() {
        Map<String, Integer> result = CrucibleDecompositionLogic.calculateDecomposition(null);
        assertNotNull(result, "Result should not be null even when input is null.");
        assertTrue(result.isEmpty(), "Result should be empty for null input.");
    }

    @Test
    public void testCalculateDecomposition_EmptyInput() {
        Map<String, Integer> inputMap = new LinkedHashMap<>();
        Map<String, Integer> result = CrucibleDecompositionLogic.calculateDecomposition(inputMap);
        assertNotNull(result, "Result should not be null.");
        assertTrue(result.isEmpty(), "Result should be empty for empty input.");
    }

    @Test
    public void testCalculateDecomposition_ValidAspects() {
        Map<String, Integer> inputMap = new LinkedHashMap<>();
        inputMap.put("terra", 5);
        inputMap.put("aer", 2);
        inputMap.put("ignis", 10);

        Map<String, Integer> result = CrucibleDecompositionLogic.calculateDecomposition(inputMap);

        assertNotNull(result, "Result should not be null.");
        assertEquals(3, result.size(), "Result should contain exactly 3 aspects.");

        assertTrue(result.containsKey("terra"));
        assertEquals(5, result.get("terra"));

        assertTrue(result.containsKey("aer"));
        assertEquals(2, result.get("aer"));

        assertTrue(result.containsKey("ignis"));
        assertEquals(10, result.get("ignis"));
    }
}
