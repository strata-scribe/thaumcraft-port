package thaumcraft.api.crafting.logic;

import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ShapedArcaneRecipeJsonLogicTest {

    @Test
    public void testRecipeData() {
        ShapedArcaneRecipeJsonLogic.RecipeData<String, String> data = new ShapedArcaneRecipeJsonLogic.RecipeData<>(
            "group",
            "RESEARCH",
            100,
            Map.of("aer", 1),
            "output",
            List.of(" A ", " A "),
            Map.of("A", "ing")
        );
        assertEquals("group", data.getGroup());
        assertEquals("RESEARCH", data.getResearch());
        assertEquals(100, data.getVis());
        assertEquals(1, data.getCrystals().get("aer").intValue());
        assertEquals("output", data.getOutput());
        assertEquals(2, data.getPattern().size());
        assertEquals("ing", data.getKey().get("A"));
    }

    @Test
    public void testValidateValidRecipe() {
        ShapedArcaneRecipeJsonLogic.RecipeData<String, String> data = new ShapedArcaneRecipeJsonLogic.RecipeData<>(
            "group",
            "RESEARCH",
            100,
            Map.of("aer", 1),
            "output",
            List.of(" A ", " B "),
            Map.of("A", "ingA", "B", "ingB")
        );

        ShapedArcaneRecipeJsonLogic logic = new ShapedArcaneRecipeJsonLogic();
        assertDoesNotThrow(() -> logic.validate(data));
    }

    @Test
    public void testValidateMissingPattern() {
        ShapedArcaneRecipeJsonLogic.RecipeData<String, String> data = new ShapedArcaneRecipeJsonLogic.RecipeData<>(
            "group",
            "RESEARCH",
            100,
            Map.of("aer", 1),
            "output",
            null,
            Map.of("A", "ingA", "B", "ingB")
        );

        ShapedArcaneRecipeJsonLogic logic = new ShapedArcaneRecipeJsonLogic();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> logic.validate(data));
        assertEquals("Pattern cannot be empty", e.getMessage());
    }

    @Test
    public void testValidateEmptyPattern() {
        ShapedArcaneRecipeJsonLogic.RecipeData<String, String> data = new ShapedArcaneRecipeJsonLogic.RecipeData<>(
            "group",
            "RESEARCH",
            100,
            Map.of("aer", 1),
            "output",
            List.of(),
            Map.of("A", "ingA", "B", "ingB")
        );

        ShapedArcaneRecipeJsonLogic logic = new ShapedArcaneRecipeJsonLogic();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> logic.validate(data));
        assertEquals("Pattern cannot be empty", e.getMessage());
    }

    @Test
    public void testValidateTooManyRows() {
        ShapedArcaneRecipeJsonLogic.RecipeData<String, String> data = new ShapedArcaneRecipeJsonLogic.RecipeData<>(
            "group",
            "RESEARCH",
            100,
            Map.of("aer", 1),
            "output",
            List.of(" A ", " B ", " C ", " D "),
            Map.of("A", "ingA", "B", "ingB", "C", "ingC", "D", "ingD")
        );

        ShapedArcaneRecipeJsonLogic logic = new ShapedArcaneRecipeJsonLogic();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> logic.validate(data));
        assertEquals("Pattern cannot have more than 3 rows", e.getMessage());
    }

    @Test
    public void testValidateTooManyColumns() {
        ShapedArcaneRecipeJsonLogic.RecipeData<String, String> data = new ShapedArcaneRecipeJsonLogic.RecipeData<>(
            "group",
            "RESEARCH",
            100,
            Map.of("aer", 1),
            "output",
            List.of("ABCD"),
            Map.of("A", "ingA", "B", "ingB", "C", "ingC", "D", "ingD")
        );

        ShapedArcaneRecipeJsonLogic logic = new ShapedArcaneRecipeJsonLogic();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> logic.validate(data));
        assertEquals("Pattern rows must have length between 1 and 3", e.getMessage());
    }

    @Test
    public void testValidateEmptyRowLength() {
        ShapedArcaneRecipeJsonLogic.RecipeData<String, String> data = new ShapedArcaneRecipeJsonLogic.RecipeData<>(
            "group",
            "RESEARCH",
            100,
            Map.of("aer", 1),
            "output",
            List.of(""),
            Map.of("A", "ingA")
        );

        ShapedArcaneRecipeJsonLogic logic = new ShapedArcaneRecipeJsonLogic();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> logic.validate(data));
        assertEquals("Pattern rows must have length between 1 and 3", e.getMessage());
    }


    @Test
    public void testValidateInvalidKeyInPattern() {
        ShapedArcaneRecipeJsonLogic.RecipeData<String, String> data = new ShapedArcaneRecipeJsonLogic.RecipeData<>(
            "group",
            "RESEARCH",
            100,
            Map.of("aer", 1),
            "output",
            List.of(" C ", " B "),
            Map.of("A", "ingA", "B", "ingB")
        );

        ShapedArcaneRecipeJsonLogic logic = new ShapedArcaneRecipeJsonLogic();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> logic.validate(data));
        assertEquals("Pattern contains unknown key: C", e.getMessage());
    }

    @Test
    public void testValidateMissingKeyDefinition() {
        ShapedArcaneRecipeJsonLogic.RecipeData<String, String> data = new ShapedArcaneRecipeJsonLogic.RecipeData<>(
            "group",
            "RESEARCH",
            100,
            Map.of("aer", 1),
            "output",
            List.of(" A ", " B "),
            null
        );

        ShapedArcaneRecipeJsonLogic logic = new ShapedArcaneRecipeJsonLogic();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> logic.validate(data));
        assertEquals("Key mapping cannot be null or empty", e.getMessage());
    }

    @Test
    public void testValidateEmptyKeyDefinition() {
        ShapedArcaneRecipeJsonLogic.RecipeData<String, String> data = new ShapedArcaneRecipeJsonLogic.RecipeData<>(
            "group",
            "RESEARCH",
            100,
            Map.of("aer", 1),
            "output",
            List.of(" A ", " B "),
            Map.of()
        );

        ShapedArcaneRecipeJsonLogic logic = new ShapedArcaneRecipeJsonLogic();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> logic.validate(data));
        assertEquals("Key mapping cannot be null or empty", e.getMessage());
    }

    @Test
    public void testValidateUnusedKeyDefinition() {
        ShapedArcaneRecipeJsonLogic.RecipeData<String, String> data = new ShapedArcaneRecipeJsonLogic.RecipeData<>(
            "group",
            "RESEARCH",
            100,
            Map.of("aer", 1),
            "output",
            List.of(" A "),
            Map.of("A", "ingA", "B", "ingB")
        );

        ShapedArcaneRecipeJsonLogic logic = new ShapedArcaneRecipeJsonLogic();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> logic.validate(data));
        assertEquals("Key defined but not used in pattern: B", e.getMessage());
    }

    @Test
    public void testValidateInconsistentRowLengths() {
        ShapedArcaneRecipeJsonLogic.RecipeData<String, String> data = new ShapedArcaneRecipeJsonLogic.RecipeData<>(
            "group",
            "RESEARCH",
            100,
            Map.of("aer", 1),
            "output",
            List.of(" A ", " B"),
            Map.of("A", "ingA", "B", "ingB")
        );

        ShapedArcaneRecipeJsonLogic logic = new ShapedArcaneRecipeJsonLogic();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> logic.validate(data));
        assertEquals("Pattern rows must all have the same length", e.getMessage());
    }
}
