package thaumcraft.common.entities;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThaumcraftEntityLootLogicTest {

    @Test
    void testCalculatePechDrops() {
        // Test RNG <= 0.5 (should only drop mana_bean)
        List<String> drops1 = ThaumcraftEntityLootLogic.calculatePechDrops(0.4, 0);
        assertEquals(1, drops1.size());
        assertTrue(drops1.contains("mana_bean"));

        // Test RNG > 0.5 (should drop mana_bean and pech_wand)
        List<String> drops2 = ThaumcraftEntityLootLogic.calculatePechDrops(0.6, 0);
        assertEquals(2, drops2.size());
        assertTrue(drops2.contains("mana_bean"));
        assertTrue(drops2.contains("pech_wand"));
    }

    @Test
    void testCalculateCrimsonCultistDrops() {
        // Test RNG <= 0.8, looting 0
        List<String> drops1 = ThaumcraftEntityLootLogic.calculateCrimsonCultistDrops(0.7, 0);
        assertEquals(1, drops1.size());
        assertTrue(drops1.contains("iron_ingot"));

        // Test RNG > 0.8, looting 1
        List<String> drops2 = ThaumcraftEntityLootLogic.calculateCrimsonCultistDrops(0.9, 1);
        assertEquals(3, drops2.size());
        assertTrue(drops2.contains("crimson_rites"));
        int ingotCount = 0;
        for (String drop : drops2) {
            if (drop.equals("iron_ingot")) ingotCount++;
        }
        assertEquals(2, ingotCount);
    }

    @Test
    void testCalculateWispDrops() {
        // Test RNG <= 0.4
        List<String> drops1 = ThaumcraftEntityLootLogic.calculateWispDrops(0.3, 0);
        assertEquals(0, drops1.size());

        // Test RNG > 0.4
        List<String> drops2 = ThaumcraftEntityLootLogic.calculateWispDrops(0.5, 0);
        assertEquals(1, drops2.size());
        assertTrue(drops2.contains("auram_vis_crystal"));
    }

    @Test
    void testCalculateEldritchGuardianDrops() {
        // Test RNG <= 0.3
        List<String> drops1 = ThaumcraftEntityLootLogic.calculateEldritchGuardianDrops(0.2, 0);
        assertEquals(1, drops1.size());
        assertTrue(drops1.contains("void_seed"));

        // Test RNG > 0.3
        List<String> drops2 = ThaumcraftEntityLootLogic.calculateEldritchGuardianDrops(0.4, 0);
        assertEquals(2, drops2.size());
        assertTrue(drops2.contains("eldritch_eye"));
        assertTrue(drops2.contains("void_seed"));
    }
}
