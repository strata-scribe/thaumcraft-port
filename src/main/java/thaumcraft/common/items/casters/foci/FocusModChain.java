package thaumcraft.common.items.casters.foci;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusMod;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.common.casters.FocusChainLogic;

public class FocusModChain extends FocusMod {

    public FocusModChain() {
        super();
    }

    @Override
    public String getResearch() {
        return "FOCUSCHAIN";
    }

    @Override
    public String getKey() {
        return "thaumcraft.CHAIN";
    }

    @Override
    public int getComplexity() {
        return FocusChainLogic.calculateChainComplexity(getSettingValue("length"));
    }

    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] {
                new NodeSetting("length", "focus.chain.length", new NodeSetting.NodeSettingIntRange(1, 10))
        };
    }

    @Override
    public EnumSupplyType[] mustBeSupplied() {
        return new EnumSupplyType[] { EnumSupplyType.TARGET };
    }

    @Override
    public EnumSupplyType[] willSupply() {
        return new EnumSupplyType[] { EnumSupplyType.TARGET };
    }

    @Override
    public HitResult[] supplyTargets() {
        return new HitResult[0]; // Empty array to stop main engine loop
    }

    private static class EntityChainTarget implements FocusChainLogic.ChainTarget {
        private final Entity entity;
        public EntityChainTarget(Entity entity) {
            this.entity = entity;
        }
        public Entity getEntity() { return entity; }
        @Override public int getId() { return entity.getId(); }
        @Override public double getX() { return entity.getX(); }
        @Override public double getY() { return entity.getY(); }
        @Override public double getZ() { return entity.getZ(); }
    }

    @Override
    public boolean execute() {
        FocusPackage remainingPackage = getRemainingPackage();
        if (remainingPackage == null || getParent() == null) {
            return false;
        }

        HitResult[] parentTargets = getParent().supplyTargets();
        if (parentTargets == null || getPackage() == null || getPackage().world == null) {
            return false;
        }

        int maxLength = getSettingValue("length");
        double radius = 8.0; // Assume 8 blocks for chain jumping radius

        for (HitResult target : parentTargets) {
            if (target instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity primaryEnt) {

                // Get available targets around primary
                AABB searchBox = primaryEnt.getBoundingBox().inflate(radius * maxLength);
                List<Entity> entities = getPackage().world.getEntities(
                        primaryEnt,
                        searchBox,
                        e -> e instanceof LivingEntity && !e.isSpectator()
                );

                List<FocusChainLogic.ChainTarget> available = new ArrayList<>();
                for (Entity e : entities) {
                    available.add(new EntityChainTarget(e));
                }

                EntityChainTarget primaryTarget = new EntityChainTarget(primaryEnt);
                List<FocusChainLogic.ChainTarget> secondaryTargets = FocusChainLogic.getSecondaryTargets(
                        available, primaryTarget, maxLength, radius
                );

                List<EntityChainTarget> allTargets = new ArrayList<>();
                allTargets.add(primaryTarget);
                for (FocusChainLogic.ChainTarget ct : secondaryTargets) {
                    allTargets.add((EntityChainTarget) ct);
                }

                int jumpIndex = 0;
                for (EntityChainTarget ct : allTargets) {
                    FocusPackage subPackage = remainingPackage.copy(remainingPackage.getCaster());
                    subPackage.multiplyPower(FocusChainLogic.calculateFalloff(1.0f, jumpIndex));

                    HitResult subTarget = new EntityHitResult(ct.getEntity());
                    FocusEngine.runFocusPackage(subPackage, null, new HitResult[]{subTarget});
                    jumpIndex++;
                }
            } else {
                // Not a LivingEntity (e.g. block or non-living entity), just execute on the primary target and stop chain.
                FocusPackage subPackage = remainingPackage.copy(remainingPackage.getCaster());
                FocusEngine.runFocusPackage(subPackage, null, new HitResult[]{target});
            }
        }

        return false;
    }
}
