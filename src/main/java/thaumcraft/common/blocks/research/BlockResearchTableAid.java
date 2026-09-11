package thaumcraft.common.blocks.research;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlockResearchTableAid extends Block {

    public static final com.mojang.serialization.MapCodec<BlockResearchTableAid> CODEC = simpleCodec(BlockResearchTableAid::new);

    @Override
    public com.mojang.serialization.MapCodec<BlockResearchTableAid> codec() {
        return CODEC;
    }

    public BlockResearchTableAid(BlockBehaviour.Properties properties) {
        super(properties);
    }
}
