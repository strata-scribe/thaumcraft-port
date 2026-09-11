package thaumcraft.common.golems.seals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.tasks.TaskHandler;

import java.util.List;
import java.util.Random;

/**
 * Guard Seal.
 * Scans for hostile, animal, or player targets in its designated area and generates combat tasks.
 * Requires the FIGHTER trait.
 */
public class SealGuard implements ISeal, ISealGui, ISealConfigArea, ISealConfigToggles {

    private int delay;
    protected SealToggle[] props;
    private final Identifier icon = Identifier.fromNamespaceAndPath("thaumcraft", "items/seals/seal_guard");

    public SealGuard() {
        this.delay = new Random().nextInt(20);
        this.props = new SealToggle[]{
                new SealToggle(true, "pmob", "golem.prop.mob"),
                new SealToggle(false, "panimal", "golem.prop.animal"),
                new SealToggle(false, "pplayer", "golem.prop.player")
        };
    }

    @Override
    public String getKey() {
        return "thaumcraft:guard";
    }

    @Override
    public boolean canPlaceAt(Level world, BlockPos pos, Direction side) {
        return world != null && !world.isEmptyBlock(pos);
    }

    @Override
    public void tickSeal(Level world, ISealEntity seal) {
        if (world == null || seal == null || seal.isStoppedByRedstone(world)) return;

        if (delay++ % 20 != 0) return;

        AABB area = GolemHelper.getBoundsForArea(seal);
        List<LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, area);

        for (LivingEntity target : targets) {
            if (target != null && target.isAlive() && isValidTarget(target)) {
                Task task = new Task(seal.getSealPos(), target);
                task.setPriority(seal.getPriority());
                task.setLifespan((short) 10); // Combat tasks have short 10-tick lifespan to avoid stale chasing
                TaskHandler.addTask(world.dimension(), task);
            }
        }
    }

    public boolean isValidTarget(LivingEntity target) {
        if (target == null || !target.isAlive()) return false;

        if (props[0].getValue() && target instanceof Monster) {
            return true;
        }
        if (props[1].getValue() && target instanceof Animal) {
            return true;
        }
        if (props[2].getValue() && target instanceof Player) {
            return true;
        }
        return false;
    }

    @Override
    public void onTaskStarted(Level world, IGolemAPI golem, Task task) {
        if (task != null && task.getEntity() instanceof LivingEntity target && isValidTarget(target)) {
            if (golem.getGolemEntity() instanceof Mob mob) {
                mob.setTarget(target);
            }
            golem.addRankXp(1);
        }
        if (task != null) {
            task.setSuspended(true);
        }
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
        if (golem == null || task == null || task.getEntity() == null) return false;
        return !golem.getGolemEntity().isAlliedTo(task.getEntity());
    }

    @Override
    public void readCustomNBT(CompoundTag nbt) {
        if (nbt != null) {
            for (int i = 0; i < props.length; i++) {
                if (nbt.contains("prop_" + props[i].getKey())) {
                    props[i].setValue(nbt.getBoolean("prop_" + props[i].getKey()).orElse(props[i].getValue()));
                }
            }
        }
    }

    @Override
    public void writeCustomNBT(CompoundTag nbt) {
        if (nbt != null) {
            for (SealToggle prop : props) {
                nbt.putBoolean("prop_" + prop.getKey(), prop.getValue());
            }
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
    public int[] getGuiCategories() {
        return new int[]{2, 0, 4};
    }

    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[]{EnumGolemTrait.FIGHTER.get()};
    }

    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return null;
    }

    @Override
    public SealToggle[] getToggles() {
        return props;
    }

    @Override
    public void setToggle(int indx, boolean value) {
        if (indx >= 0 && indx < props.length) {
            props[indx].setValue(value);
        }
    }
}
