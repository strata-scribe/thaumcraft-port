package thaumcraft.common.items.casters.foci;

import net.minecraft.world.phys.HitResult;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.casters.FocusRiftLogic;
import thaumcraft.common.entities.ThaumcraftEntities;
import thaumcraft.common.entities.projectile.EntityFocusRift;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.entity.projectile.ProjectileUtil;

public class FocusMediumRift extends FocusMedium {

    public FocusMediumRift() {
        super();
    }

    @Override
    public String getResearch() {
        return "FOCUSRIFT";
    }

    @Override
    public String getKey() {
        return "thaumcraft.RIFT";
    }

    @Override
    public int getComplexity() {
        return FocusRiftLogic.calculateRiftComplexity(getSettingValue("duration"));
    }

    @Override
    public Aspect getAspect() {
        return Aspect.ELDRITCH; // or Aspect.VOID if ELDRITCH is not appropriate
    }

    @Override
    public EnumSupplyType[] willSupply() {
        return new EnumSupplyType[] { EnumSupplyType.TARGET, EnumSupplyType.TRAJECTORY };
    }

    @Override
    public boolean hasIntermediary() {
        return true;
    }

    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] {
                new NodeSetting("duration", "focus.rift.duration", new NodeSetting.NodeSettingIntRange(1, 5))
        };
    }

    @Override
    public boolean execute(Trajectory trajectory) {
        FocusPackage remaining = getRemainingPackage();
        if (remaining != null && trajectory != null && getPackage() != null && getPackage().world != null) {
            Level level = getPackage().world;

            // Do a raycast to find where to place the rift, similar to projectile but instantly
            double range = 32.0;
            Vec3 dir = trajectory.direction != null ? trajectory.direction.normalize() : new Vec3(0, 0, 1);
            Vec3 start = trajectory.source != null ? trajectory.source : Vec3.ZERO;
            Vec3 end = start.add(dir.scale(range));

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
            BlockHitResult blockHit = level.clip(context);

            HitResult hit = null;
            if (entityHit != null && blockHit != null && blockHit.getType() != HitResult.Type.MISS) {
                double entityDist = start.distanceToSqr(entityHit.getLocation());
                double blockDist = start.distanceToSqr(blockHit.getLocation());
                hit = (entityDist <= blockDist) ? entityHit : blockHit;
            } else if (entityHit != null) {
                hit = entityHit;
            } else if (blockHit != null && blockHit.getType() != HitResult.Type.MISS) {
                hit = blockHit;
            }

            Vec3 spawnPos = (hit != null && hit.getType() != HitResult.Type.MISS) ? hit.getLocation() : end;

            if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                EntityFocusRift rift = ThaumcraftEntities.FOCUS_RIFT.get().create(serverLevel, EntitySpawnReason.EVENT);
                if (rift != null) {
                    rift.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
                    rift.setDurationSetting(getSettingValue("duration"));
                    rift.setFocusPackage(remaining);
                    serverLevel.addFreshEntity(rift);
                }
            } else if (level.isClientSide() && hit != null) {
                // If we are on a headless/test setup or similar, we might need a fallback execution
                // but spawning the entity is generally handled server-side.
            }
        }
        return true;
    }
}
