package thaumcraft.common.items.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.common.items.tools.logic.EssentiaResonatorLogic;

public class ItemResonator extends Item {

    public ItemResonator(Properties properties) {
        super(properties);
    }

    public ItemResonator() {
        this(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (state != null && state.getBlock() != null) {
            try {
                var key = BuiltInRegistries.BLOCK.getKey(state.getBlock());
                if (key != null && EssentiaResonatorLogic.canInspectDevice(key.toString())) {
                    return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
                }
            } catch (Throwable ignored) {
            }
        }
        return InteractionResult.PASS;
    }
}
