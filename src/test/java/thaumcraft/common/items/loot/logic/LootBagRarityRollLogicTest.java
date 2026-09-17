package thaumcraft.common.items.loot.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LootBagRarityRollLogicTest {

    @Test
    public void testCommonBag() {
        // Low roll
        LootBagRarityRollLogic.LootBagResult result1 = LootBagRarityRollLogic.calculateLootBagResult(0.1, 0.1, 0);
        assertEquals(1, result1.goldCoins);
        assertEquals(1, result1.aspectCrystals);
        assertEquals(LootBagRarityRollLogic.LootTier.COMMON, result1.treasureTier);

        // High roll
        LootBagRarityRollLogic.LootBagResult result2 = LootBagRarityRollLogic.calculateLootBagResult(0.9, 0.9, 0);
        assertEquals(3, result2.goldCoins);
        assertEquals(3, result2.aspectCrystals);
        assertEquals(LootBagRarityRollLogic.LootTier.UNCOMMON, result2.treasureTier);
    }

    @Test
    public void testUncommonBag() {
        // Low roll
        LootBagRarityRollLogic.LootBagResult result1 = LootBagRarityRollLogic.calculateLootBagResult(0.1, 0.1, 1);
        assertEquals(3, result1.goldCoins);
        assertEquals(3, result1.aspectCrystals); // 1 + 2
        assertEquals(LootBagRarityRollLogic.LootTier.COMMON, result1.treasureTier);

        // Mid roll
        LootBagRarityRollLogic.LootBagResult result2 = LootBagRarityRollLogic.calculateLootBagResult(0.5, 0.5, 1);
        assertEquals(5, result2.goldCoins);
        assertEquals(4, result2.aspectCrystals); // 2 + 2
        assertEquals(LootBagRarityRollLogic.LootTier.UNCOMMON, result2.treasureTier);

        // High roll
        LootBagRarityRollLogic.LootBagResult result3 = LootBagRarityRollLogic.calculateLootBagResult(0.8, 0.9, 1);
        assertEquals(6, result3.goldCoins);
        assertEquals(5, result3.aspectCrystals); // 3 + 2
        assertEquals(LootBagRarityRollLogic.LootTier.RARE, result3.treasureTier);
    }

    @Test
    public void testRareBag() {
        // Low roll
        LootBagRarityRollLogic.LootBagResult result1 = LootBagRarityRollLogic.calculateLootBagResult(0.1, 0.1, 2);
        assertEquals(6, result1.goldCoins);
        assertEquals(5, result1.aspectCrystals); // 1 + 4
        assertEquals(LootBagRarityRollLogic.LootTier.UNCOMMON, result1.treasureTier);

        // High roll
        LootBagRarityRollLogic.LootBagResult result2 = LootBagRarityRollLogic.calculateLootBagResult(0.9, 0.9, 2);
        assertEquals(10, result2.goldCoins);
        assertEquals(7, result2.aspectCrystals); // 3 + 4
        assertEquals(LootBagRarityRollLogic.LootTier.RARE, result2.treasureTier);
    }

    @Test
    public void testInvalidBag() {
        LootBagRarityRollLogic.LootBagResult result = LootBagRarityRollLogic.calculateLootBagResult(0.5, 0.5, 99);
        assertEquals(1, result.goldCoins);
        assertEquals(1, result.aspectCrystals);
        assertEquals(LootBagRarityRollLogic.LootTier.COMMON, result.treasureTier);
    }
}
