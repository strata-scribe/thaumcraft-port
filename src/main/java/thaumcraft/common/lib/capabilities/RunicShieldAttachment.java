package thaumcraft.common.lib.capabilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class RunicShieldAttachment {

    public static final Codec<RunicShieldAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("currentShield").forGetter(RunicShieldAttachment::getCurrentShield),
            Codec.INT.fieldOf("maxShield").forGetter(RunicShieldAttachment::getMaxShield),
            Codec.INT.fieldOf("rechargeDelay").forGetter(RunicShieldAttachment::getRechargeDelay)
    ).apply(instance, RunicShieldAttachment::new));

    public static final com.mojang.serialization.MapCodec<RunicShieldAttachment> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("currentShield").forGetter(RunicShieldAttachment::getCurrentShield),
            Codec.INT.fieldOf("maxShield").forGetter(RunicShieldAttachment::getMaxShield),
            Codec.INT.fieldOf("rechargeDelay").forGetter(RunicShieldAttachment::getRechargeDelay)
    ).apply(instance, RunicShieldAttachment::new));

    private int currentShield;
    private int maxShield;
    private int rechargeDelay;

    public RunicShieldAttachment() {
        this(0, 0, 0);
    }

    public RunicShieldAttachment(int currentShield, int maxShield, int rechargeDelay) {
        this.currentShield = currentShield;
        this.maxShield = maxShield;
        this.rechargeDelay = rechargeDelay;
    }

    public int getCurrentShield() {
        return currentShield;
    }

    public void setCurrentShield(int currentShield) {
        this.currentShield = currentShield;
    }

    public int getMaxShield() {
        return maxShield;
    }

    public void setMaxShield(int maxShield) {
        this.maxShield = maxShield;
    }

    public int getRechargeDelay() {
        return rechargeDelay;
    }

    public void setRechargeDelay(int rechargeDelay) {
        this.rechargeDelay = rechargeDelay;
    }
}
