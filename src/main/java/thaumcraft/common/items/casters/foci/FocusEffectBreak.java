package thaumcraft.common.items.casters.foci;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.casters.FocusLogic;

public class FocusEffectBreak extends FocusEffect {

    public FocusEffectBreak() {
        super();
    }

    @Override
    public String getResearch() {
        return "FOCUSBREAK";
    }

    @Override
    public String getKey() {
        return "thaumcraft.BREAK";
    }

    @Override
    public Aspect getAspect() {
        return Aspect.ENTROPY;
    }

    @Override
    public int getComplexity() {
        return FocusLogic.calculateBreakComplexity(getSettingValue("power"), getSettingValue("silk"), getSettingValue("fortune"));
    }

    @Override
    public boolean execute(HitResult target, @Nullable Trajectory trajectory, float finalPower, int num) {
        if (target == null || getPackage() == null || getPackage().world == null) {
            return false;
        }
        Level world = getPackage().world;
        LivingEntity caster = getPackage().getCaster();

        if (target instanceof BlockHitResult bhr) {
            BlockPos pos = bhr.getBlockPos();
            BlockState state = world.getBlockState(pos);
            float hardness = state.getDestroySpeed(world, pos);
            if (hardness >= 0.0f) {
                float dur = FocusLogic.calculateBreakDurability(hardness);
                float strength = getSettingValue("power") * finalPower;
                int delay = FocusLogic.calculateBreakDelayTicks(dur, strength, num);
                boolean silk = getSettingValue("silk") > 0;
                int fortune = getSettingValue("fortune");
                float visFactor = FocusLogic.calculateBreakVisFactor(fortune, silk);

                world.destroyBlock(pos, true, caster);
                return true;
            }
        }
        return true;
    }

    @Override
    public NodeSetting[] createSettings() {
        int[] silk = { 0, 1 };
        String[] silkDesc = { "focus.common.no", "focus.common.yes" };
        int[] fortune = { 0, 1, 2, 3, 4 };
        String[] fortuneDesc = { "focus.common.no", "I", "II", "III", "IV" };
        return new NodeSetting[] {
                new NodeSetting("power", "focus.break.power", new NodeSetting.NodeSettingIntRange(1, 5)),
                new NodeSetting("fortune", "focus.common.fortune", new NodeSetting.NodeSettingIntList(fortune, fortuneDesc)),
                new NodeSetting("silk", "focus.common.silk", new NodeSetting.NodeSettingIntList(silk, silkDesc))
        };
    }

    @Override
    public void renderParticleFX(Level world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
        // Client side
    }

    @Override
    public void onCast(Entity caster) {
        if (caster != null && caster.level() != null) {
            caster.level().playSound(null, caster.blockPosition().above(), SoundEvents.END_GATEWAY_SPAWN, SoundSource.PLAYERS, 0.1f, 2.0f);
        }
    }
}
