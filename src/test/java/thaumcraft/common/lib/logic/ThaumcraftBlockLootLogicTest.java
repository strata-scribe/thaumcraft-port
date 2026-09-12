package thaumcraft.common.lib.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

class ThaumcraftBlockLootLogicTest {

    @Test
    void testGenerateOreDropTable() {
        ThaumcraftBlockLootLogic.LootTable table = ThaumcraftBlockLootLogic.generateOreDropTable("thaumcraft:amber_ore", "thaumcraft:amber");

        assertEquals("minecraft:block", table.type);
        assertEquals(1, table.pools.size());

        ThaumcraftBlockLootLogic.LootPool pool = table.pools.get(0);
        assertEquals(1, pool.rolls);
        assertEquals(1, pool.entries.size());

        ThaumcraftBlockLootLogic.LootEntry altEntry = pool.entries.get(0);
        assertEquals("minecraft:alternatives", altEntry.type);
        assertEquals(2, altEntry.children.size());

        ThaumcraftBlockLootLogic.LootEntry silkTouchEntry = altEntry.children.get(0);
        assertEquals("minecraft:item", silkTouchEntry.type);
        assertEquals("thaumcraft:amber_ore", silkTouchEntry.name);
        assertEquals(1, silkTouchEntry.conditions.size());
        assertEquals("minecraft:match_tool", silkTouchEntry.conditions.get(0).type);

        ThaumcraftBlockLootLogic.LootEntry fortuneEntry = altEntry.children.get(1);
        assertEquals("minecraft:item", fortuneEntry.type);
        assertEquals("thaumcraft:amber", fortuneEntry.name);
        assertEquals(2, fortuneEntry.functions.size());
        assertEquals("minecraft:apply_bonus", fortuneEntry.functions.get(0).type);
        assertEquals("minecraft:explosion_decay", fortuneEntry.functions.get(1).type);
    }

    @Test
    void testGenerateApparatusDropTable() {
        ThaumcraftBlockLootLogic.LootTable table = ThaumcraftBlockLootLogic.generateApparatusDropTable("thaumcraft:infusion_matrix");

        assertEquals(1, table.pools.size());
        ThaumcraftBlockLootLogic.LootPool pool = table.pools.get(0);
        assertEquals(1, pool.conditions.size());
        assertEquals("minecraft:survives_explosion", pool.conditions.get(0).type);

        assertEquals(1, pool.entries.size());
        ThaumcraftBlockLootLogic.LootEntry entry = pool.entries.get(0);
        assertEquals("minecraft:item", entry.type);
        assertEquals("thaumcraft:infusion_matrix", entry.name);

        assertEquals(2, entry.functions.size());
        assertEquals("minecraft:copy_nbt", entry.functions.get(0).type);
        assertEquals("minecraft:copy_name", entry.functions.get(1).type);
    }

    @Test
    void testGenerateCropDropTable() {
        ThaumcraftBlockLootLogic.LootTable table = ThaumcraftBlockLootLogic.generateCropDropTable("thaumcraft:mana_pod", "thaumcraft:mana_bean", "thaumcraft:mana_pod_seed", 7);

        assertEquals(2, table.pools.size());

        ThaumcraftBlockLootLogic.LootPool producePool = table.pools.get(0);
        assertEquals(1, producePool.entries.size());
        ThaumcraftBlockLootLogic.LootEntry produceEntry = producePool.entries.get(0);
        assertEquals("minecraft:item", produceEntry.type);
        assertEquals("thaumcraft:mana_bean", produceEntry.name);
        assertEquals(1, produceEntry.conditions.size());
        assertEquals("minecraft:block_state_property", produceEntry.conditions.get(0).type);

        ThaumcraftBlockLootLogic.LootPool seedPool = table.pools.get(1);
        assertEquals(1, seedPool.conditions.size());
        assertEquals("minecraft:survives_explosion", seedPool.conditions.get(0).type);
        assertEquals(1, seedPool.entries.size());
        ThaumcraftBlockLootLogic.LootEntry seedEntry = seedPool.entries.get(0);
        assertEquals("minecraft:item", seedEntry.type);
        assertEquals("thaumcraft:mana_pod_seed", seedEntry.name);
        assertEquals(1, seedEntry.functions.size());
        assertEquals("minecraft:apply_bonus", seedEntry.functions.get(0).type);
    }

    @Test
    void testGenerateMultiBlockComponentDropTable() {
        ThaumcraftBlockLootLogic.LootTable table = ThaumcraftBlockLootLogic.generateMultiBlockComponentDropTable("thaumcraft:arcane_stone");

        assertEquals(1, table.pools.size());
        ThaumcraftBlockLootLogic.LootPool pool = table.pools.get(0);

        assertEquals(1, pool.conditions.size());
        assertEquals("minecraft:survives_explosion", pool.conditions.get(0).type);

        assertEquals(1, pool.entries.size());
        ThaumcraftBlockLootLogic.LootEntry entry = pool.entries.get(0);
        assertEquals("minecraft:item", entry.type);
        assertEquals("thaumcraft:arcane_stone", entry.name);
    }
}
