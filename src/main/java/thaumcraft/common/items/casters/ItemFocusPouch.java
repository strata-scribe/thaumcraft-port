package thaumcraft.common.items.casters;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemFocusPouch extends Item {

    public ItemFocusPouch(Properties properties) {
        super(properties.stacksTo(1));
    }

    public ItemFocusPouch() {
        this(new Item.Properties());
    }

    public int getCapacity(ItemStack stack) {
        return 18;
    }
}
