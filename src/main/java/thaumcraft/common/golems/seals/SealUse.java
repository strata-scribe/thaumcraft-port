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
import thaumcraft.common.golems.tasks.TaskHandler;

import java.util.Random;

/**
 * Use Seal.
 * Simulates player block clicks (left/right click, sneak, with held items or empty hand).
 * Requires the DEFT and SMART traits.
 */
public class SealUse extends SealFiltered implements ISealConfigToggles {

    private int delay;
    private int watchedTask = Integer.MIN_VALUE;
    protected SealToggle[] props;
    private final Identifier icon = Identifier.fromNamespaceAndPath("thaumcraft", "items/seals/seal_use");

    public SealUse() {
        super();
        this.delay = new Random().nextInt(20);
        this.props = new SealToggle[]{
                new SealToggle(true, "pmeta", "golem.prop.meta"),
                new SealToggle(true, "pnbt", "golem.prop.nbt"),
                new SealToggle(false, "pore", "golem.prop.ore"),
                new SealToggle(false, "pmod", "golem.prop.mod"),
                new SealToggle(false, "pleft", "golem.prop.left"),
                new SealToggle(false, "pempty", "golem.prop.empty"),
                new SealToggle(false, "pemptyhand", "golem.prop.emptyhand"),
                new SealToggle(false, "psneak", "golem.prop.sneak"),
                new SealToggle(false, "ppro", "golem.prop.provision.wl")
        };
    }

    @Override
    public String getKey() {
        return "thaumcraft:use";
    }

    @Override
    public boolean canPlaceAt(Level world, BlockPos pos, Direction side) {
        return true;
    }

    @Override
    public void tickSeal(Level world, ISealEntity seal) {
        if (world == null || seal == null || seal.isStoppedByRedstone(world)) return;

        if (delay++ % 5 != 0) return;

        Task oldTask = TaskHandler.getTask(world.dimension(), watchedTask);
        if (oldTask == null || oldTask.isSuspended() || oldTask.isCompleted()) {
            if (props[5].getValue() != world.isEmptyBlock(seal.getSealPos().pos)) {
                return;
            }
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
        if (world != null && golem != null && task != null) {
            if (props[5].getValue() == world.isEmptyBlock(task.getPos())) {
                golem.swingArm();
                golem.addRankXp(1);
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

        // If empty hand is allowed
        if (props[6].getValue()) {
            return true;
        }

        NonNullList<ItemStack> carrying = golem.getCarrying();
        for (ItemStack carried : carrying) {
            if (carried != null && !carried.isEmpty() && matchesFilter(carried)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int[] getGuiCategories() {
        return new int[]{1, 3, 0, 4};
    }

    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[]{EnumGolemTrait.DEFT.get(), EnumGolemTrait.SMART.get()};
    }

    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return null;
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
