package thaumcraft.common.lib.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import thaumcraft.common.lib.crafting.InfusionRecipeBuilderLogic.InfusionRecipeData;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class InfusionRecipeJsonLogicTest {

    @Test
    public void testToJson() {
        Map<String, Integer> aspects = new LinkedHashMap<>();
        aspects.put("aer", 20);
        aspects.put("ignis", 10);

        List<String> ingredients = List.of("thaumcraft:fabric", "minecraft:feather");

        InfusionRecipeData data = new InfusionRecipeData(
            "boots_traveller",
            "BOOTS_TRAVELLER",
            "minecraft:leather_boots",
            "thaumcraft:traveller_boots",
            4,
            aspects,
            ingredients
        );

        JsonObject json = InfusionRecipeJsonLogic.toJson(data);

        assertEquals("thaumcraft:infusion", json.get("type").getAsString());
        assertEquals("BOOTS_TRAVELLER", json.get("research").getAsString());
        assertEquals("thaumcraft:traveller_boots", json.get("result").getAsString());
        assertEquals("minecraft:leather_boots", json.get("central_item").getAsString());
        assertEquals(4, json.get("instability").getAsInt());

        JsonObject aspectsJson = json.getAsJsonObject("aspects");
        assertEquals(2, aspectsJson.size());
        assertEquals(20, aspectsJson.get("aer").getAsInt());
        assertEquals(10, aspectsJson.get("ignis").getAsInt());

        JsonArray recipeJson = json.getAsJsonArray("recipe");
        assertEquals(2, recipeJson.size());
        assertEquals("thaumcraft:fabric", recipeJson.get(0).getAsString());
        assertEquals("minecraft:feather", recipeJson.get(1).getAsString());
    }

    @Test
    public void testFromJsonValid() {
        JsonObject json = new JsonObject();
        json.addProperty("research", "BOOTS_TRAVELLER");
        json.addProperty("result", "thaumcraft:traveller_boots");
        json.addProperty("central_item", "minecraft:leather_boots");
        json.addProperty("instability", 4);

        JsonObject aspectsJson = new JsonObject();
        aspectsJson.addProperty("aer", 20);
        aspectsJson.addProperty("ignis", 10);
        json.add("aspects", aspectsJson);

        JsonArray recipeJson = new JsonArray();
        recipeJson.add("thaumcraft:fabric");
        recipeJson.add("minecraft:feather");
        json.add("recipe", recipeJson);

        InfusionRecipeData data = InfusionRecipeJsonLogic.fromJson(json, "boots_traveller");

        assertEquals("boots_traveller", data.name);
        assertEquals("BOOTS_TRAVELLER", data.research);
        assertEquals("thaumcraft:traveller_boots", data.outputItem);
        assertEquals("minecraft:leather_boots", data.centralItem);
        assertEquals(4, data.instability);

        assertEquals(2, data.aspects.size());
        assertEquals(20, data.aspects.get("aer"));
        assertEquals(10, data.aspects.get("ignis"));

        assertEquals(2, data.ingredients.size());
        assertEquals("thaumcraft:fabric", data.ingredients.get(0));
        assertEquals("minecraft:feather", data.ingredients.get(1));
    }

    @Test
    public void testFromJsonMissingResearch() {
        JsonObject json = new JsonObject();
        json.addProperty("result", "thaumcraft:traveller_boots");
        json.addProperty("central_item", "minecraft:leather_boots");
        json.addProperty("instability", 4);
        json.add("aspects", new JsonObject());
        json.add("recipe", new JsonArray());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            InfusionRecipeJsonLogic.fromJson(json, "test");
        });
        assertEquals("Recipe is missing 'research'", e.getMessage());
    }

    @Test
    public void testFromJsonNegativeInstability() {
        JsonObject json = new JsonObject();
        json.addProperty("research", "TEST");
        json.addProperty("result", "item");
        json.addProperty("central_item", "item");
        json.addProperty("instability", -1);
        json.add("aspects", new JsonObject());
        json.add("recipe", new JsonArray());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            InfusionRecipeJsonLogic.fromJson(json, "test");
        });
        assertEquals("Instability cannot be negative", e.getMessage());
    }

    @Test
    public void testFromJsonEmptyAspects() {
        JsonObject json = new JsonObject();
        json.addProperty("research", "TEST");
        json.addProperty("result", "item");
        json.addProperty("central_item", "item");
        json.addProperty("instability", 1);

        JsonObject aspectsJson = new JsonObject();
        json.add("aspects", aspectsJson);

        json.add("recipe", new JsonArray());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            InfusionRecipeJsonLogic.fromJson(json, "test");
        });
        assertEquals("Recipe must have at least one aspect", e.getMessage());
    }

    @Test
    public void testFromJsonInvalidAspectAmount() {
        JsonObject json = new JsonObject();
        json.addProperty("research", "TEST");
        json.addProperty("result", "item");
        json.addProperty("central_item", "item");
        json.addProperty("instability", 1);

        JsonObject aspectsJson = new JsonObject();
        aspectsJson.addProperty("aer", 0);
        json.add("aspects", aspectsJson);

        json.add("recipe", new JsonArray());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            InfusionRecipeJsonLogic.fromJson(json, "test");
        });
        assertEquals("Aspect amount must be greater than 0", e.getMessage());
    }

    @Test
    public void testFromJsonEmptyRecipe() {
        JsonObject json = new JsonObject();
        json.addProperty("research", "TEST");
        json.addProperty("result", "item");
        json.addProperty("central_item", "item");
        json.addProperty("instability", 1);

        JsonObject aspectsJson = new JsonObject();
        aspectsJson.addProperty("aer", 10);
        json.add("aspects", aspectsJson);

        JsonArray recipeJson = new JsonArray();
        json.add("recipe", recipeJson);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            InfusionRecipeJsonLogic.fromJson(json, "test");
        });
        assertEquals("Recipe must have at least one ingredient in 'recipe'", e.getMessage());
    }
}
