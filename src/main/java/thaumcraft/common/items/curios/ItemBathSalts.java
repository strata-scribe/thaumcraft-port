package thaumcraft.common.items.curios;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.blocks.ThaumcraftBlocks;

public class ItemBathSalts extends Item {

    public ItemBathSalts(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (state.is(Blocks.WATER)) {
            if (!level.isClientSide()) {
                level.setBlock(pos, ThaumcraftBlocks.purifyingFluid.get().defaultBlockState(), 3);
                level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);

                Player player = context.getPlayer();
                if (player != null && !player.isCreative()) {
                    context.getItemInHand().shrink(1);
                }
            }
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }

        return super.useOn(context);
    }
}
