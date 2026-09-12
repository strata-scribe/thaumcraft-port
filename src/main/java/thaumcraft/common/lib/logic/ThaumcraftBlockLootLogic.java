package thaumcraft.common.lib.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Pure Java logic for generating Thaumcraft block loot tables.
 * Decoupled from net.minecraft for easy unit testing.
 */
public class ThaumcraftBlockLootLogic {

    public static class LootCondition {
        public String type;
        public Map<String, Object> properties;

        public LootCondition(String type) {
            this.type = type;
        }

        public LootCondition(String type, Map<String, Object> properties) {
            this.type = type;
            this.properties = properties;
        }
    }

    public static class LootFunction {
        public String type;
        public Map<String, Object> properties;

        public LootFunction(String type) {
            this.type = type;
        }

        public LootFunction(String type, Map<String, Object> properties) {
            this.type = type;
            this.properties = properties;
        }
    }

    public static class LootEntry {
        public String type;
        public String name;
        public List<LootCondition> conditions = new ArrayList<>();
        public List<LootFunction> functions = new ArrayList<>();
        public List<LootEntry> children = new ArrayList<>();

        public LootEntry(String type, String name) {
            this.type = type;
            this.name = name;
        }

        public LootEntry(String type, List<LootEntry> children) {
            this.type = type;
            this.children = children;
        }
    }

    public static class LootPool {
        public int rolls;
        public List<LootEntry> entries = new ArrayList<>();
        public List<LootCondition> conditions = new ArrayList<>();

        public LootPool(int rolls) {
            this.rolls = rolls;
        }
    }

    public static class LootTable {
        public String type = "minecraft:block";
        public List<LootPool> pools = new ArrayList<>();
    }

    /**
     * Generates a drop table for an ore that drops itself if Silk Touched,
     * or a specified raw item (with Fortune bonus) otherwise.
     */
    public static LootTable generateOreDropTable(String blockName, String dropItemName) {
        LootTable table = new LootTable();

        LootPool pool = new LootPool(1);

        LootEntry silkTouchEntry = new LootEntry("minecraft:item", blockName);
        silkTouchEntry.conditions.add(new LootCondition("minecraft:match_tool", Map.of("enchantments", List.of(Map.of("enchantment", "minecraft:silk_touch", "levels", Map.of("min", 1))))));

        LootEntry fortuneEntry = new LootEntry("minecraft:item", dropItemName);
        fortuneEntry.functions.add(new LootFunction("minecraft:apply_bonus", Map.of("enchantment", "minecraft:fortune", "formula", "minecraft:ore_drops")));
        fortuneEntry.functions.add(new LootFunction("minecraft:explosion_decay"));

        LootEntry alternatives = new LootEntry("minecraft:alternatives", List.of(silkTouchEntry, fortuneEntry));

        pool.entries.add(alternatives);
        table.pools.add(pool);

        return table;
    }

    /**
     * Generates a drop table for an apparatus block, preserving NBT data and dropping the item itself.
     */
    public static LootTable generateApparatusDropTable(String blockName) {
        LootTable table = new LootTable();

        LootPool pool = new LootPool(1);
        pool.conditions.add(new LootCondition("minecraft:survives_explosion"));

        LootEntry entry = new LootEntry("minecraft:item", blockName);
        entry.functions.add(new LootFunction("minecraft:copy_nbt", Map.of("source", "block_entity", "ops", List.of(Map.of("source", "", "target", "BlockEntityTag", "op", "replace")))));
        entry.functions.add(new LootFunction("minecraft:copy_name", Map.of("source", "block_entity")));

        pool.entries.add(entry);
        table.pools.add(pool);

        return table;
    }

    /**
     * Generates a drop table for a crop, dropping produce on maturity and seeds (with fortune).
     */
    public static LootTable generateCropDropTable(String blockName, String produceItem, String seedItem, int maxAge) {
        LootTable table = new LootTable();

        LootPool producePool = new LootPool(1);
        LootEntry produceEntry = new LootEntry("minecraft:item", produceItem);
        produceEntry.conditions.add(new LootCondition("minecraft:block_state_property", Map.of("block", blockName, "properties", Map.of("age", maxAge))));
        producePool.entries.add(produceEntry);

        LootPool seedPool = new LootPool(1);
        LootEntry seedEntry = new LootEntry("minecraft:item", seedItem);
        seedEntry.functions.add(new LootFunction("minecraft:apply_bonus", Map.of("enchantment", "minecraft:fortune", "formula", "minecraft:binomial_with_bonus_count", "parameters", Map.of("extra", 3, "probability", 0.5714286))));
        seedPool.entries.add(seedEntry);
        seedPool.conditions.add(new LootCondition("minecraft:survives_explosion"));

        table.pools.add(producePool);
        table.pools.add(seedPool);

        return table;
    }

    /**
     * Generates a drop table for a multi-block component, dropping the base construction material.
     */
    public static LootTable generateMultiBlockComponentDropTable(String baseMaterialItem) {
        LootTable table = new LootTable();

        LootPool pool = new LootPool(1);
        pool.conditions.add(new LootCondition("minecraft:survives_explosion"));

        LootEntry entry = new LootEntry("minecraft:item", baseMaterialItem);

        pool.entries.add(entry);
        table.pools.add(pool);

        return table;
    }
}
