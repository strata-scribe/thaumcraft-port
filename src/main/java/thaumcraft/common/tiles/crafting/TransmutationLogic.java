package thaumcraft.common.tiles.crafting;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.util.HashMap;
import java.util.Map;

public class TransmutationLogic {

    public static final Map<String, AspectList> RECIPES = new HashMap<>();

    static {
        // Iron + Instrumentum/Praecantatio -> Thaumium Ingot
        AspectList thaumiumRecipe = new AspectList();
        thaumiumRecipe.add(Aspect.TOOL, 1);
        thaumiumRecipe.add(Aspect.MAGIC, 1);
        RECIPES.put("THAUMIUM", thaumiumRecipe);

        // Copper + Instrumentum -> Alchemical Brass
        AspectList brassRecipe = new AspectList();
        brassRecipe.add(Aspect.TOOL, 1);
        RECIPES.put("ALCHEMICAL_BRASS", brassRecipe);
    }

    /**
     * Calculates the purity of the transmutation catalyst based on required aspects
     * versus total aspects present.
     * @param available The aspects currently in the crucible.
     * @param required The aspects required for the recipe.
     * @return Purity percentage (0.0 to 1.0)
     */
    public static double calculatePurity(AspectList available, AspectList required) {
        if (available == null || required == null || required.size() == 0 || available.size() == 0) {
            return 0.0;
        }

        int requiredTotal = 0;
        int matchedAvailable = 0;

        for (Aspect aspect : required.getAspects()) {
            int reqAmt = required.getAmount(aspect);
            int availAmt = available.getAmount(aspect);

            requiredTotal += reqAmt;
            matchedAvailable += Math.min(reqAmt, availAmt);
        }

        if (requiredTotal == 0) return 0.0;

        double purity = (double) matchedAvailable / available.visSize();
        // The purity should also be limited by whether we have enough of the required
        if (matchedAvailable < requiredTotal) {
           return 0.0; // Cannot complete recipe, purity is effectively 0
        }
        return purity;
    }

    /**
     * Checks if a transmutation can occur.
     * @param outputItem The identifier for the output item (e.g. "THAUMIUM", "ALCHEMICAL_BRASS")
     * @param available The aspects available in the crucible.
     * @return true if the available aspects meet or exceed the required aspects.
     */
    public static boolean canTransmute(String outputItem, AspectList available) {
        if (!RECIPES.containsKey(outputItem) || available == null) {
            return false;
        }

        AspectList required = RECIPES.get(outputItem);
        for (Aspect aspect : required.getAspects()) {
            if (available.getAmount(aspect) < required.getAmount(aspect)) {
                return false;
            }
        }
        return true;
    }
}
