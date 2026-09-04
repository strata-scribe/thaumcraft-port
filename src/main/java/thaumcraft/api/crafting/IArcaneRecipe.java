package thaumcraft.api.crafting;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import thaumcraft.api.aspects.AspectList;


public interface IArcaneRecipe extends Recipe<RecipeInput>, IThaumcraftRecipe
{	
    public int getVis();
    public String getResearch();    
    public AspectList getCrystals();
}
