package thaumcraft.common.items.curios;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.blocks.ThaumcraftBlocks;
import thaumcraft.api.items.ThaumcraftItems;

public class ItemSalisMundus extends Item {

    public ItemSalisMundus(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        SalisMundusLogic.TransformationTarget target = SalisMundusLogic.getTarget(blockId);

        if (target != SalisMundusLogic.TransformationTarget.NONE) {
            if (!level.isClientSide()) {
                switch (target) {
                    case TRANSFORM_WORKBENCH:
                        level.setBlock(pos, ThaumcraftBlocks.arcaneWorkbench.get().defaultBlockState(), 3);
                        level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        break;
                    case TRANSFORM_CRUCIBLE:
                        level.setBlock(pos, ThaumcraftBlocks.crucible.get().defaultBlockState(), 3);
                        level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        break;
                    case TRANSFORM_THAUMONOMICON:
                        level.destroyBlock(pos, false);
                        Block.popResource(level, pos, new ItemStack(ThaumcraftItems.thaumonomicon.get()));
                        level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                        break;
                    default:
                        break;
                }

                Player player = context.getPlayer();
                if (player == null || !player.isCreative()) {
                    context.getItemInHand().shrink(1);
                }
            }

            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }

        return super.useOn(context);
    }
}
