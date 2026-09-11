package thaumcraft.common.golems.seals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.GolemSealLogic;
import thaumcraft.common.golems.tasks.TaskHandler;

import java.util.Random;

/**
 * Wander / Patrol Seal.
 * Generates patrol route waypoint tasks within its configured area bounding box
 * to maintain perimeter security and anchor idle golems.
 */
public class SealWander implements ISeal, ISealConfigArea {

    private int delay;
    private int waypointCounter;
    private int watchedTask = Integer.MIN_VALUE;
    private final Identifier icon = Identifier.fromNamespaceAndPath("thaumcraft", "items/seals/seal_wander");

    public SealWander() {
        this.delay = new Random().nextInt(20);
        this.waypointCounter = 0;
    }

    @Override
    public String getKey() {
        return "thaumcraft:wander";
    }

    @Override
    public boolean canPlaceAt(Level world, BlockPos pos, Direction side) {
        return world != null && !world.isEmptyBlock(pos);
    }

    @Override
    public void tickSeal(Level world, ISealEntity seal) {
        if (world == null || seal == null || seal.isStoppedByRedstone(world)) return;

        if (delay++ % 20 != 0) return;

        Task oldTask = TaskHandler.getTask(world.dimension(), watchedTask);
        if (oldTask == null || oldTask.isReserved() || oldTask.isSuspended() || oldTask.isCompleted()) {
            BlockPos targetPos = GolemHelper.getPosInArea(seal, waypointCounter);
            if (targetPos != null) {
                Task task = new Task(seal.getSealPos(), targetPos);
                task.setPriority(seal.getPriority());
                task.setLifespan((short) 100);
                TaskHandler.addTask(world.dimension(), task);
                watchedTask = task.getId();
                waypointCounter = GolemSealLogic.calculatePatrolWaypointIndex(waypointCounter, 16);
            }
        }
    }

    @Override
    public void onTaskStarted(Level world, IGolemAPI golem, Task task) {
    }

    @Override
    public boolean onTaskCompletion(Level world, IGolemAPI golem, Task task) {
        if (task != null) {
            task.setSuspended(true);
        }
        return true;
    }

    @Override
    public void onTaskSuspension(Level world, Task task) {
    }

    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        return true;
    }

    @Override
    public void readCustomNBT(CompoundTag nbt) {
        if (nbt != null) {
            waypointCounter = nbt.getInt("waypointCounter").orElse(0);
        }
    }

    @Override
    public void writeCustomNBT(CompoundTag nbt) {
        if (nbt != null) {
            nbt.putInt("waypointCounter", waypointCounter);
        }
    }

    @Override
    public Identifier getSealIcon() {
        return icon;
    }

    @Override
    public void onRemoval(Level world, BlockPos pos, Direction side) {
    }

    @Override
    public Object returnContainer(Level world, Player player, BlockPos pos, Direction side, ISealEntity seal) {
        return null;
    }

    @Override
    public Object returnGui(Level world, Player player, BlockPos pos, Direction side, ISealEntity seal) {
        return null;
    }

    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return null;
    }

    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return null;
    }
}
