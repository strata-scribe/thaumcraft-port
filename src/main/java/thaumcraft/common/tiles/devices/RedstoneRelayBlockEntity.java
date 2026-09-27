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

public class RedstoneRelayBlockEntity extends BlockEntity {

    private int tickCount = 0;

    public RedstoneRelayBlockEntity(BlockPos pos, BlockState state) {
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

    public static void tick(Level level, BlockPos pos, BlockState state, RedstoneRelayBlockEntity tile) {
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

                thaumcraft.common.lib.RedstoneRelayLogic.transmitPower(new thaumcraft.common.lib.RedstoneRelayLogic.IEnvironment() {
                    @Override
                    public boolean isRedstoneRelay(int x, int y, int z) {
                        return level.getBlockState(new BlockPos(x, y, z)).getBlock() instanceof BlockRedstoneRelay;
                    }

                    @Override
                    public int getPower(int x, int y, int z) {
                        return level.getBlockState(new BlockPos(x, y, z)).getValue(BlockRedstoneRelay.POWER);
                    }

                    @Override
                    public void setPower(int x, int y, int z, int p) {
                        BlockPos pPos = new BlockPos(x, y, z);
                        BlockState targetState = level.getBlockState(pPos);
                        level.setBlockAndUpdate(pPos, targetState.setValue(BlockRedstoneRelay.POWER, p).setValue(BlockRedstoneRelay.POWERED, p > 0));
                    }

                    @Override
                    public boolean isLineOfSightBlocked(int x, int y, int z) {
                        BlockPos pPos = new BlockPos(x, y, z);
                        BlockState targetState = level.getBlockState(pPos);
                        return !targetState.isAir() && targetState.isRedstoneConductor(level, pPos);
                    }
                }, pos.getX(), pos.getY(), pos.getZ(), newPower);
            }
        }
    }
}
