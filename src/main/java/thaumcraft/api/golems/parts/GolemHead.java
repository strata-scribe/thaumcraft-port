package thaumcraft.api.golems.parts;
import net.minecraft.resources.Identifier;

import thaumcraft.api.golems.EnumGolemTrait;


import thaumcraft.api.golems.ThaumcraftGolemRegistries;

public class GolemHead
{
    public String key;
    public String[] research;
    public Identifier icon;
    public Object[] components;
    public EnumGolemTrait[] traits;
    public IHeadFunction function;
    public PartModel model;
    
    public GolemHead(String key, String[] research, Identifier icon, PartModel model, Object[] comp, EnumGolemTrait[] tags) {
        this.key = key;
        this.research = research;
        this.icon = icon;
        components = comp;
        traits = tags;
        this.model = model;
        function = null;
    }
    
    public GolemHead(String key, String[] research, Identifier icon, PartModel model, Object[] comp, IHeadFunction function, EnumGolemTrait[] tags) {
        this(key, research, icon, model, comp, tags);
        this.function = function;
    }
    
    public String getLocalizedName() {
        return net.minecraft.network.chat.Component.translatable("golem.head." + key.toLowerCase()).getString();
    }
    
    public String getLocalizedDescription() {
        return net.minecraft.network.chat.Component.translatable("golem.head.text." + key.toLowerCase()).getString();
    }
    
    public static GolemHead[] getHeads() {
        return ThaumcraftGolemRegistries.GOLEM_HEADS.getEntries().stream()
                .map(net.neoforged.neoforge.registries.DeferredHolder::get)
                .toArray(GolemHead[]::new);
    }
    
    public interface IHeadFunction extends IGenericFunction
    {
    }
}
