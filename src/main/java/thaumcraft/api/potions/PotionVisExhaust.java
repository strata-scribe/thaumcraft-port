package thaumcraft.api.potions;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.server.level.ServerLevel;

public class PotionVisExhaust extends MobEffect
{
    public static MobEffect instance = null; // will be instantiated at runtime
    
    public PotionVisExhaust(boolean isBad, int color)
    {
    	super(MobEffectCategory.HARMFUL, color);
    }
    
	@Override
	public boolean applyEffectTick(ServerLevel level, LivingEntity target, int amplifier) {
		return true;
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}
}
