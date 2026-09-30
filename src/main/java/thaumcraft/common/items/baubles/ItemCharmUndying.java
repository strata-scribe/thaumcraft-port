package thaumcraft.common.items.baubles;

import net.minecraft.world.item.Item;

public class ItemCharmUndying extends Item {

    public ItemCharmUndying(Properties properties) {
        super(properties.stacksTo(1));
    }

    public ItemCharmUndying() {
        this(new Item.Properties());
    }
}
