package thaumcraft.common.lib.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import thaumcraft.common.lib.crafting.InfusionRecipeBuilderLogic.InfusionRecipeData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pure Java logic for serializing and deserializing InfusionRecipeData to/from JSON.
 */
public class InfusionRecipeJsonLogic {

    public static JsonObject toJson(InfusionRecipeData data) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "thaumcraft:infusion");
        json.addProperty("research", data.research);
        json.addProperty("result", data.outputItem);
        json.addProperty("instability", data.instability);

        json.addProperty("central_item", data.centralItem);

        JsonObject aspectsJson = new JsonObject();
        for (Map.Entry<String, Integer> entry : data.aspects.entrySet()) {
            aspectsJson.addProperty(entry.getKey(), entry.getValue());
        }
        json.add("aspects", aspectsJson);

        JsonArray recipeJson = new JsonArray();
        for (String ingredient : data.ingredients) {
            recipeJson.add(ingredient);
        }
        json.add("recipe", recipeJson);

        return json;
    }

    public static InfusionRecipeData fromJson(JsonObject json, String recipeName) {
        if (!json.has("research")) throw new IllegalArgumentException("Recipe is missing 'research'");
        if (!json.has("result")) throw new IllegalArgumentException("Recipe is missing 'result'");
        if (!json.has("central_item")) throw new IllegalArgumentException("Recipe is missing 'central_item'");
        if (!json.has("aspects")) throw new IllegalArgumentException("Recipe is missing 'aspects'");
        if (!json.has("recipe")) throw new IllegalArgumentException("Recipe is missing 'recipe'");
        if (!json.has("instability")) throw new IllegalArgumentException("Recipe is missing 'instability'");

        String research = json.get("research").getAsString();
        String result = json.get("result").getAsString();
        String centralItem = json.get("central_item").getAsString();
        int instability = json.get("instability").getAsInt();

        if (instability < 0) {
            throw new IllegalArgumentException("Instability cannot be negative");
        }

        JsonObject aspectsJson = json.getAsJsonObject("aspects");
        Map<String, Integer> aspects = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : aspectsJson.entrySet()) {
            int amount = entry.getValue().getAsInt();
            if (amount <= 0) {
                throw new IllegalArgumentException("Aspect amount must be greater than 0");
            }
            aspects.put(entry.getKey(), amount);
        }

        if (aspects.isEmpty()) {
            throw new IllegalArgumentException("Recipe must have at least one aspect");
        }

        JsonArray recipeJson = json.getAsJsonArray("recipe");
        List<String> ingredients = new ArrayList<>();
        for (JsonElement element : recipeJson) {
            ingredients.add(element.getAsString());
        }

        if (ingredients.isEmpty()) {
            throw new IllegalArgumentException("Recipe must have at least one ingredient in 'recipe'");
        }

        return new InfusionRecipeData(
            recipeName,
            research,
            centralItem,
            result,
            instability,
            aspects,
            ingredients
        );
    }
}
