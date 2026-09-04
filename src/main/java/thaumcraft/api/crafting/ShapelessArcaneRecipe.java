package thaumcraft.api.crafting;

import javax.annotation.Nonnull;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.items.ItemsTC;

public class ShapelessArcaneRecipe implements IArcaneRecipe {
	private String research;
	private int vis;
	private AspectList crystals;	
	private NonNullList<Ingredient> components;
	private ItemStack recipeOutput;
	private String group = "";

	public ShapelessArcaneRecipe(Identifier group, String res, int vis, AspectList crystals, Block result, Object... recipe){ this(group, res, vis, crystals, new ItemStack(result), recipe); }
    public ShapelessArcaneRecipe(Identifier group, String res, int vis, AspectList crystals, Item  result, Object... recipe){ this(group, res, vis, crystals, new ItemStack(result), recipe); }
    public ShapelessArcaneRecipe(Identifier group, String res, int vis, AspectList crystals, NonNullList<Ingredient> input, @Nonnull ItemStack result)
    {
    	this.group = group != null ? group.toString() : "";
    	this.components = input;
    	this.recipeOutput = result;
    	this.research = res;
		this.vis = vis;
		this.crystals = crystals;
    }
	public ShapelessArcaneRecipe(Identifier group, String res, int vis, AspectList crystals, ItemStack result, Object[] recipe) {
		this.group = group != null ? group.toString() : "";
		this.recipeOutput = result;
		this.research = res;
		this.vis = vis;
		this.crystals = crystals;
		this.components = NonNullList.create();
		for (Object in : recipe) {
			this.components.add(ThaumcraftApiHelper.getIngredient(in));
		}
	}
	
	@Override
	public ItemStack assemble(RecipeInput var1) {
		return recipeOutput.copy();
	}
	
	@Override
	public boolean matches(RecipeInput inv, Level world) {
		if (inv.size() < 15) return false;
		
		if (crystals != null) {
			for (Aspect aspect : crystals.getAspects()) {
				ItemStack cs = ThaumcraftApiHelper.makeCrystal(aspect, crystals.getAmount(aspect));
				boolean b = false;
				for (int i = 0; i < 6; ++i) {
					ItemStack itemstack1 = inv.getItem(9 + i);
					if (!itemstack1.isEmpty() && itemstack1.getItem() == ItemsTC.crystalEssence && itemstack1.getCount() >= cs.getCount() && ThaumcraftInvHelper.areItemStackTagsEqualRelaxed(cs, itemstack1)) {
						b = true;
						break;
					}
				}
				if (!b) return false;
			}
		}
		
		java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
		for (int i = 0; i < 9; i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				inputs.add(stack);
			}
		}
		
		java.util.List<Ingredient> recipeIngredients = new java.util.ArrayList<>(this.components);
		if (inputs.size() != recipeIngredients.size()) return false;
		
		for (ItemStack stack : inputs) {
			boolean matched = false;
			for (int i = 0; i < recipeIngredients.size(); i++) {
				if (recipeIngredients.get(i).test(stack)) {
					recipeIngredients.remove(i);
					matched = true;
					break;
				}
			}
			if (!matched) return false;
		}
		
		return recipeIngredients.isEmpty();
	}
	
	@Override
	public int getVis() {
		return vis;
	}

	@Override
	public String getResearch() {
		return research;
	}

	@Override
	public AspectList getCrystals() {
		return crystals;
	}

	@Override
	public boolean showNotification() {
		return true;
	}

	@Override
	public String group() {
		return group;
	}

	@Override
	public String getGroup() {
		return group;
	}

	@Override
	public RecipeSerializer<? extends Recipe<RecipeInput>> getSerializer() {
		return null;
	}

	@Override
	public RecipeType<? extends Recipe<RecipeInput>> getType() {
		return null;
	}

	@Override
	public net.minecraft.world.item.crafting.PlacementInfo placementInfo() {
		return null;
	}

	@Override
	public net.minecraft.world.item.crafting.RecipeBookCategory recipeBookCategory() {
		return null;
	}

	public NonNullList<Ingredient> getIngredients() {
		return this.components;
	}
}
