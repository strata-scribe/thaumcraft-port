package thaumcraft.common.items.curios;

import net.minecraft.world.item.Item;

public class ItemCurio extends Item {

    public ItemCurio(Properties properties) {
        super(properties);
    }

    public ItemCurio() {
        this(new Item.Properties().stacksTo(64));
    }
}
