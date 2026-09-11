package thaumcraft.common.entities.monster.pech;

public class PechBarterLogic {

    /**
     * Evaluates item qualities and returns its barter weight (value).
     * 0 means the Pech is not interested.
     * Higher values mean better loot tier.
     */
    public static int getBarterValue(boolean isGoldNugget, boolean isGoldIngot, boolean isGoldBlock, boolean isGem, int aspectVisSize) {
        int value = 0;

        if (isGoldNugget) {
            value += 1;
        } else if (isGoldIngot) {
            value += 3;
        } else if (isGoldBlock) {
            value += 27;
        } else if (isGem) {
            value += 5;
        }

        if (aspectVisSize > 0) {
            value += aspectVisSize / 2; // Arbitrary value formula: half of total vis
        }

        return value;
    }
}
