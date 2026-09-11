package thaumcraft.common.golems.seals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.GolemSealLogic;
import thaumcraft.common.golems.tasks.TaskHandler;

import java.util.Random;

/**
 * Deposit Seal.
 * Dispatches tasks for golems to deposit carried items into target containers or inventories,
 * respecting whitelist/blacklist filters and stack-size limits.
 * Forbids the CLUMSY trait.
 */
public class SealDeposit extends SealFiltered implements ISealConfigToggles {

    private int delay;
    private int watchedTask = Integer.MIN_VALUE;
    protected SealToggle[] props;
    private final Identifier icon = Identifier.fromNamespaceAndPath("thaumcraft", "items/seals/seal_deposit");

    public SealDeposit() {
        super();
        this.delay = new Random().nextInt(20);
        this.props = new SealToggle[]{
                new SealToggle(true, "pmeta", "golem.prop.meta"),
                new SealToggle(true, "pnbt", "golem.prop.nbt"),
                new SealToggle(false, "pore", "golem.prop.ore"),
                new SealToggle(false, "pmod", "golem.prop.mod"),
                new SealToggle(false, "pexist", "golem.prop.exist")
        };
    }

    @Override
    public String getKey() {
        return "thaumcraft:deposit";
    }

    @Override
    public void tickSeal(Level world, ISealEntity seal) {
        if (world == null || seal == null || seal.isStoppedByRedstone(world)) return;

        if (delay++ % 20 != 0) return;

        Task oldTask = TaskHandler.getTask(world.dimension(), watchedTask);
        if (oldTask == null || oldTask.isReserved() || oldTask.isSuspended() || oldTask.isCompleted()) {
            Task task = new Task(seal.getSealPos(), seal.getSealPos().pos);
            task.setPriority(seal.getPriority());
            TaskHandler.addTask(world.dimension(), task);
            watchedTask = task.getId();
        }
    }

    @Override
    public void onTaskStarted(Level world, IGolemAPI golem, Task task) {
    }

    @Override
    public boolean onTaskCompletion(Level world, IGolemAPI golem, Task task) {
        if (golem != null && task != null) {
            NonNullList<ItemStack> carrying = golem.getCarrying();
            for (ItemStack carried : carrying) {
                if (carried != null && !carried.isEmpty() && matchesFilter(carried)) {
                    int limit = carried.getCount();
                    if (hasStacksizeLimiters() && !filterSize.isEmpty() && filterSize.get(0) > 0) {
                        limit = GolemSealLogic.calculateDepositAmount(0, filterSize.get(0), carried.getCount());
                    }
                    if (limit > 0) {
                        ItemStack toDrop = carried.copy();
                        toDrop.setCount(limit);
                        golem.dropItem(toDrop);
                        golem.addRankXp(1);
                        golem.swingArm();
                        break;
                    }
                }
            }
            task.setSuspended(true);
        }
        return true;
    }

    public boolean matchesFilter(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return isBlacklist();

        boolean filterHasItems = false;
        boolean matched = false;

        for (ItemStack filterStack : filter) {
            if (filterStack != null && !filterStack.isEmpty()) {
                filterHasItems = true;
                if (ItemStack.isSameItem(filterStack, stack)) {
                    if (!props[1].getValue() || ItemStack.isSameItemSameComponents(filterStack, stack)) {
                        matched = true;
                        break;
                    }
                }
            }
        }

        if (!filterHasItems) {
            return isBlacklist();
        }

        return isBlacklist() ? !matched : matched;
    }

    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        if (golem == null) return false;

        NonNullList<ItemStack> carrying = golem.getCarrying();
        for (ItemStack carried : carrying) {
            if (carried != null && !carried.isEmpty() && matchesFilter(carried)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasStacksizeLimiters() {
        return !isBlacklist();
    }

    @Override
    public int[] getGuiCategories() {
        return new int[]{1, 0, 4};
    }

    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return null;
    }

    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return new EnumGolemTrait[]{EnumGolemTrait.CLUMSY.get()};
    }

    @Override
    public Identifier getSealIcon() {
        return icon;
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

    @Override
    public void readCustomNBT(CompoundTag nbt) {
        super.readCustomNBT(nbt);
        if (nbt != null) {
            for (SealToggle prop : props) {
                if (nbt.contains("prop_" + prop.getKey())) {
                    prop.setValue(nbt.getBoolean("prop_" + prop.getKey()).orElse(prop.getValue()));
                }
            }
        }
    }

    @Override
    public void writeCustomNBT(CompoundTag nbt) {
        super.writeCustomNBT(nbt);
        if (nbt != null) {
            for (SealToggle prop : props) {
                nbt.putBoolean("prop_" + prop.getKey(), prop.getValue());
            }
        }
    }
}
