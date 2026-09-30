package thaumcraft.common.items.consumables;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumcraft.common.items.consumables.logic.BottleTaintLogic;

public class ItemBottleTaint extends Item {

    public ItemBottleTaint(Properties properties) {
        super(properties.stacksTo(16));
    }

    public ItemBottleTaint() {
        this(new Item.Properties());
    }

    public static InteractionResult sidedSuccess(boolean isClient) {
        return isClient ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            BottleTaintLogic.calculateFluxAuraPollution(1);
            BottleTaintLogic.calculateTaintSpreadRadius(1);
            BottleTaintLogic.getTaintPoisonDurationTicks();
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
        }
        return sidedSuccess(level.isClientSide());
    }
}
