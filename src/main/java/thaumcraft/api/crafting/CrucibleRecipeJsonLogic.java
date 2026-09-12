package thaumcraft.api.crafting;

import java.util.Map;
import java.util.LinkedHashMap;

public class CrucibleRecipeJsonLogic {

    public static class CrucibleRecipeData<S, I> {
        public final String research;
        public final String group;
        public final S result;
        public final I catalyst;
        public final Map<String, Integer> aspects;

        public CrucibleRecipeData(String research, String group, S result, I catalyst, Map<String, Integer> aspects) {
            this.research = research != null ? research : "";
            this.group = group != null ? group : "";
            this.result = result;
            this.catalyst = catalyst;
            this.aspects = aspects;
            validate();
        }

        private void validate() {
            if (result == null) {
                throw new IllegalArgumentException("Crucible recipe must have a result");
            }
            if (catalyst == null) {
                throw new IllegalArgumentException("Crucible recipe must have a catalyst");
            }
            if (aspects == null || aspects.isEmpty()) {
                throw new IllegalArgumentException("Crucible recipe must have at least one aspect");
            }
            for (Map.Entry<String, Integer> entry : aspects.entrySet()) {
                if (entry.getValue() == null || entry.getValue() <= 0) {
                    throw new IllegalArgumentException("Crucible recipe aspect amounts must be greater than zero");
                }
            }
        }

        public String getResearch() {
            return research;
        }

        public String getGroup() {
            return group;
        }

        public S getResult() {
            return result;
        }

        public I getCatalyst() {
            return catalyst;
        }

        public Map<String, Integer> getAspects() {
            return aspects;
        }
    }
}
