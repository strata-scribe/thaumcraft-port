package thaumcraft.api.crafting;

import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InfusionRecipeCodecLogicTest {

    @Test
    public void testRecipeDataStructure() {
        String research = "TEST_RESEARCH";
        String output = "OUTPUT_ITEM";
        int instability = 5;
        Map<String, Integer> aspects = Map.of("ignis", 10, "terra", 5);
        String centralItem = "CENTRAL_ITEM";
        List<String> recipe = List.of("PEDESTAL_ITEM_1", "PEDESTAL_ITEM_2");

        InfusionRecipeCodecLogic.RecipeData<String, String> data = new InfusionRecipeCodecLogic.RecipeData<>(
            research, output, instability, aspects, centralItem, recipe
        );

        assertNotNull(data);
        assertEquals(research, data.getResearch());
        assertEquals(output, data.getOutput());
        assertEquals(instability, data.getInstability());
        assertEquals(aspects, data.getAspects());
        assertEquals(centralItem, data.getCentralItem());
        assertEquals(recipe, data.getRecipe());

        assertEquals(10, data.getAspects().get("ignis"));
        assertEquals(5, data.getAspects().get("terra"));
        assertEquals(2, data.getRecipe().size());
        assertEquals("PEDESTAL_ITEM_1", data.getRecipe().get(0));
    }

}
