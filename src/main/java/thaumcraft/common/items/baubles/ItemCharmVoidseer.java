package thaumcraft.common.items.baubles;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.common.items.baubles.logic.BaublesCurioLogic;

public class ItemCharmVoidseer extends Item implements IWarpingGear {

    public ItemCharmVoidseer(Properties properties) {
        super(properties.stacksTo(1));
    }

    public ItemCharmVoidseer() {
        this(new Item.Properties());
    }

    @Override
    public int getWarp(ItemStack itemstack, Player player) {
        return BaublesCurioLogic.getVoidseerWarpBonus();
    }
}
