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
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ResearchInfusionJsonValidationTest {

    @BeforeAll
    public static void setupCategories() {
        ResearchCategories.researchCategories.clear();
        for (String cat : ConfigResearch.TCCategories) {
            ResearchCategories.registerCategory(cat, null, new AspectList(), null, null, null);
        }
    }

    @Test
    public void testInfusionResearchDeserialization() {
        String filePath = "/assets/thaumcraft/research/infusion.json";
        JsonParser parser = new JsonParser();
        Map<String, ResearchEntry> parsedEntries = new HashMap<>();

        InputStream stream = getClass().getResourceAsStream(filePath);
        assertNotNull(stream, "Resource file must exist: " + filePath);

        JsonObject obj = parser.parse(new InputStreamReader(stream)).getAsJsonObject();
        JsonArray entries = obj.get("entries").getAsJsonArray();

        for (JsonElement elem : entries) {
            JsonObject entryJson = elem.getAsJsonObject();
            String key = entryJson.get("key").getAsString();
            ResearchEntry entry = ResearchEntry.CODEC.parse(JsonOps.INSTANCE, entryJson)
                    .getOrThrow(err -> new AssertionError("Failed to parse research entry " + key + ": " + err));
            assertNotNull(entry);
            assertEquals(key, entry.getKey());

            parsedEntries.put(key, entry);
        }

        // Verify Altar Instability Factors and Central Item Requirements

        // 1. Check INFUSIONSTABLE parents list contains !INSTABILITY
        ResearchEntry infusionStable = parsedEntries.get("INFUSIONSTABLE");
        assertNotNull(infusionStable, "INFUSIONSTABLE entry must exist");
        boolean foundInstability = false;
        for (String parent : infusionStable.getParents()) {
            if ("!INSTABILITY".equals(parent)) {
                foundInstability = true;
                break;
            }
        }
        assertTrue(foundInstability, "INFUSIONSTABLE must have !INSTABILITY as a parent");

        // 2. We will check required_item from the raw json element, to be absolutely sure without relying on obtain
        // Codec parsing sets `obtain`, but it might be null if items are not present in BuiltInRegistries.ITEM
        // So we just check the raw JsonObject directly
        JsonArray infusionStages = null;
        for (JsonElement elem : entries) {
            if (elem.getAsJsonObject().get("key").getAsString().equals("INFUSION")) {
                infusionStages = elem.getAsJsonObject().get("stages").getAsJsonArray();
                break;
            }
        }
        assertNotNull(infusionStages, "INFUSION stages must exist");
        JsonObject firstStage = infusionStages.get(0).getAsJsonObject();
        JsonArray requiredItemArray = firstStage.get("required_item").getAsJsonArray();
        boolean foundStone = false;
        for (JsonElement e : requiredItemArray) {
            if ("minecraft:stone".equals(e.getAsString())) {
                foundStone = true;
                break;
            }
        }
        assertTrue(foundStone, "INFUSION first stage required_item must contain minecraft:stone");

        // 3. Check INFUSIONELDRITCH second stage warp equals 2
        ResearchEntry infusionEldritch = parsedEntries.get("INFUSIONELDRITCH");
        assertNotNull(infusionEldritch, "INFUSIONELDRITCH entry must exist");
        assertNotNull(infusionEldritch.getStages(), "INFUSIONELDRITCH must have stages");
        assertTrue(infusionEldritch.getStages().length > 1, "INFUSIONELDRITCH must have at least two stages");

        assertEquals(2, infusionEldritch.getStages()[1].getWarp(), "INFUSIONELDRITCH second stage warp must equal 2");

    }
}
