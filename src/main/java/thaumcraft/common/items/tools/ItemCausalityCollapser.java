package thaumcraft.common.items.tools;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumcraft.common.items.tools.logic.CausalityCollapserLogic;

public class ItemCausalityCollapser extends Item {

    public ItemCausalityCollapser(Properties properties) {
        super(properties.stacksTo(16));
    }

    public ItemCausalityCollapser() {
        this(new Item.Properties());
    }

    public static InteractionResult sidedSuccess(boolean isClient) {
        return isClient ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            CausalityCollapserLogic.getExplosionRadius();
            CausalityCollapserLogic.canCollapseRift(10.0f, 10.0f);
            CausalityCollapserLogic.calculateVoidSeedDropCount(5.0f, level.getRandom().nextFloat());
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
        }
        return sidedSuccess(level.isClientSide());
    }
}
