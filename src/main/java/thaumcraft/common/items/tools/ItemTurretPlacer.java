package thaumcraft.common.items.tools;

import net.minecraft.world.item.Item;

public class ItemTurretPlacer extends Item {

    public ItemTurretPlacer(Properties properties) {
        super(properties);
    }

    public ItemTurretPlacer() {
        this(new Item.Properties().stacksTo(16));
    }
}
