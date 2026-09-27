package thaumcraft.common.items.consumables;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.items.ItemGenericEssentiaContainer;
import thaumcraft.common.tiles.crafting.CrucibleBlockEntity;
import thaumcraft.common.tiles.crafting.InfusionMatrixBlockEntity;
import thaumcraft.api.items.ThaumcraftItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemUtils;

public class ItemPhial extends ItemGenericEssentiaContainer {

    public ItemPhial(Item.Properties properties) {
        super(properties, 10);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        BlockEntity blockEntity = level.getBlockEntity(pos);

        AspectList aspects = getAspects(stack);

        if (aspects != null && aspects.size() > 0) {
            Aspect aspect = aspects.getAspects()[0];
            int amount = aspects.getAmount(aspect);

            boolean handled = false;

            if (blockEntity instanceof CrucibleBlockEntity crucible) {
                if (crucible.doesContainerAccept(aspect)) {
                    crucible.addToContainer(aspect, amount);
                    handled = true;
                }
            } else if (blockEntity instanceof InfusionMatrixBlockEntity matrix) {
                if (matrix.doesContainerAccept(aspect)) {
                    matrix.addToContainer(aspect, amount);
                    handled = true;
                }
            }

            if (handled) {
                if (player != null) {
                    if (!player.getAbilities().instabuild) {
                        ItemStack emptyPhial = new ItemStack(ThaumcraftItems.phial.get());
                        ItemStack newStack = ItemUtils.createFilledResult(stack, player, emptyPhial);
                        player.setItemInHand(context.getHand(), newStack);
                    }
                } else {
                    stack.shrink(1);
                }
                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 0.5f, 1.0f);
                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(context);
    }
}
