package thaumcraft.api.golems.parts;
import net.minecraft.resources.Identifier;

import thaumcraft.api.golems.EnumGolemTrait;


import thaumcraft.api.golems.ThaumcraftGolemRegistries;

public class GolemAddon
{
    public String key;
    public String[] research;
    public Identifier icon;
    public Object[] components;
    public EnumGolemTrait[] traits;
    public IAddonFunction function;
    public PartModel model;
    
    public GolemAddon(String key, String[] research, Identifier icon, PartModel model, Object[] comp, EnumGolemTrait[] tags) {
        this.key = key;
        this.research = research;
        this.icon = icon;
        components = comp;
        traits = tags;
        this.model = model;
        function = null;
    }
    
    public GolemAddon(String key, String[] research, Identifier icon, PartModel model, Object[] comp, IAddonFunction function, EnumGolemTrait[] tags) {
        this(key, research, icon, model, comp, tags);
        this.function = function;
    }
    
    public String getLocalizedName() {
        return net.minecraft.network.chat.Component.translatable("golem.addon." + key.toLowerCase()).getString();
    }
    
    public String getLocalizedDescription() {
        return net.minecraft.network.chat.Component.translatable("golem.addon.text." + key.toLowerCase()).getString();
    }
    
    public static GolemAddon[] getAddons() {
        return ThaumcraftGolemRegistries.GOLEM_ADDONS.getEntries().stream()
                .map(net.neoforged.neoforge.registries.DeferredHolder::get)
                .toArray(GolemAddon[]::new);
    }
    
    public interface IAddonFunction extends IGenericFunction
    {
    }
}
