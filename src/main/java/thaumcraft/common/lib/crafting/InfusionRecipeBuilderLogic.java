package thaumcraft.common.lib.crafting;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Pure Java simulation of infusion recipe data structures,
 * completely decoupled from Minecraft/Forge classes for testability.
 */
public class InfusionRecipeBuilderLogic {
    private String name;
    private String research;
    private String centralItem;
    private String outputItem;
    private int instability;
    private final Map<String, Integer> aspects = new LinkedHashMap<>();
    private final List<String> ingredients = new ArrayList<>();

    public InfusionRecipeBuilderLogic name(String name) {
        this.name = name;
        return this;
    }

    public InfusionRecipeBuilderLogic research(String research) {
        this.research = research;
        return this;
    }

    public InfusionRecipeBuilderLogic centralItem(String centralItem) {
        this.centralItem = centralItem;
        return this;
    }

    public InfusionRecipeBuilderLogic outputItem(String outputItem) {
        this.outputItem = outputItem;
        return this;
    }

    public InfusionRecipeBuilderLogic instability(int instability) {
        this.instability = instability;
        return this;
    }

    public InfusionRecipeBuilderLogic addAspect(String aspect, int amount) {
        this.aspects.put(aspect, amount);
        return this;
    }

    public InfusionRecipeBuilderLogic addIngredient(String ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    public InfusionRecipeData build() {
        if (name == null || name.isEmpty()) throw new IllegalStateException("Recipe name is required");
        if (research == null || research.isEmpty()) throw new IllegalStateException("Research is required");
        if (centralItem == null || centralItem.isEmpty()) throw new IllegalStateException("Central item is required");
        if (outputItem == null || outputItem.isEmpty()) throw new IllegalStateException("Output item is required");
        if (instability < 0) throw new IllegalStateException("Instability cannot be negative");
        if (aspects.isEmpty()) throw new IllegalStateException("At least one aspect is required");
        if (ingredients.isEmpty()) throw new IllegalStateException("At least one ingredient is required");

        return new InfusionRecipeData(name, research, centralItem, outputItem, instability, new LinkedHashMap<>(aspects), new ArrayList<>(ingredients));
    }

    public static class InfusionRecipeData {
        public final String name;
        public final String research;
        public final String centralItem;
        public final String outputItem;
        public final int instability;
        public final Map<String, Integer> aspects;
        public final List<String> ingredients;

        public InfusionRecipeData(String name, String research, String centralItem, String outputItem, int instability, Map<String, Integer> aspects, List<String> ingredients) {
            this.name = name;
            this.research = research;
            this.centralItem = centralItem;
            this.outputItem = outputItem;
            this.instability = instability;
            this.aspects = aspects;
            this.ingredients = ingredients;
        }
    }
}
