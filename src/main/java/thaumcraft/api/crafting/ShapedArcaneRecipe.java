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

public class ShapedArcaneRecipe implements IArcaneRecipe {
	
	private String research;
	private int vis;
	private AspectList crystals;
	private int width;
	private int height;
	private NonNullList<Ingredient> recipeItems;
	private ItemStack recipeOutput;
	private String group = "";

	public ShapedArcaneRecipe(Identifier group, String res, int vis, AspectList crystals, Block result, Object... recipe){ this(group, res, vis, crystals, new ItemStack(result), recipe); }
    public ShapedArcaneRecipe(Identifier group, String res, int vis, AspectList crystals, Item result, Object... recipe){ this(group, res, vis, crystals, new ItemStack(result), recipe); }
    public ShapedArcaneRecipe(Identifier group, String res, int vis, AspectList crystals, @Nonnull ItemStack result, Object... recipe) {
		this.group = group != null ? group.toString() : "";
		this.research = res;
		this.vis = vis;
		this.crystals = crystals;
		this.recipeOutput = result;
		parseRecipe(recipe);
	}

	private void parseRecipe(Object[] recipe) {
		int idx = 0;
		java.util.List<String> rows = new java.util.ArrayList<>();
		while (idx < recipe.length && recipe[idx] instanceof String) {
			rows.add((String) recipe[idx]);
			idx++;
		}
		this.height = rows.size();
		this.width = rows.isEmpty() ? 0 : rows.get(0).length();
		
		java.util.Map<Character, Ingredient> map = new java.util.HashMap<>();
		while (idx < recipe.length) {
			Character chr = (Character) recipe[idx];
			Object val = recipe[idx + 1];
			map.put(chr, ThaumcraftApiHelper.getIngredient(val));
			idx += 2;
		}
		
		this.recipeItems = NonNullList.withSize(this.width * this.height, Ingredient.of());
		for (int r = 0; r < this.height; r++) {
			String row = rows.get(r);
			for (int c = 0; c < this.width; c++) {
				char key = row.charAt(c);
				Ingredient ing = map.getOrDefault(key, Ingredient.of());
				this.recipeItems.set(r * this.width + c, ing);
			}
		}
	}
	
	@Override
	public ItemStack assemble(RecipeInput var1) {
		return recipeOutput.copy();
	}
	
	private boolean checkMatch(RecipeInput inv, int startX, int startY, boolean mirror) {
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 3; c++) {
				int targetX = c - startX;
				int targetY = r - startY;
				Ingredient ing = Ingredient.of();
				if (targetX >= 0 && targetY >= 0 && targetX < this.width && targetY < this.height) {
					if (mirror) {
						ing = this.recipeItems.get(targetY * this.width + (this.width - 1 - targetX));
					} else {
						ing = this.recipeItems.get(targetY * this.width + targetX);
					}
				}
				ItemStack slotStack = inv.getItem(r * 3 + c);
				if (!ing.test(slotStack)) {
					return false;
				}
			}
		}
		return true;
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
		
		for (int startX = 0; startX <= 3 - this.width; startX++) {
			for (int startY = 0; startY <= 3 - this.height; startY++) {
				if (checkMatch(inv, startX, startY, false)) {
					return true;
				}
				if (checkMatch(inv, startX, startY, true)) {
					return true;
				}
			}
		}
		
		return false;
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
	
	public int getRecipeWidth() {
		return this.width;
	}

	public int getRecipeHeight() {
		return this.height;
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
		return this.recipeItems;
	}
}
