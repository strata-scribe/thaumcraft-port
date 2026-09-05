package thaumcraft.api.golems.parts;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.Identifier;

import thaumcraft.api.golems.EnumGolemTrait;


public class GolemMaterial
{
    public String key;
    public String[] research;
    public Identifier texture;
    public int itemColor;
    public int healthMod;
    public int armor;
    public int damage;
    public ItemStack componentBase;
    public ItemStack componentMechanism;
    public EnumGolemTrait[] traits;
    
    public GolemMaterial(String key, String[] research, Identifier texture, int itemColor, int hp, int armor, int damage, ItemStack compb, ItemStack compm, EnumGolemTrait[] tags) {
        this.key = key;
        this.research = research;
        this.texture = texture;
        this.itemColor = itemColor;
        componentBase = compb;
        componentMechanism = compm;
        healthMod = hp;
        this.armor = armor;
        traits = tags;
        this.damage = damage;
    }
    
    public String getLocalizedName() {
        return net.minecraft.network.chat.Component.translatable("golem.material." + key.toLowerCase()).getString();
    }
    
    public String getLocalizedDescription() {
        return net.minecraft.network.chat.Component.translatable("golem.material.text." + key.toLowerCase()).getString();
    }
}
