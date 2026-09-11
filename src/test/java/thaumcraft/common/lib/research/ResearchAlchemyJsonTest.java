package thaumcraft.common.lib.research;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.Identifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchStage;
import thaumcraft.common.config.ConfigResearch;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ResearchAlchemyJsonTest {

    @BeforeAll
    public static void setupCategories() {
        ResearchCategories.researchCategories.clear();
        for (String cat : ConfigResearch.TCCategories) {
            ResearchCategories.registerCategory(cat, null, new AspectList(), null, null, null);
        }
    }

    @Test
    public void testAlchemyResearchDeserialization() {
        String[] files = {
            "/assets/thaumcraft/research/basics.json",
            "/assets/thaumcraft/research/alchemy.json"
        };
        JsonParser parser = new JsonParser();
        Map<String, ResearchEntry> parsedEntries = new HashMap<>();

        for (String filePath : files) {
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
        }

        // Assertions for keys
        assertTrue(parsedEntries.containsKey("UNLOCKALCHEMY"), "UNLOCKALCHEMY (Crucible) must be parsed successfully!");
        assertTrue(parsedEntries.containsKey("ESSENTIASMELTER"), "ESSENTIASMELTER must be parsed successfully!");
        assertTrue(parsedEntries.containsKey("TUBES"), "TUBES must be parsed successfully!");
        assertTrue(parsedEntries.containsKey("CENTRIFUGE"), "CENTRIFUGE must be parsed successfully!");
        assertTrue(parsedEntries.containsKey("THAUMATORIUM"), "THAUMATORIUM must be parsed successfully!");

        ResearchEntry crucible = parsedEntries.get("UNLOCKALCHEMY");
        assertNotNull(crucible.getStages());
        assertTrue(crucible.getStages().length > 0);
        assertNotNull(crucible.getParents());
        assertTrue(crucible.getParents().length > 0);
        assertNotNull(crucible.getIcons());
        assertTrue(crucible.getIcons().length > 0);

        ResearchEntry essentiaSmelter = parsedEntries.get("ESSENTIASMELTER");
        assertNotNull(essentiaSmelter);
        assertNotNull(essentiaSmelter.getStages());
        assertTrue(essentiaSmelter.getStages().length > 0);
        assertNotNull(essentiaSmelter.getParents());
        assertTrue(essentiaSmelter.getParents().length > 0);
        assertNotNull(essentiaSmelter.getIcons());
        assertTrue(essentiaSmelter.getIcons().length > 0);

        ResearchEntry tubes = parsedEntries.get("TUBES");
        assertNotNull(tubes.getStages());
        assertTrue(tubes.getStages().length > 0);
        assertNotNull(tubes.getParents());
        assertTrue(tubes.getParents().length > 0);
        assertNotNull(tubes.getIcons());
        assertTrue(tubes.getIcons().length > 0);

        ResearchEntry centrifuge = parsedEntries.get("CENTRIFUGE");
        assertNotNull(centrifuge.getStages());
        assertTrue(centrifuge.getStages().length > 0);
        assertNotNull(centrifuge.getParents());
        assertTrue(centrifuge.getParents().length > 0);
        assertNotNull(centrifuge.getIcons());
        assertTrue(centrifuge.getIcons().length > 0);

        ResearchEntry thaumatorium = parsedEntries.get("THAUMATORIUM");
        assertNotNull(thaumatorium.getStages());
        assertTrue(thaumatorium.getStages().length > 0);
        assertNotNull(thaumatorium.getParents());
        assertTrue(thaumatorium.getParents().length > 0);
        assertNotNull(thaumatorium.getIcons());
        assertTrue(thaumatorium.getIcons().length > 0);

        // Check for thaumcraft:Alembic recipe in ESSENTIASMELTER
        boolean foundAlembic = false;
        for (ResearchStage stage : essentiaSmelter.getStages()) {
            if (stage.getRecipes() != null) {
                for (Identifier recipe : stage.getRecipes()) {
                    if (recipe.toString().equalsIgnoreCase("thaumcraft:alembic")) {
                        foundAlembic = true;
                        break;
                    }
                }
            }
        }
        assertTrue(foundAlembic, "Alembic recipe must be present in ESSENTIASMELTER stages");

        // Basic schema validations for all parsed alchemy/basics entries
        for (ResearchEntry entry : parsedEntries.values()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getName());
            assertNotNull(entry.getCategory());

            // If it has stages, make sure they are somewhat valid
            if (entry.getStages() != null) {
                for (ResearchStage stage : entry.getStages()) {
                    assertNotNull(stage.getText());
                    // Warp ratings should be >= 0
                    assertTrue(stage.getWarp() >= 0);
                }
            }
        }
    }
}
