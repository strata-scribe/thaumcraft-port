package thaumcraft.common.blocks.world;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PavingStoneTravelBlock extends Block {

    public static final MapCodec<PavingStoneTravelBlock> CODEC = simpleCodec(PavingStoneTravelBlock::new);

    public PavingStoneTravelBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            if (!level.isClientSide()) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.SPEED, 40, 1, false, false));
                livingEntity.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 40, 1, false, false));
            }
        }
        super.stepOn(level, pos, state, entity);
    }
}
