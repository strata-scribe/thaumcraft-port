package thaumcraft.api.internal;

import java.util.ArrayList;
import net.minecraft.world.item.ItemStack;

public class WeightedRandomLoot {
	
	/** The Item/Block ID to generate in the bag. */
    public ItemStack item;
    public int weight;

    public WeightedRandomLoot(ItemStack stack, int weight)
    {
        this.item = stack;
        this.weight = weight;
    }
    
    public static ArrayList<WeightedRandomLoot> lootBagCommon = new ArrayList<WeightedRandomLoot>();
    public static ArrayList<WeightedRandomLoot> lootBagUncommon = new ArrayList<WeightedRandomLoot>();
    public static ArrayList<WeightedRandomLoot> lootBagRare = new ArrayList<WeightedRandomLoot>();
    
}
