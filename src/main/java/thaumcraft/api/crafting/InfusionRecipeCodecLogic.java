package thaumcraft.api.crafting;

import java.util.List;
import java.util.Map;

public class InfusionRecipeCodecLogic {

    public static class RecipeData<I, S> {
        public final String research;
        public final S output;
        public final int instability;
        public final Map<String, Integer> aspects;
        public final I centralItem;
        public final List<I> recipe;

        public RecipeData(String research, S output, int instability, Map<String, Integer> aspects, I centralItem, List<I> recipe) {
            this.research = research;
            this.output = output;
            this.instability = instability;
            this.aspects = aspects;
            this.centralItem = centralItem;
            this.recipe = recipe;
        }

        public String getResearch() {
            return research;
        }

        public S getOutput() {
            return output;
        }

        public int getInstability() {
            return instability;
        }

        public Map<String, Integer> getAspects() {
            return aspects;
        }

        public I getCentralItem() {
            return centralItem;
        }

        public List<I> getRecipe() {
            return recipe;
        }
    }

}
