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
        super(ThaumcraftBlockEntities.VIS_BATTERY.get(), pos, state);
        this.logic = new VisBatteryStorageLogic(1000f, 1f, 1f);
    }

    public VisBatteryStorageLogic getLogic() {
        return logic;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("storedVis", com.mojang.serialization.Codec.FLOAT, logic.getStoredVis());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        float storedVis = input.read("storedVis", com.mojang.serialization.Codec.FLOAT).orElse(0.0f);
        logic.setStoredVis(storedVis);
    }
}
