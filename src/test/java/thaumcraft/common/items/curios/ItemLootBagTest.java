package thaumcraft.common.items.curios;

import org.junit.jupiter.api.Test;
import thaumcraft.common.items.loot.logic.LootBagRarityRollLogic;
import static org.junit.jupiter.api.Assertions.*;

public class ItemLootBagTest {

    @Test
    public void testCommonLootBagDistribution() {
        int commonCount = 0;
        int uncommonCount = 0;

        for (int i = 0; i <= 1000; i++) {
            double roll = i / 1000.0; // From 0.0 to 1.0 inclusive
            LootBagRarityRollLogic.LootBagResult result = LootBagRarityRollLogic.calculateLootBagResult(roll, 0.5, 0);

            if (result.treasureTier == LootBagRarityRollLogic.LootTier.COMMON) {
                commonCount++;
            } else if (result.treasureTier == LootBagRarityRollLogic.LootTier.UNCOMMON) {
                uncommonCount++;
            }
        }

        // 0.0 to 0.8 is common (801 values: 0.000 to 0.800)
        // 0.801 to 1.000 is uncommon (200 values)
        assertEquals(801, commonCount, "Expected 801 common rolls for Common Bag");
        assertEquals(200, uncommonCount, "Expected 200 uncommon rolls for Common Bag");
    }

    @Test
    public void testUncommonLootBagDistribution() {
        int commonCount = 0;
        int uncommonCount = 0;
        int rareCount = 0;

        for (int i = 0; i <= 1000; i++) {
            double roll = i / 1000.0;
            LootBagRarityRollLogic.LootBagResult result = LootBagRarityRollLogic.calculateLootBagResult(roll, 0.5, 1);

            if (result.treasureTier == LootBagRarityRollLogic.LootTier.COMMON) {
                commonCount++;
            } else if (result.treasureTier == LootBagRarityRollLogic.LootTier.UNCOMMON) {
                uncommonCount++;
            } else if (result.treasureTier == LootBagRarityRollLogic.LootTier.RARE) {
                rareCount++;
            }
        }

        // < 0.2 is COMMON (0.000 to 0.199 = 200 values)
        // > 0.7 is RARE (0.701 to 1.000 = 300 values)
        // 0.200 to 0.700 is UNCOMMON (501 values)
        assertEquals(200, commonCount, "Expected 200 common rolls for Uncommon Bag");
        assertEquals(501, uncommonCount, "Expected 501 uncommon rolls for Uncommon Bag");
        assertEquals(300, rareCount, "Expected 300 rare rolls for Uncommon Bag");
    }

    @Test
    public void testRareLootBagDistribution() {
        int uncommonCount = 0;
        int rareCount = 0;

        for (int i = 0; i <= 1000; i++) {
            double roll = i / 1000.0;
            LootBagRarityRollLogic.LootBagResult result = LootBagRarityRollLogic.calculateLootBagResult(roll, 0.5, 2);

            if (result.treasureTier == LootBagRarityRollLogic.LootTier.UNCOMMON) {
                uncommonCount++;
            } else if (result.treasureTier == LootBagRarityRollLogic.LootTier.RARE) {
                rareCount++;
            }
        }

        // < 0.3 is UNCOMMON (0.000 to 0.299 = 300 values)
        // >= 0.3 is RARE (0.300 to 1.000 = 701 values)
        assertEquals(300, uncommonCount, "Expected 300 uncommon rolls for Rare Bag");
        assertEquals(701, rareCount, "Expected 701 rare rolls for Rare Bag");
    }

    @Test
    public void testClassStructureAndConstructors() throws Exception {
        Class<?> clazz = Class.forName("thaumcraft.common.items.curios.ItemLootBag", false, getClass().getClassLoader());
        assertEquals("net.minecraft.world.item.Item", clazz.getSuperclass().getName());

        boolean hasTwoArgConstructor = false;
        boolean hasOneArgConstructor = false;
        for (var ctor : clazz.getDeclaredConstructors()) {
            Class<?>[] params = ctor.getParameterTypes();
            if (params.length == 2 && params[0].getName().equals("net.minecraft.world.item.Item$Properties") && params[1] == int.class) {
                hasTwoArgConstructor = true;
            } else if (params.length == 1 && params[0].getName().equals("net.minecraft.world.item.Item$Properties")) {
                hasOneArgConstructor = true;
            }
        }
        assertTrue(hasTwoArgConstructor, "ItemLootBag must provide (Item.Properties, int) constructor");
        assertTrue(hasOneArgConstructor, "ItemLootBag must provide (Item.Properties) default constructor");

        boolean hasGetRarityTier = false;
        for (var method : clazz.getDeclaredMethods()) {
            if (method.getName().equals("getRarityTier") && method.getReturnType() == int.class) {
                hasGetRarityTier = true;
                break;
            }
        }
        assertTrue(hasGetRarityTier, "ItemLootBag must provide getRarityTier() accessor");
    }

    @Test
    public void testRarityTierAssignmentsAndLogic() {
        // Rarity 0 = Common
        LootBagRarityRollLogic.LootBagResult commonLow = LootBagRarityRollLogic.calculateLootBagResult(0.1, 0.5, 0);
        assertEquals(LootBagRarityRollLogic.LootTier.COMMON, commonLow.treasureTier);
        assertTrue(commonLow.goldCoins >= 1 && commonLow.goldCoins <= 3);

        // Rarity 1 = Uncommon
        LootBagRarityRollLogic.LootBagResult uncommonMid = LootBagRarityRollLogic.calculateLootBagResult(0.5, 0.5, 1);
        assertEquals(LootBagRarityRollLogic.LootTier.UNCOMMON, uncommonMid.treasureTier);
        assertTrue(uncommonMid.goldCoins >= 3 && uncommonMid.goldCoins <= 6);

        // Rarity 2 = Rare
        LootBagRarityRollLogic.LootBagResult rareHigh = LootBagRarityRollLogic.calculateLootBagResult(0.8, 0.5, 2);
        assertEquals(LootBagRarityRollLogic.LootTier.RARE, rareHigh.treasureTier);
        assertTrue(rareHigh.goldCoins >= 6 && rareHigh.goldCoins <= 10);
    }
}
