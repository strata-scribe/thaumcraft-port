package thaumcraft.common.tiles.devices;

import java.util.Random;

public class ArcaneBoreEnchantmentLogic {

    /**
     * Determines if a block should be mined with Silk Touch.
     * @param hasSilkTouchEnchantment true if the bore has Silk Touch
     * @param blockIsSilkTouchable true if the block supports Silk Touch (e.g., stone, glass, ores)
     * @return true if the block should be dropped as itself
     */
    public static boolean shouldSilkTouch(boolean hasSilkTouchEnchantment, boolean blockIsSilkTouchable) {
        return hasSilkTouchEnchantment && blockIsSilkTouchable;
    }

    /**
     * Calculates the drop count applying standard Fortune logic.
     * @param baseCount the default number of items dropped
     * @param fortuneLevel the level of Fortune enchantment
     * @param random a random number generator
     * @return the resulting drop count
     */
    public static int calculateFortuneDropCount(int baseCount, int fortuneLevel, Random random) {
        if (fortuneLevel <= 0) {
            return baseCount;
        }
        int multiplier = random.nextInt(fortuneLevel + 2) - 1;
        if (multiplier < 0) {
            multiplier = 0;
        }
        return baseCount * (multiplier + 1);
    }

    /**
     * Calculates the final drop yield, prioritizing Silk Touch over Fortune.
     * @param hasSilkTouchEnchantment true if the bore has Silk Touch
     * @param blockIsSilkTouchable true if the block supports Silk Touch
     * @param baseCount the default number of items dropped if not using Silk Touch
     * @param fortuneLevel the level of Fortune enchantment
     * @param random a random number generator
     * @return the final count of items to drop
     */
    public static int getFinalYield(boolean hasSilkTouchEnchantment, boolean blockIsSilkTouchable, int baseCount, int fortuneLevel, Random random) {
        if (shouldSilkTouch(hasSilkTouchEnchantment, blockIsSilkTouchable)) {
            return 1;
        }
        return calculateFortuneDropCount(baseCount, fortuneLevel, random);
    }
}
