package thaumcraft.common.items.consumables;

import net.minecraft.world.item.Item;

public class ItemAlumentum extends Item {

    public ItemAlumentum(Properties properties) {
        super(properties);
    }

    public ItemAlumentum() {
        this(new Item.Properties().stacksTo(64));
    }
}
