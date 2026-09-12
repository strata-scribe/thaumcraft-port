package thaumcraft.api.crafting;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;



public class InfusionRecipe implements IThaumcraftRecipe
{






	public static final MapCodec<InfusionRecipe> CODEC = RecordCodecBuilder.<InfusionRecipeCodecLogic.RecipeData<Ingredient, ItemStack>>mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("research").forGetter(InfusionRecipeCodecLogic.RecipeData::getResearch),
            ItemStack.CODEC.fieldOf("result").forGetter(InfusionRecipeCodecLogic.RecipeData::getOutput),
            Codec.INT.fieldOf("instability").forGetter(InfusionRecipeCodecLogic.RecipeData::getInstability),
            Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("aspects").forGetter(InfusionRecipeCodecLogic.RecipeData::getAspects),
            Ingredient.CODEC.fieldOf("central_item").forGetter(InfusionRecipeCodecLogic.RecipeData::getCentralItem),
            Ingredient.CODEC.listOf().fieldOf("recipe").forGetter(InfusionRecipeCodecLogic.RecipeData::getRecipe)
    ).apply(instance, InfusionRecipeCodecLogic.RecipeData::new)).xmap(
            data -> {
                AspectList aspects = new AspectList();
                data.getAspects().forEach((k, v) -> aspects.add(Aspect.getAspect(k), v));
                return new InfusionRecipe(data.getResearch(), data.getOutput(), data.getInstability(), aspects, data.getCentralItem(), data.getRecipe().toArray());
            },
            recipe -> {
                java.util.Map<String, Integer> aspectMap = new java.util.LinkedHashMap<>();
                if (recipe.getAspects() != null && recipe.getAspects().getAspects() != null) {
                    for (Aspect a : recipe.getAspects().getAspects()) {
                        if (a != null) {
                            aspectMap.put(a.getTag(), recipe.getAspects().getAmount(a));
                        }
                    }
                }
                return new InfusionRecipeCodecLogic.RecipeData<>(
                        recipe.getResearch(),
                        recipe.recipeOutput instanceof ItemStack ? (ItemStack) recipe.recipeOutput : ItemStack.EMPTY,
                        recipe.instability,
                        aspectMap,
                        recipe.getRecipeInput(),
                        new ArrayList<>(recipe.getComponents())
                );
            }
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, InfusionRecipe> STREAM_CODEC = StreamCodec.<RegistryFriendlyByteBuf, InfusionRecipeCodecLogic.RecipeData<Ingredient, ItemStack>, String, ItemStack, Integer, java.util.Map<String, Integer>, Ingredient, List<Ingredient>>composite(
            ByteBufCodecs.STRING_UTF8, InfusionRecipeCodecLogic.RecipeData::getResearch,
            ItemStack.STREAM_CODEC, InfusionRecipeCodecLogic.RecipeData::getOutput,
            ByteBufCodecs.INT, InfusionRecipeCodecLogic.RecipeData::getInstability,
            ByteBufCodecs.map(java.util.LinkedHashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.INT), InfusionRecipeCodecLogic.RecipeData::getAspects,
            Ingredient.CONTENTS_STREAM_CODEC, InfusionRecipeCodecLogic.RecipeData::getCentralItem,
            ByteBufCodecs.collection(java.util.ArrayList::new, Ingredient.CONTENTS_STREAM_CODEC), InfusionRecipeCodecLogic.RecipeData::getRecipe,
            InfusionRecipeCodecLogic.RecipeData::new
    ).map(
            data -> {
                AspectList aspects = new AspectList();
                data.getAspects().forEach((k, v) -> aspects.add(Aspect.getAspect(k), v));
                return new InfusionRecipe(data.getResearch(), data.getOutput(), data.getInstability(), aspects, data.getCentralItem(), data.getRecipe().toArray());
            },
            recipe -> {
                java.util.Map<String, Integer> aspectMap = new java.util.LinkedHashMap<>();
                if (recipe.getAspects() != null && recipe.getAspects().getAspects() != null) {
                    for (Aspect a : recipe.getAspects().getAspects()) {
                        if (a != null) {
                            aspectMap.put(a.getTag(), recipe.getAspects().getAmount(a));
                        }
                    }
                }
                return new InfusionRecipeCodecLogic.RecipeData<>(
                        recipe.getResearch(),
                        recipe.recipeOutput instanceof ItemStack ? (ItemStack) recipe.recipeOutput : ItemStack.EMPTY,
                        recipe.instability,
                        aspectMap,
                        recipe.getRecipeInput(),
                        new ArrayList<>(recipe.getComponents())
                );
            }
    );

	public AspectList aspects;
	public String research;
	private String name;
	protected NonNullList<Ingredient> components = NonNullList.create();
	public Ingredient sourceInput; //Use Ingredient.of() if the source item can be anything
	public Object recipeOutput;
	public int instability;
	
	public InfusionRecipe(String research, Object outputResult, int inst, AspectList aspects2, Object centralItem, Object ... recipe) {
		name="";
		this.research = research;
		recipeOutput = outputResult;
		aspects = aspects2;
		instability = inst;
		sourceInput = ThaumcraftApiHelper.getIngredient(centralItem);
		if (sourceInput==null) {
			String ret = "Invalid infusion central item: "+centralItem;
            throw new RuntimeException(ret);
		}		
		for (Object in : recipe)
        {
            Ingredient ing = ThaumcraftApiHelper.getIngredient(in);
            if (ing != null) {
            	components.add(ing);
            } else {
                String ret = "Invalid infusion recipe: ";
                for (Object tmp :  recipe)
                {
                    ret += tmp + ", ";
                }
                ret += outputResult;
                throw new RuntimeException(ret);
            }
        }
	}

	/**
     * Used to check if a recipe matches current crafting inventory
     * @param player 
     */
	public boolean matches(List<ItemStack> input, ItemStack central, Level world, Player player) {
		if (getRecipeInput()==null) return false;			
		if (!ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(research)) {
    		return false;
    	}		
		return (getRecipeInput().isEmpty() || getRecipeInput().test(central)) && RecipeMatcher.findMatches(input, getComponents()) != null;
    }
    
	@Override
    public String getResearch() {
		return research;
    }
    
	public Ingredient getRecipeInput() {
		return sourceInput;
	}

	public NonNullList<Ingredient> getComponents() {
		return components;
	}
	
	public Object getRecipeOutput() {
		return recipeOutput;
	}
	
	public AspectList getAspects() {
		return aspects;
	}			
	
	public Object getRecipeOutput(Player player, ItemStack input, List<ItemStack> comps ) {
		return recipeOutput;
    }
    
    public AspectList getAspects(Player player, ItemStack input, List<ItemStack> comps) {
		return aspects;
    }
    
    public int getInstability(Player player, ItemStack input, List<ItemStack> comps) {
		return instability;
    }
    
    private String group="";
	
	@Override
	public String getGroup() {
		return group;
	}
	
	public InfusionRecipe setGroup(Identifier s) {
		group=s.toString();
		return this;
	}
}
