package thaumcraft.common.tiles.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.blocks.devices.BlockRedstoneRelay;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;
import thaumcraft.common.lib.LevitatorRelayLogic;
import thaumcraft.common.tiles.essentia.JarBlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class TileRedstoneRelay extends BlockEntity {

    private int tickCount = 0;

    public TileRedstoneRelay(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.REDSTONE_RELAY.get(), pos, state);
    }


    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.tickCount = input.read("TickCount", com.mojang.serialization.Codec.INT).orElse(0);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("TickCount", com.mojang.serialization.Codec.INT, this.tickCount);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TileRedstoneRelay tile) {
        if (level.isClientSide()) {
            return;
        }

        tile.tickCount++;
        // Update signal every 10 ticks
        if (tile.tickCount % 10 == 0) {
            int newPower = 0;
            BlockEntity blockEntityBelow = level.getBlockEntity(pos.below());

            if (blockEntityBelow instanceof JarBlockEntity jar) {
                newPower = LevitatorRelayLogic.calculateRedstoneSignalFromEssentia(jar.getAmount(), JarBlockEntity.CAPACITY);
            } else {
                float vis = AuraHelper.getVis(level, pos);
                float maxVis = AuraHelper.getAuraBase(level, pos);
                newPower = LevitatorRelayLogic.calculateRedstoneSignalFromAura(vis, maxVis);
            }

            int currentPower = state.getValue(BlockRedstoneRelay.POWER);
            if (currentPower != newPower) {
                level.setBlockAndUpdate(pos, state.setValue(BlockRedstoneRelay.POWER, newPower).setValue(BlockRedstoneRelay.POWERED, newPower > 0));
            }
        }
    }
}
