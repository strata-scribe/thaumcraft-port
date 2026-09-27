package thaumcraft.common.lib.research;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ResearchBasicsJsonValidationTest {

    @Test
    public void testBasicsJsonParsing() {
        try (InputStream is = getClass().getResourceAsStream("/assets/thaumcraft/research/basics.json")) {
            assertNotNull(is, "basics.json not found in resources");

            JsonObject root = JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();
            assertNotNull(root);

            JsonArray entries = root.getAsJsonArray("entries");
            assertNotNull(entries, "No entries array found");
            assertFalse(entries.isEmpty(), "Entries array is empty");

            for (JsonElement entryElement : entries) {
                JsonObject entry = entryElement.getAsJsonObject();

                // Verify entry keys are non-empty strings
                assertTrue(entry.has("key"), "Entry missing 'key' property");
                String key = entry.get("key").getAsString();
                assertNotNull(key);
                assertFalse(key.trim().isEmpty(), "Entry key is empty");

                // Verify stage requirements
                if (entry.has("stages")) {
                    JsonArray stages = entry.getAsJsonArray("stages");
                    for (JsonElement stageElement : stages) {
                        JsonObject stage = stageElement.getAsJsonObject();

                        validateStringOrIntArray(stage, "required_research");
                        validateStringOrIntArray(stage, "required_knowledge");
                        validateStringOrIntArray(stage, "required_craft");
                        validateStringOrIntArray(stage, "required_item");
                        validateStringOrIntArray(stage, "recipes");
                    }
                }
            }

        } catch (Exception e) {
            fail("Exception thrown during JSON validation: " + e.getMessage());
        }
    }

    private void validateStringOrIntArray(JsonObject stage, String propertyName) {
        if (stage.has(propertyName)) {
            JsonArray reqs = stage.getAsJsonArray(propertyName);
            for (JsonElement req : reqs) {
                assertTrue(req.isJsonPrimitive(), propertyName + " token must be a string or integer primitive");
                assertTrue(req.getAsJsonPrimitive().isString() || req.getAsJsonPrimitive().isNumber(), propertyName + " token must be string or number, found: " + req.toString());
            }
        }
    }
}
