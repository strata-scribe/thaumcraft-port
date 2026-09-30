package thaumcraft.common.items.baubles;

import net.minecraft.world.item.Item;

public class ItemBandCuriosity extends Item {

    public ItemBandCuriosity(Properties properties) {
        super(properties.stacksTo(1));
    }

    public ItemBandCuriosity() {
        this(new Item.Properties());
    }
}
