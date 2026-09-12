package thaumcraft.data.tags;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ThaumcraftBlockTagProviderLogicTest {

    @Test
    public void testIsOre() {
        assertTrue(ThaumcraftBlockTagProviderLogic.isOre("iron_ore"));
        assertTrue(ThaumcraftBlockTagProviderLogic.isOre("Cinnabar_Ore"));
        assertTrue(ThaumcraftBlockTagProviderLogic.isOre("ore_gold"));
        assertFalse(ThaumcraftBlockTagProviderLogic.isOre("iron_block"));
        assertFalse(ThaumcraftBlockTagProviderLogic.isOre("stone"));
        assertFalse(ThaumcraftBlockTagProviderLogic.isOre(null));
    }

    @Test
    public void testIsLog() {
        assertTrue(ThaumcraftBlockTagProviderLogic.isLog("oak_log"));
        assertTrue(ThaumcraftBlockTagProviderLogic.isLog("greatwood_log"));
        assertTrue(ThaumcraftBlockTagProviderLogic.isLog("silverwood"));
        assertFalse(ThaumcraftBlockTagProviderLogic.isLog("oak_planks"));
        assertFalse(ThaumcraftBlockTagProviderLogic.isLog(null));
    }

    @Test
    public void testIsLeaves() {
        assertTrue(ThaumcraftBlockTagProviderLogic.isLeaves("oak_leaves"));
        assertTrue(ThaumcraftBlockTagProviderLogic.isLeaves("Silverwood_Leaves"));
        assertFalse(ThaumcraftBlockTagProviderLogic.isLeaves("oak_log"));
        assertFalse(ThaumcraftBlockTagProviderLogic.isLeaves(null));
    }

    @Test
    public void testIsStone() {
        assertTrue(ThaumcraftBlockTagProviderLogic.isStone("stone"));
        assertTrue(ThaumcraftBlockTagProviderLogic.isStone("cobblestone"));
        assertTrue(ThaumcraftBlockTagProviderLogic.isStone("bedrock"));
        assertFalse(ThaumcraftBlockTagProviderLogic.isStone("dirt"));
        assertFalse(ThaumcraftBlockTagProviderLogic.isStone("wood"));
        assertFalse(ThaumcraftBlockTagProviderLogic.isStone(null));
    }

    @Test
    public void testIsWarded() {
        assertTrue(ThaumcraftBlockTagProviderLogic.isWarded("warded_stone"));
        assertTrue(ThaumcraftBlockTagProviderLogic.isWarded("Warded_Glass"));
        assertFalse(ThaumcraftBlockTagProviderLogic.isWarded("stone"));
        assertFalse(ThaumcraftBlockTagProviderLogic.isWarded("glass"));
        assertFalse(ThaumcraftBlockTagProviderLogic.isWarded(null));
    }

    @Test
    public void testGenerateTagsForBlock() {
        List<String> tagsOre = ThaumcraftBlockTagProviderLogic.generateTagsForBlock("iron_ore");
        assertEquals(1, tagsOre.size());
        assertTrue(tagsOre.contains("forge:ores"));

        List<String> tagsLog = ThaumcraftBlockTagProviderLogic.generateTagsForBlock("greatwood_log");
        assertEquals(1, tagsLog.size());
        assertTrue(tagsLog.contains("minecraft:logs"));

        List<String> tagsLeaves = ThaumcraftBlockTagProviderLogic.generateTagsForBlock("silverwood_leaves");
        assertEquals(1, tagsLeaves.size());
        assertTrue(tagsLeaves.contains("minecraft:leaves"));

        List<String> tagsStone = ThaumcraftBlockTagProviderLogic.generateTagsForBlock("arcane_stone");
        assertEquals(1, tagsStone.size());
        assertTrue(tagsStone.contains("forge:stone"));

        List<String> tagsWarded = ThaumcraftBlockTagProviderLogic.generateTagsForBlock("warded_stone");
        assertEquals(2, tagsWarded.size()); // It should match both stone and warded
        assertTrue(tagsWarded.contains("thaumcraft:warded"));
        assertTrue(tagsWarded.contains("forge:stone"));

        List<String> tagsEmpty = ThaumcraftBlockTagProviderLogic.generateTagsForBlock("dirt");
        assertTrue(tagsEmpty.isEmpty());

        List<String> tagsNull = ThaumcraftBlockTagProviderLogic.generateTagsForBlock(null);
        assertTrue(tagsNull.isEmpty());
    }
}
