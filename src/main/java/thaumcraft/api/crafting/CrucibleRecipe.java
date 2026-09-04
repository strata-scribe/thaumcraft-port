package thaumcraft.api.crafting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.Identifier;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;


public class CrucibleRecipe implements IThaumcraftRecipe  {

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
