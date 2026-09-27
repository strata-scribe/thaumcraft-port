package thaumcraft.api.golems.parts;
import net.minecraft.resources.Identifier;

import thaumcraft.api.golems.EnumGolemTrait;


import thaumcraft.api.golems.ThaumcraftGolemRegistries;

public class GolemLeg
{
    public String key;
    public String[] research;
    public Identifier icon;
    public Object[] components;
    public EnumGolemTrait[] traits;
    public ILegFunction function;
    public PartModel model;
    
    public GolemLeg(String key, String[] research, Identifier icon, PartModel model, Object[] comp, EnumGolemTrait[] tags) {
        this.key = key;
        this.research = research;
        this.icon = icon;
        components = comp;
        traits = tags;
        this.model = model;
        function = null;
    }
    
    public GolemLeg(String key, String[] research, Identifier icon, PartModel model, Object[] comp, ILegFunction function, EnumGolemTrait[] tags) {
        this(key, research, icon, model, comp, tags);
        this.function = function;
    }
    
    public String getLocalizedName() {
        return net.minecraft.network.chat.Component.translatable("golem.leg." + key.toLowerCase()).getString();
    }
    
    public String getLocalizedDescription() {
        return net.minecraft.network.chat.Component.translatable("golem.leg.text." + key.toLowerCase()).getString();
    }
    
    public static GolemLeg[] getLegs() {
        return ThaumcraftGolemRegistries.GOLEM_LEGS.getEntries().stream()
                .map(net.neoforged.neoforge.registries.DeferredHolder::get)
                .toArray(GolemLeg[]::new);
    }
    
    public interface ILegFunction extends IGenericFunction
    {
    }
}
