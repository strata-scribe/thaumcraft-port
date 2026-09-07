package thaumcraft.common.blocks.devices;

import net.minecraft.world.level.block.Block;

public class BlockCondenserLattice extends Block {
    private final boolean dirty;

    public BlockCondenserLattice(Properties properties, boolean dirty) {
        super(properties);
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return dirty;
    }
}
