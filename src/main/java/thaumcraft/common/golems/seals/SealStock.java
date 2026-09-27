package thaumcraft.common.golems.seals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SealStock extends SealFiltered implements ISealConfigToggles {

    private int delay;
    protected SealToggle[] props;
    private final Identifier icon = Identifier.fromNamespaceAndPath("thaumcraft", "items/seals/seal_stock");

    public SealStock() {
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
        return "thaumcraft:stock";
    }

    @Override
    public void tickSeal(Level world, ISealEntity seal) {
        if (world == null || seal == null || seal.isStoppedByRedstone(world)) return;

        if (delay++ % 20 != 0) return;

        IItemHandler inventory = ThaumcraftInvHelper.getItemHandlerAt(world, seal.getSealPos().pos, seal.getSealPos().face);

        List<SealStockLogic.ItemStack> inventoryList = new ArrayList<>();
        if (inventory != null) {
            for (int i = 0; i < inventory.getSlots(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    String itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
                    inventoryList.add(new SealStockLogic.ItemStack(itemId, stack.getCount()));
                }
            }
        }

        List<SealStockLogic.ItemQuota> quotas = new ArrayList<>();
        for (int i = 0; i < filter.size(); i++) {
            ItemStack stack = filter.get(i);
            int size = filterSize.get(i);
            if (stack != null && !stack.isEmpty() && size > 0) {
                String itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
                quotas.add(new SealStockLogic.ItemQuota(itemId, size));
            }
        }

        SealStockLogic.Pos pos = new SealStockLogic.Pos(seal.getSealPos().pos.getX(), seal.getSealPos().pos.getY(), seal.getSealPos().pos.getZ());
        List<SealStockLogic.FetchTask> tasks = SealStockLogic.evaluateQuotasAndDispatchTasks(pos, inventoryList, quotas);

        for (SealStockLogic.FetchTask task : tasks) {
            for (int i = 0; i < filter.size(); i++) {
                ItemStack filterStack = filter.get(i);
                if (filterStack != null && !filterStack.isEmpty()) {
                    String filterItemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(filterStack.getItem()).toString();
                    if (filterItemId.equals(task.itemId())) {
                        ItemStack requestStack = filterStack.copy();
                        requestStack.setCount(task.fetchAmount());
                        GolemHelper.requestProvisioning(world, seal.getSealPos().pos, seal.getSealPos().face, requestStack);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onTaskStarted(Level world, IGolemAPI golem, Task task) {
    }

    @Override
    public boolean onTaskCompletion(Level world, IGolemAPI golem, Task task) {
        return false;
    }

    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        return false;
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
