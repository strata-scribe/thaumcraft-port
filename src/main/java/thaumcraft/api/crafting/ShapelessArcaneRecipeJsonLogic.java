package thaumcraft.api.crafting;

import java.util.List;
import java.util.Map;

public class ShapelessArcaneRecipeJsonLogic {

    public static class RecipeData<I, S> {
        private final String group;
        private final String research;
        private final int vis;
        private final Map<String, Integer> crystals;
        private final List<I> ingredients;
        private final S result;

        public RecipeData(String group, String research, int vis, Map<String, Integer> crystals, List<I> ingredients, S result) {
            if (vis < 0) {
                throw new IllegalArgumentException("Vis cost must be >= 0");
            }
            if (ingredients == null || ingredients.isEmpty() || ingredients.size() > 9) {
                throw new IllegalArgumentException("Recipe must have between 1 and 9 ingredients");
            }
            if (result == null) {
                throw new IllegalArgumentException("Recipe result cannot be null");
            }
            if (crystals != null) {
                for (Map.Entry<String, Integer> entry : crystals.entrySet()) {
                    if (entry.getValue() == null || entry.getValue() <= 0) {
                        throw new IllegalArgumentException("Crystal amounts must be greater than 0");
                    }
                }
            }

            this.group = group != null ? group : "";
            this.research = research != null ? research : "";
            this.vis = vis;
            this.crystals = crystals != null ? Map.copyOf(crystals) : Map.of();
            this.ingredients = List.copyOf(ingredients);
            this.result = result;
        }

        public String getGroup() {
            return group;
        }

        public String getResearch() {
            return research;
        }

        public int getVis() {
            return vis;
        }

        public Map<String, Integer> getCrystals() {
            return crystals;
        }

        public List<I> getIngredients() {
            return ingredients;
        }

        public S getResult() {
            return result;
        }
    }
}
