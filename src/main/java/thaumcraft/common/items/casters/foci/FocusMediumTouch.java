package thaumcraft.common.items.casters.foci;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.casters.FocusLogic;

public class FocusMediumTouch extends FocusMedium {

    public FocusMediumTouch() {
        super();
    }

    @Override
    public String getResearch() {
        return "BASEAUROMANCY";
    }

    @Override
    public String getKey() {
        return "thaumcraft.TOUCH";
    }

    @Override
    public int getComplexity() {
        return FocusLogic.calculateTouchComplexity();
    }

    @Override
    public EnumSupplyType[] willSupply() {
        return new EnumSupplyType[] { EnumSupplyType.TRAJECTORY, EnumSupplyType.TARGET };
    }

    @Override
    public Aspect getAspect() {
        return Aspect.AVERSION;
    }

    protected double getReachDistance() {
        if (this instanceof FocusMediumBolt) {
            return 16.0;
        }
        if (getPackage() != null && getPackage().getCaster() instanceof Player player) {
            return player.blockInteractionRange();
        }
        return 4.5;
    }

    @Override
    public Trajectory[] supplyTrajectories() {
        if (getParent() == null || getPackage() == null || getPackage().world == null) {
            return new Trajectory[0];
        }
        List<Trajectory> trajectories = new ArrayList<>();
        double range = getReachDistance();
        Trajectory[] parentTrajectories = getParent().supplyTrajectories();
        if (parentTrajectories != null) {
            for (Trajectory sT : parentTrajectories) {
                if (sT == null || sT.direction == null || sT.source == null) continue;
                Vec3 end = sT.direction.normalize();
                Vec3 reachEnd = sT.source.add(end.scale(range));

                HitResult hit = rayTrace(sT.source, reachEnd, range);
                Vec3 hitPos = (hit != null && hit.getType() != HitResult.Type.MISS) ? hit.getLocation() : reachEnd;
                trajectories.add(new Trajectory(hitPos, sT.direction.normalize()));
            }
        }
        return trajectories.toArray(new Trajectory[0]);
    }

    @Override
    public HitResult[] supplyTargets() {
        if (getParent() == null || getPackage() == null || getPackage().world == null) {
            return new HitResult[0];
        }
        List<HitResult> targets = new ArrayList<>();
        double range = getReachDistance();
        Trajectory[] parentTrajectories = getParent().supplyTrajectories();
        if (parentTrajectories != null) {
            for (Trajectory sT : parentTrajectories) {
                if (sT == null || sT.direction == null || sT.source == null) continue;
                Vec3 end = sT.direction.normalize();
                Vec3 reachEnd = sT.source.add(end.scale(range));

                HitResult hit = rayTrace(sT.source, reachEnd, range);
                if (hit != null && hit.getType() != HitResult.Type.MISS) {
                    targets.add(hit);
                }
            }
        }
        return targets.toArray(new HitResult[0]);
    }

    protected HitResult rayTrace(Vec3 start, Vec3 end, double range) {
        if (getPackage() == null || getPackage().world == null) {
            return null;
        }
        LivingEntity caster = getPackage().getCaster();
        AABB searchBox = new AABB(start, end).inflate(1.0);
        EntityHitResult entityHit = null;
        if (caster != null) {
            entityHit = ProjectileUtil.getEntityHitResult(
                    caster,
                    start,
                    end,
                    searchBox,
                    e -> !e.isSpectator() && e.isPickable() && !e.isPassengerOfSameVehicle(caster),
                    range * range
            );
        }

        ClipContext context = new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster);
        BlockHitResult blockHit = getPackage().world.clip(context);

        if (entityHit != null && blockHit != null && blockHit.getType() != HitResult.Type.MISS) {
            double entityDist = start.distanceToSqr(entityHit.getLocation());
            double blockDist = start.distanceToSqr(blockHit.getLocation());
            return (entityDist <= blockDist) ? entityHit : blockHit;
        } else if (entityHit != null) {
            return entityHit;
        } else {
            return blockHit;
        }
    }

    @Override
    public boolean execute(Trajectory trajectory) {
        return true;
    }
}
