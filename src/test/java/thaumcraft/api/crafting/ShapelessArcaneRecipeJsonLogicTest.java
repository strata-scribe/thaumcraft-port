package thaumcraft.api.crafting;

import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class ShapelessArcaneRecipeJsonLogicTest {

    @Test
    public void testValidRecipeData() {
        String group = "test_group";
        String research = "test_research";
        int vis = 10;
        Map<String, Integer> crystals = Map.of("aer", 2, "ignis", 1);
        List<String> ingredients = List.of("item1", "item2");
        String result = "result_item";

        ShapelessArcaneRecipeJsonLogic.RecipeData<String, String> data = new ShapelessArcaneRecipeJsonLogic.RecipeData<>(
            group, research, vis, crystals, ingredients, result
        );

        assertEquals(group, data.getGroup());
        assertEquals(research, data.getResearch());
        assertEquals(vis, data.getVis());
        assertEquals(2, data.getCrystals().size());
        assertEquals(2, data.getCrystals().get("aer"));
        assertEquals(1, data.getCrystals().get("ignis"));
        assertEquals(2, data.getIngredients().size());
        assertEquals("item1", data.getIngredients().get(0));
        assertEquals("item2", data.getIngredients().get(1));
        assertEquals(result, data.getResult());
    }

    @Test
    public void testValidRecipeDataNoGroupOrResearch() {
        int vis = 10;
        Map<String, Integer> crystals = Map.of("aer", 2);
        List<String> ingredients = List.of("item1");
        String result = "result_item";

        ShapelessArcaneRecipeJsonLogic.RecipeData<String, String> data = new ShapelessArcaneRecipeJsonLogic.RecipeData<>(
            null, null, vis, crystals, ingredients, result
        );

        assertEquals("", data.getGroup());
        assertEquals("", data.getResearch());
    }

    @Test
    public void testValidRecipeDataNoCrystals() {
        int vis = 5;
        List<String> ingredients = List.of("item1");
        String result = "result_item";

        ShapelessArcaneRecipeJsonLogic.RecipeData<String, String> data = new ShapelessArcaneRecipeJsonLogic.RecipeData<>(
            "group", "research", vis, null, ingredients, result
        );

        assertTrue(data.getCrystals().isEmpty());
    }

    @Test
    public void testNegativeVis() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ShapelessArcaneRecipeJsonLogic.RecipeData<>(
                "group", "research", -1, null, List.of("item1"), "result"
            );
        });
    }

    @Test
    public void testNullIngredients() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ShapelessArcaneRecipeJsonLogic.RecipeData<>(
                "group", "research", 10, null, null, "result"
            );
        });
    }

    @Test
    public void testEmptyIngredients() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ShapelessArcaneRecipeJsonLogic.RecipeData<>(
                "group", "research", 10, null, Collections.emptyList(), "result"
            );
        });
    }

    @Test
    public void testTooManyIngredients() {
        List<String> ingredients = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        assertThrows(IllegalArgumentException.class, () -> {
            new ShapelessArcaneRecipeJsonLogic.RecipeData<>(
                "group", "research", 10, null, ingredients, "result"
            );
        });
    }

    @Test
    public void testNullResult() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ShapelessArcaneRecipeJsonLogic.RecipeData<>(
                "group", "research", 10, null, List.of("item1"), null
            );
        });
    }

    @Test
    public void testInvalidCrystalAmount() {
        Map<String, Integer> crystals = Map.of("aer", 0);
        assertThrows(IllegalArgumentException.class, () -> {
            new ShapelessArcaneRecipeJsonLogic.RecipeData<>(
                "group", "research", 10, crystals, List.of("item1"), "result"
            );
        });
    }

    @Test
    public void testNegativeCrystalAmount() {
        Map<String, Integer> crystals = Map.of("aer", -5);
        assertThrows(IllegalArgumentException.class, () -> {
            new ShapelessArcaneRecipeJsonLogic.RecipeData<>(
                "group", "research", 10, crystals, List.of("item1"), "result"
            );
        });
    }
}
