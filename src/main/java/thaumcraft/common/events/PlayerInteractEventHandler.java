package thaumcraft.common.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.casters.CasterTriggerRegistry;
import thaumcraft.api.casters.ICaster;
import thaumcraft.api.casters.IInteractWithCaster;

@EventBusSubscriber(modid = Thaumcraft.MODID)
public class PlayerInteractEventHandler {

    @SubscribeEvent
    public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        ItemStack stack = event.getItemStack();

        if (stack.getItem() instanceof ICaster) {
            BlockState state = level.getBlockState(pos);

            if (CasterTriggerRegistry.hasTrigger(state)) {
                if (CasterTriggerRegistry.performTrigger(level, stack, player, pos, event.getFace(), state)) {
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    return;
                }
            }

            if (level.getBlockEntity(pos) instanceof IInteractWithCaster interact) {
                if (interact.onCasterRightClick(level, stack, player, pos, event.getFace(), event.getHand())) {
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    return;
                }
            }
        }
    }
}
