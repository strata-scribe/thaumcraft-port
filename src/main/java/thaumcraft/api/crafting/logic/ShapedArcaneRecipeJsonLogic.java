package thaumcraft.api.crafting.logic;

import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

public class ShapedArcaneRecipeJsonLogic {

    public static class RecipeData<I, S> {
        public final String research;
        public final int vis;
        public final Map<String, Integer> crystals;
        public final S output;
        public final String group;
        public final List<String> pattern;
        public final Map<String, I> key;

        public RecipeData(String group, String research, int vis, Map<String, Integer> crystals, S output, List<String> pattern, Map<String, I> key) {
            this.group = group;
            this.research = research;
            this.vis = vis;
            this.crystals = crystals;
            this.output = output;
            this.pattern = pattern;
            this.key = key;
        }

        public String getGroup() { return group; }
        public String getResearch() { return research; }
        public int getVis() { return vis; }
        public Map<String, Integer> getCrystals() { return crystals; }
        public S getOutput() { return output; }
        public List<String> getPattern() { return pattern; }
        public Map<String, I> getKey() { return key; }
    }

    public <I, S> void validate(RecipeData<I, S> data) {
        if (data.getPattern() == null || data.getPattern().isEmpty()) {
            throw new IllegalArgumentException("Pattern cannot be empty");
        }

        if (data.getPattern().size() > 3) {
            throw new IllegalArgumentException("Pattern cannot have more than 3 rows");
        }

        if (data.getKey() == null || data.getKey().isEmpty()) {
            throw new IllegalArgumentException("Key mapping cannot be null or empty");
        }

        int length = data.getPattern().get(0).length();
        if (length == 0 || length > 3) {
            throw new IllegalArgumentException("Pattern rows must have length between 1 and 3");
        }

        Set<String> usedKeys = new HashSet<>();

        for (String row : data.getPattern()) {
            if (row.length() != length) {
                throw new IllegalArgumentException("Pattern rows must all have the same length");
            }
            for (char c : row.toCharArray()) {
                if (c == ' ') continue;
                String k = String.valueOf(c);
                if (!data.getKey().containsKey(k)) {
                    throw new IllegalArgumentException("Pattern contains unknown key: " + k);
                }
                usedKeys.add(k);
            }
        }

        for (String key : data.getKey().keySet()) {
            if (!usedKeys.contains(key)) {
                throw new IllegalArgumentException("Key defined but not used in pattern: " + key);
            }
        }
    }
}
