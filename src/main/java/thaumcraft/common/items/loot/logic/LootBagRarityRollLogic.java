package thaumcraft.common.items.loot.logic;

/**
 * Pure Java logic helper for Champion Mob loot bags.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class LootBagRarityRollLogic {

    public enum LootTier {
        COMMON,
        UNCOMMON,
        RARE
    }

    public static class LootBagResult {
        public int goldCoins;
        public int aspectCrystals;
        public LootTier treasureTier;

        public LootBagResult(int goldCoins, int aspectCrystals, LootTier treasureTier) {
            this.goldCoins = goldCoins;
            this.aspectCrystals = aspectCrystals;
            this.treasureTier = treasureTier;
        }
    }

    /**
     * Calculates the loot drops for a Champion Mob loot bag based on its rarity.
     *
     * @param randomValue The RNG value (0.0 to 1.0) for primary rolls.
     * @param randomCrystalValue The RNG value (0.0 to 1.0) for crystal yield variation.
     * @param bagRarity 0 = Common, 1 = Uncommon, 2 = Rare
     * @return LootBagResult detailing the generated loot.
     */
    public static LootBagResult calculateLootBagResult(double randomValue, double randomCrystalValue, int bagRarity) {
        int goldCoins = 0;
        int aspectCrystals = 0;
        LootTier treasureTier = LootTier.COMMON;

        // Base crystal calculation
        int baseCrystals = 1 + (int)(randomCrystalValue * 3); // 1-3

        switch (bagRarity) {
            case 0: // Common
                goldCoins = 1 + (int)(randomValue * 3); // 1-3
                aspectCrystals = baseCrystals;
                treasureTier = LootTier.COMMON;
                if (randomValue > 0.8) {
                    treasureTier = LootTier.UNCOMMON;
                }
                break;
            case 1: // Uncommon
                goldCoins = 3 + (int)(randomValue * 4); // 3-6
                aspectCrystals = baseCrystals + 2;
                treasureTier = LootTier.UNCOMMON;
                if (randomValue > 0.7) {
                    treasureTier = LootTier.RARE;
                } else if (randomValue < 0.2) {
                    treasureTier = LootTier.COMMON;
                }
                break;
            case 2: // Rare
                goldCoins = 6 + (int)(randomValue * 5); // 6-10
                aspectCrystals = baseCrystals + 4;
                treasureTier = LootTier.RARE;
                if (randomValue < 0.3) {
                    treasureTier = LootTier.UNCOMMON;
                }
                break;
            default:
                goldCoins = 1;
                aspectCrystals = 1;
                treasureTier = LootTier.COMMON;
        }

        return new LootBagResult(goldCoins, aspectCrystals, treasureTier);
    }
}
