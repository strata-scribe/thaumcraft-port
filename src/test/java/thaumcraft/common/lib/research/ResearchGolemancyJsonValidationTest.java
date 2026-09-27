package thaumcraft.common.lib.research;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResearchGolemancyJsonValidationTest {

    @Test
    public void testGolemancyJsonParsesAndContainsRequiredEntries() throws Exception {
        InputStream stream = getClass().getResourceAsStream("/assets/thaumcraft/research/golemancy.json");
        assertNotNull(stream, "golemancy.json could not be loaded");

        try (InputStreamReader reader = new InputStreamReader(stream)) {
            JsonElement rootElement = JsonParser.parseReader(reader);
            assertNotNull(rootElement);
            assertTrue(rootElement.isJsonObject());

            JsonObject root = rootElement.getAsJsonObject();
            assertTrue(root.has("entries"), "JSON should contain an 'entries' array");

            JsonArray entries = root.getAsJsonArray("entries");
            Set<String> entryKeys = new HashSet<>();

            for (JsonElement element : entries) {
                if (element.isJsonObject()) {
                    JsonObject entry = element.getAsJsonObject();
                    if (entry.has("key")) {
                        entryKeys.add(entry.get("key").getAsString());
                    }
                }
            }

            // Verify golem material entries
            assertTrue(entryKeys.contains("MATSTUDWOOD"), "Missing material entry MATSTUDWOOD");
            assertTrue(entryKeys.contains("MATSTUDIRON"), "Missing material entry MATSTUDIRON");
            assertTrue(entryKeys.contains("MATSTUDCLAY"), "Missing material entry MATSTUDCLAY");
            assertTrue(entryKeys.contains("MATSTUDBRASS"), "Missing material entry MATSTUDBRASS");
            assertTrue(entryKeys.contains("MATSTUDTHAUMIUM"), "Missing material entry MATSTUDTHAUMIUM");
            assertTrue(entryKeys.contains("MATSTUDVOID"), "Missing material entry MATSTUDVOID");

            // Verify head/arm/leg part requirements
            assertTrue(entryKeys.contains("GOLEMDIRECT"), "Missing part entry GOLEMDIRECT (head)");
            assertTrue(entryKeys.contains("GOLEMBREAKER"), "Missing part entry GOLEMBREAKER (arm)");
            assertTrue(entryKeys.contains("GOLEMCLIMBER"), "Missing part entry GOLEMCLIMBER (leg)");
            assertTrue(entryKeys.contains("GOLEMFLYER"), "Missing part entry GOLEMFLYER (leg)");
            assertTrue(entryKeys.contains("GOLEMVISION"), "Missing part entry GOLEMVISION (head)");
        }
    }
}
