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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ResearchAuromancyJsonTest {

    @BeforeAll
    public static void setupCategories() {
        ResearchCategories.researchCategories.clear();
        for (String cat : ConfigResearch.TCCategories) {
            ResearchCategories.registerCategory(cat, null, new AspectList(), null, null, null);
        }
    }

    @Test
    public void testParseAuromancyJson() {
        String filePath = "/assets/thaumcraft/research/auromancy.json";
        InputStream stream = getClass().getResourceAsStream(filePath);
        assertNotNull(stream, "Resource file must exist: " + filePath);

        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(new InputStreamReader(stream)).getAsJsonObject();
        JsonArray entries = obj.get("entries").getAsJsonArray();

        List<ResearchEntry> parsedEntries = new ArrayList<>();
        List<String> parsedKeys = new ArrayList<>();

        for (JsonElement elem : entries) {
            JsonObject entryJson = elem.getAsJsonObject();
            String key = entryJson.get("key").getAsString();
            ResearchEntry entry = ResearchEntry.CODEC.parse(JsonOps.INSTANCE, entryJson)
                    .getOrThrow(err -> new AssertionError("Failed to parse research entry " + key + ": " + err));
            assertNotNull(entry);
            assertEquals(key, entry.getKey());
            parsedEntries.add(entry);
            parsedKeys.add(key);
        }

        // Validate presence of specific research keys
        assertTrue(parsedKeys.contains("BASEAUROMANCY"), "BASEAUROMANCY (Caster Gauntlet) must be parsed successfully!");
        assertTrue(parsedKeys.contains("FOCUSADVANCED"), "FOCUSADVANCED must be parsed successfully!");
        assertTrue(parsedKeys.contains("FOCUSGREATER"), "FOCUSGREATER must be parsed successfully!");
        assertTrue(parsedKeys.contains("FOCUSELEMENTAL"), "FOCUSELEMENTAL (Elemental Effects) must be parsed successfully!");
        assertTrue(parsedKeys.contains("FOCUSPROJECTILE"), "FOCUSPROJECTILE (Medium) must be parsed successfully!");
        assertTrue(parsedKeys.contains("FOCUSBOLT"), "FOCUSBOLT (Medium) must be parsed successfully!");
        assertTrue(parsedKeys.contains("FOCUSSCATTER"), "FOCUSSCATTER (Medium) must be parsed successfully!");
        assertTrue(parsedKeys.contains("FOCUSMINE"), "FOCUSMINE (Medium) must be parsed successfully!");
        assertTrue(parsedKeys.contains("FOCUSCLOUD"), "FOCUSCLOUD (Medium) must be parsed successfully!");

        // Validate schema: Caster Gauntlet (BASEAUROMANCY)
        ResearchEntry baseAuromancy = findEntry(parsedEntries, "BASEAUROMANCY");
        assertNotNull(baseAuromancy);
        assertEquals("research.BASEAUROMANCY.title", baseAuromancy.getName());
        assertEquals(3, baseAuromancy.getStages().length, "BASEAUROMANCY should have 3 stages");
        assertTrue(baseAuromancy.getParents().length > 0, "BASEAUROMANCY should have parents");
        assertTrue(baseAuromancy.getIcons().length > 0, "BASEAUROMANCY should have icons");

        // Validate schema: Elemental Effects (FOCUSELEMENTAL)
        ResearchEntry focusElemental = findEntry(parsedEntries, "FOCUSELEMENTAL");
        assertNotNull(focusElemental);
        assertEquals(4, focusElemental.getIcons().length, "FOCUSELEMENTAL should have 4 element icons");
        assertEquals(2, focusElemental.getStages().length, "FOCUSELEMENTAL should have 2 stages");

        // Validate schema: Warp ratings and parents
        ResearchEntry focusRift = findEntry(parsedEntries, "FOCUSRIFT");
        assertNotNull(focusRift);
        assertTrue(focusRift.getStages()[0].getWarp() > 0, "FOCUSRIFT should have a warp rating");
        assertTrue(focusRift.getParents().length > 0, "FOCUSRIFT should have parents");
    }

    private ResearchEntry findEntry(List<ResearchEntry> entries, String key) {
        return entries.stream().filter(e -> e.getKey().equals(key)).findFirst().orElse(null);
    }
}
