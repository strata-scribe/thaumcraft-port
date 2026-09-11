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
import thaumcraft.api.research.ResearchStage;
import thaumcraft.common.config.ConfigResearch;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResearchFundamentalsJsonTest {

    @BeforeAll
    public static void setupCategories() {
        ResearchCategories.researchCategories.clear();
        for (String cat : ConfigResearch.TCCategories) {
            ResearchCategories.registerCategory(cat, null, new AspectList(), null, null, null);
        }
        ResearchCategories.registerCategory("FUNDAMENTALS", null, new AspectList(), null, null, null);
    }

    @Test
    public void testFundamentalsJson() {
        String filePath = "/assets/thaumcraft/research/fundamentals.json";


        int totalParsed = 0;
        List<String> parsedKeys = new ArrayList<>();

        InputStream stream = getClass().getResourceAsStream(filePath);
        assertNotNull(stream, "Resource file must exist: " + filePath);

        JsonObject obj = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
        JsonArray entries = obj.get("entries").getAsJsonArray();

        for (JsonElement elem : entries) {
            JsonObject entryJson = elem.getAsJsonObject();
            String key = entryJson.get("key").getAsString();
            ResearchEntry entry = ResearchEntry.CODEC.parse(JsonOps.INSTANCE, entryJson)
                    .getOrThrow(err -> new AssertionError("Failed to parse research entry " + key + " in " + filePath + ": " + err));
            assertNotNull(entry);
            assertEquals(key, entry.getKey());
            totalParsed++;
            parsedKeys.add(key);

            assertNotNull(entry.getStages(), "Stages must not be null for: " + key);
            assertNotNull(entry.getIcons(), "Icons must not be null for: " + key);

            if (entry.getParents() != null) {
                assertTrue(entry.getParents().length > 0, "Parents array exists but empty for: " + key);
            }

            for (ResearchStage stage : entry.getStages()) {
                if (key.equals("ORES")) {
                    assertEquals(1, stage.getWarp(), "Warp not parsed correctly for ORES");
                }
                if (key.equals("AURA")) {
                    assertEquals(2, stage.getWarp(), "Warp not parsed correctly for AURA");
                }
            }
        }

        assertTrue(parsedKeys.contains("SALISMUNDUS"), "SALISMUNDUS must be parsed successfully!");
        assertTrue(parsedKeys.contains("ORES"), "ORES must be parsed successfully!");
        assertTrue(parsedKeys.contains("VISCRYSTALS"), "VISCRYSTALS must be parsed successfully!");
        assertTrue(parsedKeys.contains("PLANTS"), "PLANTS must be parsed successfully!");
        assertTrue(parsedKeys.contains("AURA"), "AURA must be parsed successfully!");

        System.out.println("SUCCESS: Parsed " + totalParsed + " fundamentals research entries.");
    }
}
