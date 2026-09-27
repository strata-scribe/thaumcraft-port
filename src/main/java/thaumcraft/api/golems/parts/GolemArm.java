package thaumcraft.api.golems.parts;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.resources.Identifier;

import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;


import thaumcraft.api.golems.ThaumcraftGolemRegistries;

public class GolemArm
{
    public String key;
    public String[] research;
    public Identifier icon;
    public Object[] components;
    public EnumGolemTrait[] traits;
    public IArmFunction function;
    public PartModel model;
    
    public GolemArm(String key, String[] research, Identifier icon, PartModel model, Object[] comp, EnumGolemTrait[] tags) {
        this.key = key;
        this.research = research;
        this.icon = icon;
        components = comp;
        traits = tags;
        this.model = model;
        function = null;
    }
    
    public GolemArm(String key, String[] research, Identifier icon, PartModel model, Object[] comp, IArmFunction function, EnumGolemTrait[] tags) {
        this(key, research, icon, model, comp, tags);
        this.function = function;
    }
    
    public String getLocalizedName() {
        return net.minecraft.network.chat.Component.translatable("golem.arm." + key.toLowerCase()).getString();
    }
    
    public String getLocalizedDescription() {
        return net.minecraft.network.chat.Component.translatable("golem.arm.text." + key.toLowerCase()).getString();
    }
    
    public static GolemArm[] getArms() {
        return ThaumcraftGolemRegistries.GOLEM_ARMS.getEntries().stream()
                .map(net.neoforged.neoforge.registries.DeferredHolder::get)
                .toArray(GolemArm[]::new);
    }
    
    public interface IArmFunction extends IGenericFunction
    {
        void onMeleeAttack(IGolemAPI p0, Entity p1);
        
        void onRangedAttack(IGolemAPI p0, LivingEntity p1, float p2);
        
        RangedAttackGoal getRangedAttackAI(RangedAttackMob p0);
    }
}
