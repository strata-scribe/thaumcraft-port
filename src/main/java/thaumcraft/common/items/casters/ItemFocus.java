package thaumcraft.common.items.casters;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomData;
import thaumcraft.Thaumcraft;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.common.casters.FocusLogic;
import thaumcraft.common.items.casters.foci.*;

public class ItemFocus extends Item {

    private final int maxComplexity;

    public ItemFocus(Properties properties, int maxComplexity) {
        super(properties.stacksTo(1).rarity(Rarity.RARE));
        this.maxComplexity = maxComplexity;
    }

    public ItemFocus(Properties properties) {
        this(properties, 15);
    }

    public int getMaxComplexity() {
        return maxComplexity;
    }

    public static FocusPackage getPackage(ItemStack focusStack) {
        if (focusStack == null || focusStack.isEmpty() || !focusStack.has(DataComponents.CUSTOM_DATA)) {
            return null;
        }
        CompoundTag tag = focusStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.contains("package")) {
            CompoundTag packTag = tag.getCompound("package").orElse(new CompoundTag());
            FocusPackage fp = new FocusPackage();
            fp.deserialize(packTag);
            return fp;
        }
        return null;
    }

    public static void setPackage(ItemStack focusStack, FocusPackage fp) {
        if (focusStack == null || focusStack.isEmpty()) {
            return;
        }
        CompoundTag tag = focusStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (fp == null) {
            tag.remove("package");
        } else {
            tag.put("package", fp.serialize());
        }
        if (tag.isEmpty()) {
            focusStack.remove(DataComponents.CUSTOM_DATA);
        } else {
            focusStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    public int getFocusColor(ItemStack focusStack) {
        if (focusStack == null || focusStack.isEmpty()) {
            return 0xFFFFFF;
        }
        CompoundTag tag = focusStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.contains("color")) {
            return tag.getInt("color").orElse(0xFFFFFF);
        }
        FocusPackage fp = getPackage(focusStack);
        if (fp != null) {
            FocusEffect[] effects = fp.getFocusEffects();
            List<Integer> colors = new ArrayList<>();
            for (FocusEffect fe : effects) {
                colors.add(FocusEngine.getElementColor(fe.getKey()));
            }
            int color = FocusLogic.blendEffectColors(colors);
            tag.putInt("color", color);
            focusStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            return color;
        }
        return 0xFFFFFF;
    }

    public float getVisCost(ItemStack focusStack) {
        FocusPackage fp = getPackage(focusStack);
        return (fp == null) ? 0.0f : FocusLogic.calculateVisCost(fp.getComplexity());
    }

    public int getActivationTime(ItemStack focusStack) {
        FocusPackage fp = getPackage(focusStack);
        return (fp == null) ? 5 : FocusLogic.calculateActivationTime(fp.getComplexity());
    }


    static {
        registerDefaultElements();
    }

    public static synchronized void registerDefaultElements() {
        register(FocusMediumTouch.class, "touch", 0xA0A0A0);
        register(FocusMediumBolt.class, "bolt", 0x3060E0);
        register(FocusMediumProjectile.class, "projectile", 0x40A0C0);
        register(FocusMediumPlanar.class, "planar", 0x808060);
        register(FocusMediumMine.class, "mine", 0xA04020);

        register(FocusEffectFire.class, "fire", 0xE05010);
        register(FocusEffectFrost.class, "frost", 0x10A0E0);
        register(FocusEffectEarth.class, "earth", 0x509030);
        register(FocusEffectAir.class, "air", 0xE0E080);
        register(FocusEffectCurse.class, "curse", 0x801080);
        register(FocusEffectFlux.class, "flux", 0x9020C0);
        register(FocusEffectHeal.class, "heal", 0xFFFFFF);
        register(FocusEffectBreak.class, "break", 0x404040);

        register(FocusModScatter.class, "scatter", 0x808080);
        register(FocusModSplitTarget.class, "split_target", 0x808080);
        register(FocusModSplitTrajectory.class, "split_trajectory", 0x808080);
    }

    private static void register(Class<?> clazz, String name, int color) {
        FocusEngine.registerElement(clazz, Identifier.fromNamespaceAndPath(Thaumcraft.MODID, "textures/foci/" + name + ".png"), color);
    }
}
