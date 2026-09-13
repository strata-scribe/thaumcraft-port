package thaumcraft.common.entities.monster.tainted;

public class TaintedSlimeMergeLogic {

    /**
     * Calculates the new slime size after absorbing taint residue.
     * The size increases by 1 for every unit of residue absorbed, scaling non-linearly.
     * Specifically, it takes (currentSize + 1) residue to grow by 1 size.
     *
     * @param currentSize The current size of the tainted slime (usually 1, 2, 4).
     * @param absorbedResidue The amount of taint residue absorbed.
     * @return The new size of the slime.
     */
    public static int calculateSizeScaling(int currentSize, double absorbedResidue) {
        if (currentSize < 1) {
            currentSize = 1;
        }
        if (absorbedResidue <= 0) {
            return currentSize;
        }

        int newSize = currentSize;
        double remainingResidue = absorbedResidue;

        // Base cost to grow is dependent on the target size
        while (remainingResidue >= (newSize + 1)) {
            remainingResidue -= (newSize + 1);
            newSize++;
        }

        return newSize;
    }

    /**
     * Calculates the health threshold below which a tainted slime should split.
     * For instance, if health falls below this threshold, the slime splits into smaller slimes.
     *
     * @param size The size of the tainted slime.
     * @return The health threshold for splitting.
     */
    public static double calculateSplitHealthThreshold(int size) {
        if (size <= 1) {
            return 0.0; // Smallest slimes don't split, they just die
        }
        // Example logic: Splits when health falls below size^2 / 2.0 (e.g., size 4 -> health 8)
        return (size * size) / 2.0;
    }

}
