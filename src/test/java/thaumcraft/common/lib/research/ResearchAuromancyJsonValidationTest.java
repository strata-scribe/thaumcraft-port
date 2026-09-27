package thaumcraft.common.lib.research;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ResearchAuromancyJsonValidationTest {

    @Test
    public void testAuromancyJsonValidity() {
        String filePath = "/assets/thaumcraft/research/auromancy.json";
        InputStream stream = getClass().getResourceAsStream(filePath);
        assertNotNull(stream, "Resource file must exist: " + filePath);

        // 1. Verify JSON parses without syntax errors
        JsonElement rootElement = assertDoesNotThrow(() -> {
            JsonParser parser = new JsonParser();
            return parser.parse(new InputStreamReader(stream));
        }, "JSON must parse without syntax errors");

        // 2. Verify focus nodes and complexity requirements are valid numbers
        validateNumbers(rootElement);
    }

    private void validateNumbers(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                String key = entry.getKey();
                JsonElement val = entry.getValue();

                if ("focus_nodes".equals(key) || "complexity".equals(key)) {
                    assertTrue(val.isJsonPrimitive(), "Key '" + key + "' must be a primitive value");
                    assertTrue(val.getAsJsonPrimitive().isNumber(), "Key '" + key + "' must be a valid number");
                } else {
                    validateNumbers(val);
                }
            }
        } else if (element.isJsonArray()) {
            JsonArray arr = element.getAsJsonArray();
            for (JsonElement item : arr) {
                validateNumbers(item);
            }
        }
    }
}
