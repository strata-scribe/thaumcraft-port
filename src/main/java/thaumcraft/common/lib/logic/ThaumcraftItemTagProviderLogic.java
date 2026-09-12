package thaumcraft.common.lib.logic;

import java.util.Arrays;
import java.util.List;

public class ThaumcraftItemTagProviderLogic {

    private static final List<String> VALID_CATEGORIES = Arrays.asList("aspects", "crystals", "ingots", "curios", "clusters");

    public static final List<String> VALID_ASPECTS = Arrays.asList(
            "aer", "terra", "ignis", "aqua", "ordo", "perditio", "vacuos", "lux", "motus", "gelum", "vitreus", "metallum", "fames", "potentia", "victus", "mortuus", "volatus", "tenebrae", "spiritus", "sensus", "praecantatio", "magia", "alienis", "alkimia", "vitium", "auram", "herba", "arbor", "bestia", "corpus", "humanus", "cognitio", "desiderium", "fabrico", "machina", "meto", "pannus", "telum", "tutamen", "voluntas", "infernus", "superbia", "invidia"
    );

    public static final List<String> VALID_INGOTS = Arrays.asList("thaumium", "void", "brass");
    public static final List<String> VALID_CURIOS = Arrays.asList("arcane", "preserved", "ancient", "eldritch", "knowledge", "twisted", "rites");
    public static final List<String> VALID_CLUSTERS = Arrays.asList("iron", "gold", "copper", "tin", "silver", "lead", "cinnabar", "quartz");

    /**
     * Validates and generates item tags for aspects, crystals, ingots, curios, and clusters.
     */
    public static String getTagForItem(String category, String itemName) {
        if (category == null || itemName == null) {
            throw new IllegalArgumentException("Category and itemName cannot be null");
        }

        category = category.toLowerCase();
        itemName = itemName.toLowerCase();

        if (!VALID_CATEGORIES.contains(category)) {
            throw new IllegalArgumentException("Invalid category: " + category);
        }

        switch (category) {
            case "aspects":
                if (!VALID_ASPECTS.contains(itemName)) {
                    throw new IllegalArgumentException("Invalid aspect: " + itemName);
                }
                return "thaumcraft:aspects/" + itemName;
            case "crystals":
                if (!VALID_ASPECTS.contains(itemName)) {
                    throw new IllegalArgumentException("Invalid crystal aspect: " + itemName);
                }
                return "c:crystals/" + itemName;
            case "ingots":
                if (!VALID_INGOTS.contains(itemName)) {
                    throw new IllegalArgumentException("Invalid ingot: " + itemName);
                }
                return "c:ingots/" + itemName;
            case "curios":
                if (!VALID_CURIOS.contains(itemName)) {
                    throw new IllegalArgumentException("Invalid curio: " + itemName);
                }
                return "thaumcraft:curios/" + itemName;
            case "clusters":
                if (!VALID_CLUSTERS.contains(itemName)) {
                    throw new IllegalArgumentException("Invalid cluster: " + itemName);
                }
                return "c:clusters/" + itemName;
            default:
                throw new IllegalArgumentException("Unhandled category: " + category);
        }
    }
}
