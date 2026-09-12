package thaumcraft.common.items.tools;

import java.util.Random;

/**
 * Pure Java simulation and mathematical logic for the Primal Crusher's yields.
 * <p>
 * Strict architectural decoupling: contains zero net.minecraft or net.neoforged imports,
 * allowing instant JUnit 5 test execution without FML runtime classloaders.
 */
public class PrimalCrusherYieldLogic {

    /**
     * Computes the number of secondary pulverized dust bonuses dropped when harvesting metal ores.
     *
     * @param isMetalOre true if the mined block is a metal ore
     * @param fortuneLevel the level of Fortune enchantment on the tool
     * @param random the random number generator
     * @return the number of bonus pulverized dusts to drop
     */
    public static int calculatePulverizedDustBonus(boolean isMetalOre, int fortuneLevel, Random random) {
        if (!isMetalOre) {
            return 0;
        }

        int bonus = 0;
        // Base 50% chance for 1 bonus dust
        if (random.nextFloat() < 0.5f) {
            bonus++;
        }

        // Additional chances based on fortune level
        for (int i = 0; i < fortuneLevel; i++) {
            if (random.nextFloat() < 0.2f) {
                bonus++;
            }
        }

        return bonus;
    }

    /**
     * Computes the number of secondary gem shards dropped when harvesting gem ores.
     *
     * @param isGemOre true if the mined block is a gem ore
     * @param fortuneLevel the level of Fortune enchantment on the tool
     * @param random the random number generator
     * @return the number of bonus gem shards to drop
     */
    public static int calculateGemShardChance(boolean isGemOre, int fortuneLevel, Random random) {
        if (!isGemOre) {
            return 0;
        }

        int shards = 0;
        // Base 25% chance for 1 bonus shard
        if (random.nextFloat() < 0.25f) {
            shards++;
        }

        // Fortune adds a scaling chance for more shards
        if (fortuneLevel > 0) {
            float fortuneChance = fortuneLevel * 0.15f;
            if (random.nextFloat() < fortuneChance) {
                shards++;
            }
        }

        return shards;
    }
}
