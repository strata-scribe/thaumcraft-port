package thaumcraft.common.tiles.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;

public class TileTubeFilter extends TileTube {

    public TileTubeFilter(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.TUBE_FILTER.get(), pos, state);
    }

    @Override
    protected TubeLogic createLogic() {
        return new FilteredTubeLogic(this::setChanged);
    }

    public FilteredTubeLogic getFilteredLogic() {
        return (FilteredTubeLogic) this.logic;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        Aspect filter = getFilteredLogic().getAspectFilter();
        if (filter != null) {
            output.store("AspectFilter", com.mojang.serialization.Codec.STRING, filter.getTag());
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        Aspect filter = input.read("AspectFilter", com.mojang.serialization.Codec.STRING)
                .map(Aspect::getAspect)
                .orElse(null);
        getFilteredLogic().setAspectFilter(filter);
    }
}
