package thaumcraft.common.items.casters.foci;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.casters.FocusLogic;

public class FocusMediumProjectile extends FocusMedium {

    public FocusMediumProjectile() {
        super();
    }

    @Override
    public String getResearch() {
        return "FOCUSPROJECTILE";
    }

    @Override
    public String getKey() {
        return "thaumcraft.PROJECTILE";
    }

    @Override
    public int getComplexity() {
        return FocusLogic.calculateProjectileComplexity(getSettingValue("speed"), getSettingValue("option"));
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
        int[] options = { 0, 1, 2, 3 };
        String[] optionDesc = {
                "focus.common.none",
                "focus.projectile.bouncy",
                "focus.projectile.seeking.hostile",
                "focus.projectile.seeking.friendly"
        };
        return new NodeSetting[] {
                new NodeSetting("option", "focus.common.options", new NodeSetting.NodeSettingIntList(options, optionDesc), "FOCUSPROJECTILE"),
                new NodeSetting("speed", "focus.projectile.speed", new NodeSetting.NodeSettingIntRange(1, 5))
        };
    }

    @Override
    public Aspect getAspect() {
        return Aspect.MOTION;
    }

    @Override
    public boolean execute(Trajectory trajectory) {
        float speed = FocusLogic.calculateProjectileSpeed(getSettingValue("speed"));
        FocusPackage remaining = getRemainingPackage();
        if (remaining != null && getPackage() != null && getPackage().world != null && trajectory != null) {
            // Direct hitscan fallback when entity runtime is headless/test
            double range = 32.0 * speed;
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
            BlockHitResult blockHit = getPackage().world.clip(context);

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

            if (hit != null) {
                thaumcraft.api.casters.FocusEngine.runFocusPackage(
                        remaining,
                        new Trajectory[] { new Trajectory(hit.getLocation(), dir) },
                        new HitResult[] { hit }
                );
            }
        }
        return true;
    }
}
