package thaumcraft.common.tiles.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;
import thaumcraft.common.tiles.devices.logic.VisBatteryStorageLogic;

public class VisBatteryBlockEntity extends BlockEntity {

    private final VisBatteryStorageLogic logic;

    public VisBatteryBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, 1000.0f, 1.0f, 1.0f);
    }

    public VisBatteryBlockEntity(BlockPos pos, BlockState state, float maxCapacity, float siphonRate, float dischargeRate) {
        super(ThaumcraftBlockEntities.VIS_BATTERY.get(), pos, state);
        this.logic = new VisBatteryStorageLogic(maxCapacity, siphonRate, dischargeRate);
    }

    public VisBatteryStorageLogic getLogic() {
        return logic;
    }

    public float getStoredVis() {
        return logic.getStoredVis();
    }

    public void setStoredVis(float storedVis) {
        logic.setStoredVis(storedVis);
        setChanged();
    }

    public float getMaxCapacity() {
        return logic.getMaxCapacity();
    }

    public boolean isFull() {
        return logic.isFull();
    }

    public boolean isEmpty() {
        return logic.isEmpty();
    }

    public float getFillRatio() {
        return logic.getFillRatio();
    }

    public float getRemainingCapacity() {
        return logic.getRemainingCapacity();
    }

    public float siphonFromAura(float availableAuraVis) {
        float drawn = logic.siphonFromAura(availableAuraVis);
        if (drawn > 0.0f) {
            setChanged();
        }
        return drawn;
    }

    public float dischargeToMachine(float requestedVis) {
        float discharged = logic.dischargeToMachine(requestedVis);
        if (discharged > 0.0f) {
            setChanged();
        }
        return discharged;
    }

    public void tickBattery(net.minecraft.world.level.Level level, BlockPos pos) {
        if (level == null || level.isClientSide()) return;
        boolean powered = level.hasNeighborSignal(pos);
        float currentVis = thaumcraft.api.aura.AuraHelper.getVis(level, pos);
        int baseAura = thaumcraft.api.aura.AuraHelper.getAuraBase(level, pos);

        if (powered) {
            float toDischarge = VisBatteryStorageLogic.calculateDischargeAmount(currentVis, baseAura, logic.getStoredVis(), logic.getDischargeRate(), true);
            if (toDischarge > 0.0f) {
                float discharged = dischargeToMachine(toDischarge);
                if (discharged > 0.0f) {
                    thaumcraft.api.aura.AuraHelper.addVis(level, pos, discharged);
                }
            }
            return;
        }

        if (VisBatteryStorageLogic.shouldAbsorb(currentVis, baseAura, false)) {
            float toAbsorb = VisBatteryStorageLogic.calculateAbsorbAmount(currentVis, baseAura, logic.getRemainingCapacity(), logic.getSiphonRate());
            if (toAbsorb > 0.0f) {
                float drained = thaumcraft.api.aura.AuraHelper.drainVis(level, pos, toAbsorb, false);
                if (drained > 0.0f) {
                    siphonFromAura(drained);
                }
            }
        } else if (VisBatteryStorageLogic.shouldDischarge(currentVis, baseAura, false, logic.getStoredVis())) {
            float toDischarge = VisBatteryStorageLogic.calculateDischargeAmount(currentVis, baseAura, logic.getStoredVis(), logic.getDischargeRate(), false);
            if (toDischarge > 0.0f) {
                float discharged = dischargeToMachine(toDischarge);
                if (discharged > 0.0f) {
                    thaumcraft.api.aura.AuraHelper.addVis(level, pos, discharged);
                }
            }
        }
    }

    public static void serverTick(net.minecraft.world.level.Level level, BlockPos pos, BlockState state, VisBatteryBlockEntity blockEntity) {
        if (blockEntity != null) {
            blockEntity.tickBattery(level, pos);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("storedVis", com.mojang.serialization.Codec.FLOAT, logic.getStoredVis());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        float storedVis = input.read("storedVis", com.mojang.serialization.Codec.FLOAT)
                .or(() -> input.read("vis", com.mojang.serialization.Codec.FLOAT))
                .or(() -> input.read("charge", com.mojang.serialization.Codec.FLOAT))
                .orElse(0.0f);
        logic.setStoredVis(storedVis);
    }
}
