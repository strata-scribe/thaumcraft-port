package thaumcraft.api.crafting;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

public class CrucibleRecipeJsonLogicTest {

    @Test
    public void testSuccessfulValidation() {
        Map<String, Integer> aspects = new HashMap<>();
        aspects.put("terra", 5);
        aspects.put("ignis", 2);

        CrucibleRecipeJsonLogic.CrucibleRecipeData<String, String> data =
            new CrucibleRecipeJsonLogic.CrucibleRecipeData<>("RESEARCH_KEY", "GROUP", "RESULT", "CATALYST", aspects);

        assertEquals("RESEARCH_KEY", data.getResearch());
        assertEquals("GROUP", data.getGroup());
        assertEquals("RESULT", data.getResult());
        assertEquals("CATALYST", data.getCatalyst());
        assertEquals(5, data.getAspects().get("terra"));
        assertEquals(2, data.getAspects().get("ignis"));
    }

    @Test
    public void testMissingCatalystThrows() {
        Map<String, Integer> aspects = new HashMap<>();
        aspects.put("terra", 5);

        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            new CrucibleRecipeJsonLogic.CrucibleRecipeData<>("RESEARCH_KEY", "GROUP", "RESULT", null, aspects);
        });
        assertEquals("Crucible recipe must have a catalyst", e.getMessage());
    }

    @Test
    public void testMissingResultThrows() {
        Map<String, Integer> aspects = new HashMap<>();
        aspects.put("terra", 5);

        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            new CrucibleRecipeJsonLogic.CrucibleRecipeData<>("RESEARCH_KEY", "GROUP", null, "CATALYST", aspects);
        });
        assertEquals("Crucible recipe must have a result", e.getMessage());
    }

    @Test
    public void testNullAspectsThrows() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            new CrucibleRecipeJsonLogic.CrucibleRecipeData<>("RESEARCH_KEY", "GROUP", "RESULT", "CATALYST", null);
        });
        assertEquals("Crucible recipe must have at least one aspect", e.getMessage());
    }

    @Test
    public void testEmptyAspectsThrows() {
        Map<String, Integer> aspects = new HashMap<>();

        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            new CrucibleRecipeJsonLogic.CrucibleRecipeData<>("RESEARCH_KEY", "GROUP", "RESULT", "CATALYST", aspects);
        });
        assertEquals("Crucible recipe must have at least one aspect", e.getMessage());
    }

    @Test
    public void testZeroAspectAmountThrows() {
        Map<String, Integer> aspects = new HashMap<>();
        aspects.put("terra", 0);

        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            new CrucibleRecipeJsonLogic.CrucibleRecipeData<>("RESEARCH_KEY", "GROUP", "RESULT", "CATALYST", aspects);
        });
        assertEquals("Crucible recipe aspect amounts must be greater than zero", e.getMessage());
    }

    @Test
    public void testNegativeAspectAmountThrows() {
        Map<String, Integer> aspects = new HashMap<>();
        aspects.put("terra", -5);

        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            new CrucibleRecipeJsonLogic.CrucibleRecipeData<>("RESEARCH_KEY", "GROUP", "RESULT", "CATALYST", aspects);
        });
        assertEquals("Crucible recipe aspect amounts must be greater than zero", e.getMessage());
    }

    @Test
    public void testNullAspectAmountThrows() {
        Map<String, Integer> aspects = new HashMap<>();
        aspects.put("terra", null);

        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            new CrucibleRecipeJsonLogic.CrucibleRecipeData<>("RESEARCH_KEY", "GROUP", "RESULT", "CATALYST", aspects);
        });
        assertEquals("Crucible recipe aspect amounts must be greater than zero", e.getMessage());
    }
}
