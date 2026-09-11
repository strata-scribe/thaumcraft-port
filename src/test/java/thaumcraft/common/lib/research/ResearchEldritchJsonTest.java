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

public class ResearchEldritchJsonTest {

    @BeforeAll
    public static void setupCategories() {
        ResearchCategories.researchCategories.clear();
        for (String cat : ConfigResearch.TCCategories) {
            ResearchCategories.registerCategory(cat, null, new AspectList(), null, null, null);
        }
    }

    @Test
    public void testParseEldritchResearchJsonFile() {
        String filePath = "/assets/thaumcraft/research/eldritch.json";

        JsonParser parser = new JsonParser();
        List<String> parsedKeys = new ArrayList<>();

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
            parsedKeys.add(key);
        }

        assertTrue(parsedKeys.contains("WARP"), "WARP must be parsed successfully!");
        assertTrue(parsedKeys.contains("ELDRITCHEYE"), "ELDRITCHEYE must be parsed successfully!");
        assertTrue(parsedKeys.contains("CrimsonRites"), "CRIMSONRITES must be parsed successfully!");
        assertTrue(parsedKeys.contains("OUTERLANDS"), "OUTERLANDS must be parsed successfully!");
        assertTrue(parsedKeys.contains("VOIDROBEARMOR"), "VOIDROBEARMOR must be parsed successfully!");

        System.out.println("SUCCESS: Parsed eldritch research entries!");
    }
}
