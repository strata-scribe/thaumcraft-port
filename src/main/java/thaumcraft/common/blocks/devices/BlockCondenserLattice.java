package thaumcraft.common.blocks.devices;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Block;

public class BlockCondenserLattice extends Block {
    public static final MapCodec<BlockCondenserLattice> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    propertiesCodec(),
                    Codec.BOOL.fieldOf("dirty").forGetter(BlockCondenserLattice::isDirty)
            ).apply(instance, BlockCondenserLattice::new)
    );

    private final boolean dirty;

    public BlockCondenserLattice(Properties properties, boolean dirty) {
        super(properties);
        this.dirty = dirty;
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    public boolean isDirty() {
        return dirty;
    }
}

