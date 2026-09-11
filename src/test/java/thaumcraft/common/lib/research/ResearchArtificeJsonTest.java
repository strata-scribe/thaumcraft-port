package thaumcraft.common.lib.research;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.common.config.ConfigResearch;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResearchArtificeJsonTest {

    @BeforeAll
    public static void setupCategories() {
        ResearchCategories.researchCategories.clear();
        for (String cat : ConfigResearch.TCCategories) {
            ResearchCategories.registerCategory(cat, null, new AspectList(), null, null, null);
        }
    }

    @Test
    public void testArtificeJson() {
        InputStream stream = getClass().getResourceAsStream("/assets/thaumcraft/research/artifice.json");
        assertNotNull(stream, "Resource file must exist: /assets/thaumcraft/research/artifice.json");

        JsonObject obj = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
        JsonArray entries = obj.get("entries").getAsJsonArray();

        List<String> parsedKeys = new ArrayList<>();
        for (JsonElement elem : entries) {
            JsonObject entryJson = elem.getAsJsonObject();
            String key = entryJson.get("key").getAsString();
            ResearchEntry entry = ResearchEntry.CODEC.parse(JsonOps.INSTANCE, entryJson)
                    .getOrThrow(err -> new AssertionError("Failed to parse research entry " + key + " in artifice.json: " + err));
            assertNotNull(entry);
            assertEquals(key, entry.getKey());
            parsedKeys.add(key);
        }

        assertTrue(parsedKeys.contains("BASEARTIFICE"), "BASEARTIFICE must be parsed successfully!");
        assertTrue(parsedKeys.contains("BELLOWS"), "BELLOWS must be parsed successfully!");
        assertTrue(parsedKeys.contains("LEVITATOR"), "LEVITATOR must be parsed successfully!");
        assertTrue(parsedKeys.contains("REDSTONERELAY"), "REDSTONERELAY must be parsed successfully!");
        assertTrue(parsedKeys.contains("MIRROR"), "MIRROR must be parsed successfully!");
        assertTrue(parsedKeys.contains("ARCANEWORKBENCH"), "ARCANEWORKBENCH must be parsed successfully!");
    }
}
