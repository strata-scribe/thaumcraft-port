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

public class ResearchJsonTest {

    @BeforeAll
    public static void setupCategories() {
        ResearchCategories.researchCategories.clear();
        for (String cat : ConfigResearch.TCCategories) {
            ResearchCategories.registerCategory(cat, null, new AspectList(), null, null, null);
        }
    }

    @Test
    public void testParseAllResearchJsonFiles() {
        String[] files = {
            "/assets/thaumcraft/research/basics.json",
            "/assets/thaumcraft/research/alchemy.json",
            "/assets/thaumcraft/research/auromancy.json",
            "/assets/thaumcraft/research/artifice.json",
            "/assets/thaumcraft/research/infusion.json",
            "/assets/thaumcraft/research/golemancy.json",
            "/assets/thaumcraft/research/eldritch.json",
            "/assets/thaumcraft/research/scans.json"
        };

        JsonParser parser = new JsonParser();
        int totalParsed = 0;
        List<String> parsedKeys = new ArrayList<>();

        for (String filePath : files) {
            InputStream stream = getClass().getResourceAsStream(filePath);
            assertNotNull(stream, "Resource file must exist: " + filePath);

            JsonObject obj = parser.parse(new InputStreamReader(stream)).getAsJsonObject();
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
            }
        }

        assertTrue(totalParsed > 50, "Expected to load at least 50 research entries, got " + totalParsed);
        assertTrue(parsedKeys.contains("FIRSTSTEPS"), "FIRSTSTEPS must be parsed successfully!");
        assertTrue(parsedKeys.contains("KNOWLEDGETYPES"), "KNOWLEDGETYPES must be parsed successfully!");
        assertTrue(parsedKeys.contains("THEORYRESEARCH"), "THEORYRESEARCH must be parsed successfully!");
        System.out.println("SUCCESS: Parsed " + totalParsed + " research entries across all 8 files. FIRSTSTEPS verified!");
    }
}

