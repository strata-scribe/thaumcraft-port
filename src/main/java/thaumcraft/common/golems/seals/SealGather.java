package thaumcraft.common.golems.seals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.tasks.TaskHandler;

import java.util.List;
import java.util.Random;

/**
 * Gather Seal.
 * Scans for dropped ItemEntity items on the ground within its designated area,
 * applies whitelist/blacklist filtering, and dispatches collection tasks to golems.
 * Forbids the CLUMSY trait.
 */
public class SealGather extends SealFiltered implements ISealConfigArea, ISealConfigToggles {

    private int delay;
    protected SealToggle[] props;
    private final Identifier icon = Identifier.fromNamespaceAndPath("thaumcraft", "items/seals/seal_gather");

    public SealGather() {
        super();
        this.delay = new Random().nextInt(5);
        this.props = new SealToggle[]{
                new SealToggle(true, "pmeta", "golem.prop.meta"),
                new SealToggle(true, "pnbt", "golem.prop.nbt"),
                new SealToggle(false, "pore", "golem.prop.ore"),
                new SealToggle(false, "pmod", "golem.prop.mod")
        };
    }

    @Override
    public String getKey() {
        return "thaumcraft:gather";
    }

    @Override
    public void tickSeal(Level world, ISealEntity seal) {
        if (world == null || seal == null || seal.isStoppedByRedstone(world)) return;

        if (delay++ % 5 != 0) return;

        AABB area = GolemHelper.getBoundsForArea(seal);
        List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, area);

        for (ItemEntity ent : items) {
            if (ent != null && ent.isAlive() && !ent.hasPickUpDelay() && !ent.getItem().isEmpty()) {
                if (matchesFilter(ent.getItem())) {
                    Task task = new Task(seal.getSealPos(), ent);
                    task.setPriority(seal.getPriority());
                    task.setLifespan((short) 300);
                    TaskHandler.addTask(world.dimension(), task);
                    break;
                }
            }
        }
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
            // Empty blacklist matches all; empty whitelist matches nothing
            return isBlacklist();
        }

        return isBlacklist() ? !matched : matched;
    }

    @Override
    public boolean onTaskCompletion(Level world, IGolemAPI golem, Task task) {
        if (task != null && task.getEntity() instanceof ItemEntity itemEntity && itemEntity.isAlive()) {
            ItemStack stack = itemEntity.getItem();
            if (matchesFilter(stack)) {
                ItemStack remaining = golem.holdItem(stack);
                if (remaining == null || remaining.isEmpty() || remaining.getCount() <= 0) {
                    itemEntity.discard();
                } else {
                    itemEntity.setItem(remaining);
                }
                golem.swingArm();
            }
        }
        if (task != null) {
            task.setSuspended(true);
        }
        return true;
    }

    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        if (golem == null || task == null || !(task.getEntity() instanceof ItemEntity itemEntity)) {
            return false;
        }
        if (!itemEntity.isAlive() || itemEntity.getItem().isEmpty()) {
            task.setSuspended(true);
            return false;
        }
        return golem.canCarry(itemEntity.getItem(), true);
    }

    @Override
    public void onTaskStarted(Level world, IGolemAPI golem, Task task) {
    }

    @Override
    public int[] getGuiCategories() {
        return new int[]{2, 1, 0, 4};
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
