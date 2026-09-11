package thaumcraft.common.items.casters.foci;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.api.items.IArchitect;
import thaumcraft.common.casters.FocusLogic;

public class FocusMediumPlanar extends FocusMedium implements IArchitect {

    private final Set<BlockPos> checked = new HashSet<>();

    public FocusMediumPlanar() {
        super();
    }

    @Override
    public String getResearch() {
        return "FOCUSPLAN";
    }

    @Override
    public String getKey() {
        return "thaumcraft.PLAN";
    }

    @Override
    public int getComplexity() {
        return FocusLogic.calculatePlanarComplexity();
    }

    @Override
    public Aspect getAspect() {
        return Aspect.CRAFT;
    }

    @Override
    public boolean isExclusive() {
        return true;
    }

    @Override
    public NodeSetting[] createSettings() {
        int[] method = { 0, 1 };
        String[] methodDesc = { "focus.plan.full", "focus.plan.surface" };
        return new NodeSetting[] {
                new NodeSetting("method", "focus.plan.method", new NodeSetting.NodeSettingIntList(method, methodDesc))
        };
    }

    @Override
    public HitResult[] supplyTargets() {
        if (getParent() == null || getPackage() == null || getPackage().world == null) {
            return new HitResult[0];
        }
        List<HitResult> targets = new ArrayList<>();
        Trajectory[] parentTrajectories = getParent().supplyTrajectories();
        if (parentTrajectories != null) {
            for (Trajectory sT : parentTrajectories) {
                if (sT == null || sT.direction == null || sT.source == null) continue;
                Vec3 end = sT.source.add(sT.direction.normalize().scale(16.0));
                ClipContext ctx = new ClipContext(sT.source, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, getPackage().getCaster());
                BlockHitResult hit = getPackage().world.clip(ctx);
                if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
                    Player player = getPackage().getCaster() instanceof Player p ? p : null;
                    List<BlockPos> blocks = getArchitectBlocks(ItemStack.EMPTY, getPackage().world, hit.getBlockPos(), hit.getDirection(), player);
                    blocks.sort(Comparator.comparingDouble(b -> b.distSqr(hit.getBlockPos())));
                    for (BlockPos p : blocks) {
                        Vec3 center = new Vec3(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5);
                        targets.add(new BlockHitResult(center, hit.getDirection(), p, false));
                    }
                }
            }
        }
        return targets.toArray(new HitResult[0]);
    }

    @Override
    public HitResult getArchitectMOP(ItemStack stack, Level world, LivingEntity player) {
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getLookAngle().scale(16.0));
        return world.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
    }

    @Override
    public boolean useBlockHighlight(ItemStack stack) {
        return false;
    }

    @Override
    public boolean showAxis(ItemStack stack, Level world, Player player, Direction side, EnumAxis axis) {
        return true;
    }

    @Override
    public ArrayList<BlockPos> getArchitectBlocks(ItemStack stack, Level world, BlockPos pos, Direction side, Player player) {
        ArrayList<BlockPos> out = new ArrayList<>();
        if (world == null || pos == null) {
            return out;
        }
        checked.clear();
        int areaX = 1;
        int areaY = 1;
        int areaZ = 1;

        if (getSettingValue("method") == 0) {
            checkNeighboursFull(world, pos, pos, side, areaX, areaY, areaZ, out);
        } else {
            BlockState state = world.getBlockState(pos);
            checkNeighboursSurface(world, pos, state, pos, side, areaX, areaY, areaZ, out);
        }
        return out;
    }

    private void checkNeighboursFull(Level world, BlockPos origin, BlockPos current, Direction side,
                                     int sizeX, int sizeY, int sizeZ, List<BlockPos> list) {
        if (checked.contains(current) || checked.size() > 128) {
            return;
        }
        checked.add(current);
        if (!world.isEmptyBlock(current)) {
            list.add(current);
        }
        for (Direction dir : Direction.values()) {
            BlockPos next = current.relative(dir);
            if (Math.abs(next.getX() - origin.getX()) <= sizeX &&
                Math.abs(next.getY() - origin.getY()) <= sizeY &&
                Math.abs(next.getZ() - origin.getZ()) <= sizeZ) {
                checkNeighboursFull(world, origin, next, side, sizeX, sizeY, sizeZ, list);
            }
        }
    }

    private void checkNeighboursSurface(Level world, BlockPos origin, BlockState matchState, BlockPos current,
                                        Direction side, int sizeX, int sizeY, int sizeZ, List<BlockPos> list) {
        if (checked.contains(current) || checked.size() > 128) {
            return;
        }
        checked.add(current);
        if (world.getBlockState(current).equals(matchState) && !world.isEmptyBlock(current)) {
            list.add(current);
            for (Direction dir : Direction.values()) {
                if (dir != side && dir != side.getOpposite()) {
                    BlockPos next = current.relative(dir);
                    if (Math.abs(next.getX() - origin.getX()) <= sizeX &&
                        Math.abs(next.getY() - origin.getY()) <= sizeY &&
                        Math.abs(next.getZ() - origin.getZ()) <= sizeZ) {
                        checkNeighboursSurface(world, origin, matchState, next, side, sizeX, sizeY, sizeZ, list);
                    }
                }
            }
        }
    }
}
