package thaumcraft.common.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.parts.*;

public class TileGolemBuilder extends BlockEntity {

    private GolemHead head;
    private GolemMaterial material;
    private GolemArm arm;
    private GolemLeg leg;
    private GolemAddon addon;

    private int constructionTime = 0;
    private int maxConstructionTime = 100; // default example
    private boolean isCrafting = false;

    public TileGolemBuilder(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.GOLEM_BUILDER.get(), pos, state);
    }

    public void startCrafting() {
        this.isCrafting = true;
        this.constructionTime = 0;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (isCrafting) {
            constructionTime++;
            if (constructionTime >= maxConstructionTime) {
                isCrafting = false;
                constructionTime = 0;
                // Assembly complete logic would go here
            }
        }
    }

    public int getHealth() {
        int hp = 0;
        if (material != null) hp += material.healthMod;
        return hp;
    }

    public float getSpeed() {
        float speed = 0.3f; // base speed
        if (hasTrait(EnumGolemTrait.HEAVY)) {
            speed -= 0.1f;
        }
        if (hasTrait(EnumGolemTrait.LIGHT)) {
            speed += 0.1f;
        }
        return speed;
    }

    public int getArmor() {
        int armor = 0;
        if (material != null) {
            armor += material.armor;
        }
        if (hasTrait(EnumGolemTrait.ARMORED)) {
            armor += 4;
        }
        if (hasTrait(EnumGolemTrait.FRAGILE)) {
            armor -= 2;
        }
        return Math.max(0, armor);
    }

    public int getCarryCapacity() {
        int capacity = 1;
        if (hasTrait(EnumGolemTrait.HAULER)) {
            capacity += 1;
        }
        return capacity;
    }

    private boolean hasTrait(net.neoforged.neoforge.registries.DeferredHolder<EnumGolemTrait, EnumGolemTrait> traitHolder) {
        if (traitHolder == null) return false;
        EnumGolemTrait trait = traitHolder.get();
        if (trait == null) return false;

        if (head != null && head.traits != null) {
            for (EnumGolemTrait t : head.traits) if (t == trait) return true;
        }
        if (material != null && material.traits != null) {
            for (EnumGolemTrait t : material.traits) if (t == trait) return true;
        }
        if (arm != null && arm.traits != null) {
            for (EnumGolemTrait t : arm.traits) if (t == trait) return true;
        }
        if (leg != null && leg.traits != null) {
            for (EnumGolemTrait t : leg.traits) if (t == trait) return true;
        }
        if (addon != null && addon.traits != null) {
            for (EnumGolemTrait t : addon.traits) if (t == trait) return true;
        }
        return false;
    }

    public GolemHead getHead() { return head; }
    public void setHead(GolemHead head) { this.head = head; }

    public GolemMaterial getMaterial() { return material; }
    public void setMaterial(GolemMaterial material) { this.material = material; }

    public GolemArm getArm() { return arm; }
    public void setArm(GolemArm arm) { this.arm = arm; }

    public GolemLeg getLeg() { return leg; }
    public void setLeg(GolemLeg leg) { this.leg = leg; }

    public GolemAddon getAddon() { return addon; }
    public void setAddon(GolemAddon addon) { this.addon = addon; }

    public int getConstructionTime() { return constructionTime; }
    public int getMaxConstructionTime() { return maxConstructionTime; }
    public boolean isCrafting() { return isCrafting; }
}
