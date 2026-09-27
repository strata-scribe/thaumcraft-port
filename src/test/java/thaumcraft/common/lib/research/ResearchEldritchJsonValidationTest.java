package thaumcraft.common.lib.research;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchEntry.EnumResearchMeta;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.common.config.ConfigResearch;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ResearchEldritchJsonValidationTest {

    @BeforeAll
    public static void setupCategories() {
        ResearchCategories.researchCategories.clear();
        for (String cat : ConfigResearch.TCCategories) {
            ResearchCategories.registerCategory(cat, null, new AspectList(), null, null, null);
        }
    }

    @Test
    public void testParseEldritchJson() {
        String filePath = "/assets/thaumcraft/research/eldritch.json";
        InputStream stream = getClass().getResourceAsStream(filePath);
        assertNotNull(stream, "Resource file must exist: " + filePath);

        JsonObject obj = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
        JsonArray entries = obj.get("entries").getAsJsonArray();

        Map<String, ResearchEntry> parsedEntries = new HashMap<>();

        for (JsonElement elem : entries) {
            JsonObject entryJson = elem.getAsJsonObject();
            String key = entryJson.get("key").getAsString();
            ResearchEntry entry = ResearchEntry.CODEC.parse(JsonOps.INSTANCE, entryJson)
                    .getOrThrow(err -> new AssertionError("Failed to parse research entry " + key + ": " + err));
            assertNotNull(entry);
            assertEquals(key, entry.getKey());
            parsedEntries.put(key, entry);
        }

        assertTrue(parsedEntries.size() > 0, "Should parse entries from eldritch.json");

        // Verify warp thresholds for key entries
        assertTrue(parsedEntries.containsKey("BASEELDRITCH"));
        ResearchEntry baseEldritch = parsedEntries.get("BASEELDRITCH");
        assertNotNull(baseEldritch.getStages());
        assertTrue(baseEldritch.getStages().length > 0);
        assertEquals(2, baseEldritch.getStages()[0].getWarp());

        assertTrue(parsedEntries.containsKey("VOIDSIPHON"));
        ResearchEntry voidSiphon = parsedEntries.get("VOIDSIPHON");
        assertNotNull(voidSiphon.getStages());
        assertTrue(voidSiphon.getStages().length > 0);
        assertEquals(1, voidSiphon.getStages()[0].getWarp());

        assertTrue(parsedEntries.containsKey("VOIDSEERPEARL"));
        ResearchEntry voidseerPearl = parsedEntries.get("VOIDSEERPEARL");
        assertNotNull(voidseerPearl.getStages());
        assertTrue(voidseerPearl.getStages().length > 0);
        assertEquals(3, voidseerPearl.getStages()[0].getWarp());

        // Verify crimson cult entry
        assertTrue(parsedEntries.containsKey("CrimsonRites"));
        ResearchEntry crimsonRites = parsedEntries.get("CrimsonRites");
        assertNotNull(crimsonRites.getParents());
        assertTrue(Arrays.asList(crimsonRites.getParents()).contains("!CrimsonCultist"));
        assertNotNull(crimsonRites.getMeta());
        assertTrue(Arrays.asList(crimsonRites.getMeta()).contains(EnumResearchMeta.HIDDEN));
        assertTrue(Arrays.asList(crimsonRites.getMeta()).contains(EnumResearchMeta.REVERSE));
    }
}
