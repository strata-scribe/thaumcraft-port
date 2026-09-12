package thaumcraft.api.crafting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.Identifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.util.LinkedHashMap;
import java.util.Map;

public class CrucibleRecipe implements IThaumcraftRecipe  {

	public static final MapCodec<CrucibleRecipe> CODEC = RecordCodecBuilder.<CrucibleRecipeJsonLogic.CrucibleRecipeData<ItemStack, Ingredient>>mapCodec(instance -> instance.group(
			Codec.STRING.optionalFieldOf("research", "").forGetter(CrucibleRecipeJsonLogic.CrucibleRecipeData::getResearch),
			Codec.STRING.optionalFieldOf("group", "").forGetter(CrucibleRecipeJsonLogic.CrucibleRecipeData::getGroup),
			ItemStack.CODEC.fieldOf("result").forGetter(CrucibleRecipeJsonLogic.CrucibleRecipeData::getResult),
			Ingredient.CODEC.fieldOf("catalyst").forGetter(CrucibleRecipeJsonLogic.CrucibleRecipeData::getCatalyst),
			Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("aspects").forGetter(CrucibleRecipeJsonLogic.CrucibleRecipeData::getAspects)
	).apply(instance, CrucibleRecipeJsonLogic.CrucibleRecipeData::new)).xmap(
			data -> {
				AspectList aspects = new AspectList();
				if (data.getAspects() != null) {
					data.getAspects().forEach((k, v) -> aspects.add(Aspect.getAspect(k), v));
				}
				CrucibleRecipe recipe = new CrucibleRecipe(data.getResearch(), data.getResult(), data.getCatalyst(), aspects);
				if (data.getGroup() != null && !data.getGroup().isEmpty()) {
					recipe.setGroup(Identifier.parse(data.getGroup()));
				}
				return recipe;
			},
			recipe -> {
				Map<String, Integer> aspectMap = new LinkedHashMap<>();
				if (recipe.getAspects() != null && recipe.getAspects().getAspects() != null) {
					for (Aspect a : recipe.getAspects().getAspects()) {
						if (a != null) {
							aspectMap.put(a.getTag(), recipe.getAspects().getAmount(a));
						}
					}
				}
				return new CrucibleRecipeJsonLogic.CrucibleRecipeData<>(
						recipe.getResearch(),
						recipe.getGroup(),
						recipe.getRecipeOutput(),
						recipe.getCatalyst(),
						aspectMap
				);
			}
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, CrucibleRecipe> STREAM_CODEC = StreamCodec.<RegistryFriendlyByteBuf, CrucibleRecipeJsonLogic.CrucibleRecipeData<ItemStack, Ingredient>, String, String, ItemStack, Ingredient, Map<String, Integer>>composite(
			ByteBufCodecs.STRING_UTF8, CrucibleRecipeJsonLogic.CrucibleRecipeData::getResearch,
			ByteBufCodecs.STRING_UTF8, CrucibleRecipeJsonLogic.CrucibleRecipeData::getGroup,
			ItemStack.STREAM_CODEC, CrucibleRecipeJsonLogic.CrucibleRecipeData::getResult,
			Ingredient.CONTENTS_STREAM_CODEC, CrucibleRecipeJsonLogic.CrucibleRecipeData::getCatalyst,
			ByteBufCodecs.map(LinkedHashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.INT), CrucibleRecipeJsonLogic.CrucibleRecipeData::getAspects,
			CrucibleRecipeJsonLogic.CrucibleRecipeData::new
	).map(
			data -> {
				AspectList aspects = new AspectList();
				if (data.getAspects() != null) {
					data.getAspects().forEach((k, v) -> aspects.add(Aspect.getAspect(k), v));
				}
				CrucibleRecipe recipe = new CrucibleRecipe(data.getResearch(), data.getResult(), data.getCatalyst(), aspects);
				if (data.getGroup() != null && !data.getGroup().isEmpty()) {
					recipe.setGroup(Identifier.parse(data.getGroup()));
				}
				return recipe;
			},
			recipe -> {
				Map<String, Integer> aspectMap = new LinkedHashMap<>();
				if (recipe.getAspects() != null && recipe.getAspects().getAspects() != null) {
					for (Aspect a : recipe.getAspects().getAspects()) {
						if (a != null) {
							aspectMap.put(a.getTag(), recipe.getAspects().getAmount(a));
						}
					}
				}
				return new CrucibleRecipeJsonLogic.CrucibleRecipeData<>(
						recipe.getResearch(),
						recipe.getGroup(),
						recipe.getRecipeOutput(),
						recipe.getCatalyst(),
						aspectMap
				);
			}
	);

	private ItemStack recipeOutput;	
	private Ingredient catalyst;
	private AspectList aspects;
	private String research;
	private String name;
	public int hash;
	
	
	public CrucibleRecipe(String researchKey, ItemStack result, Object catalyst, AspectList tags) {
		recipeOutput = result;
		name="";
		setAspects(tags);
		research = researchKey;
		setCatalyst(ThaumcraftApiHelper.getIngredient(catalyst));
		
		if (getCatalyst() == null)
        {
            throw new RuntimeException("Invalid crucible recipe catalyst: "+ catalyst);
        }
		
		generateHash();
	}
		
	private void generateHash() {
		StringBuilder hc = new StringBuilder(research);		
		hc.append(recipeOutput.toString());
		if (!recipeOutput.getComponentsPatch().isEmpty()) {
			hc.append(recipeOutput.getComponentsPatch().toString());
		}	
		getCatalyst().items().forEach(holder -> {
			ItemStack is = new ItemStack(holder.value());
			hc.append(is.toString());
			if (!is.getComponentsPatch().isEmpty()) {
				hc.append(is.getComponentsPatch().toString());
			}
		});
		hash = hc.toString().hashCode();
	}

	public boolean matches(AspectList itags, ItemStack cat) {	
		if (!getCatalyst().test(cat)) return false;		
		if (itags==null) return false;
		for (Aspect tag:getAspects().getAspects()) {
			if (itags.getAmount(tag)<getAspects().getAmount(tag)) return false;
		}
		return true;
	}
	
	public boolean catalystMatches(ItemStack cat) {
		return getCatalyst().test(cat);
	}
	
	public AspectList removeMatching(AspectList itags) {
		AspectList temptags = new AspectList();
		temptags.aspects.putAll(itags.aspects);		
		for (Aspect tag:getAspects().getAspects()) {
			temptags.remove(tag, getAspects().getAmount(tag));
		}		
		itags = temptags;
		return itags;
	}
	
	public ItemStack getRecipeOutput() {
		return recipeOutput;
	}

	@Override
	public String getResearch() {
		return research;
	}

	public Ingredient getCatalyst() {
		return catalyst;
	}

	public void setCatalyst(Ingredient catalyst) {
		this.catalyst = catalyst;
	}

	public AspectList getAspects() {
		return aspects;
	}

	public void setAspects(AspectList aspects) {
		this.aspects = aspects;
	}

	private String group="";
	
	@Override
	public String getGroup() {
		return group;
	}
	
	public CrucibleRecipe setGroup(Identifier s) {
		group=s.toString();
		return this;
	}
}
