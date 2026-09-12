package thaumcraft.common.items.curios;

import java.util.Random;

/**
 * Pure Java simulation and mathematical logic for the Primal Charm.
 * <p>
 * Strict architectural decoupling: contains zero net.minecraft or net.neoforged imports,
 * allowing instant JUnit 5 test execution without FML runtime classloaders.
 */
public class PrimalCharmLogic {

    private static final String[] PRIMAL_ASPECTS = {
            "aer", "terra", "ignis", "aqua", "ordo", "perditio"
    };

    /**
     * Determines whether the Primal Charm should crystallize ambient vis into a crystal.
     *
     * @param chance The base chance threshold. E.g., if chance is 100, then 1 in 100 ticks it may crystallize.
     * @param random The random number generator to use.
     * @return true if it should crystallize, false otherwise.
     */
    public static boolean shouldCrystallize(int chance, Random random) {
        if (chance <= 0) return true;
        return random.nextInt(chance) == 0;
    }

    /**
     * Rolls a random primal aspect tag for the crystallization.
     *
     * @param random The random number generator to use.
     * @return A random primal aspect tag string.
     */
    public static String rollPrimalAspect(Random random) {
        return PRIMAL_ASPECTS[random.nextInt(PRIMAL_ASPECTS.length)];
    }
}
