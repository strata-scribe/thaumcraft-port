package thaumcraft.common.golems.seals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealConfigFilter;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.api.golems.tasks.Task;

/**
 * Base abstract class for seals with item filtering support.
 */
public abstract class SealFiltered implements ISeal, ISealGui, ISealConfigFilter {

    protected NonNullList<ItemStack> filter;
    protected NonNullList<Integer> filterSize;
    protected boolean blacklist;

    public SealFiltered() {
        this.filter = NonNullList.withSize(getFilterSize(), ItemStack.EMPTY);
        this.filterSize = NonNullList.withSize(getFilterSize(), 0);
        this.blacklist = true;
    }

    @Override
    public void readCustomNBT(CompoundTag nbt) {
        if (nbt == null) return;
        this.filter = NonNullList.withSize(getFilterSize(), ItemStack.EMPTY);
        if (nbt.contains("Filter")) {
            nbt.getList("Filter").ifPresent(filterList -> {
                for (int i = 0; i < filterList.size(); i++) {
                    filterList.getCompound(i).ifPresent(itemTag -> {
                        int slot = itemTag.getByte("Slot").orElse((byte) 0) & 0xFF;
                        if (slot >= 0 && slot < filter.size() && itemTag.contains("Item")) {
                            itemTag.getCompound("Item").ifPresent(tag -> {
                                ItemStack.OPTIONAL_CODEC.parse(NbtOps.INSTANCE, tag).result().ifPresent(s -> {
                                    filter.set(slot, s);
                                });
                            });
                        }
                    });
                }
            });
        }
        for (ItemStack s : this.filter) {
            if (s.getCount() > 1) {
                s.setCount(1);
            }
        }
        this.blacklist = nbt.getBoolean("bl").orElse(true);
        this.filterSize = NonNullList.withSize(getFilterSize(), 0);
        if (nbt.contains("Sizes")) {
            nbt.getList("Sizes").ifPresent(nbttaglist -> {
                for (int i = 0; i < nbttaglist.size(); ++i) {
                    nbttaglist.getCompound(i).ifPresent(nbttagcompound -> {
                        int j = nbttagcompound.getByte("Slot").orElse((byte) 0) & 0xFF;
                        if (j >= 0 && j < filterSize.size()) {
                            filterSize.set(j, nbttagcompound.getInt("Size").orElse(0));
                        }
                    });
                }
            });
        }
    }

    @Override
    public void writeCustomNBT(CompoundTag nbt) {
        if (nbt == null) return;
        ListTag filterList = new ListTag();
        for (int i = 0; i < filter.size(); i++) {
            ItemStack s = filter.get(i);
            if (s != null && !s.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putByte("Slot", (byte) i);
                ItemStack.OPTIONAL_CODEC.encodeStart(NbtOps.INSTANCE, s).result().ifPresent(res -> {
                    itemTag.put("Item", res);
                });
                filterList.add(itemTag);
            }
        }
        nbt.put("Filter", filterList);
        nbt.putBoolean("bl", blacklist);

        ListTag nbttaglist = new ListTag();
        for (int i = 0; i < filterSize.size(); ++i) {
            int size = filterSize.get(i);
            if (size != 0) {
                CompoundTag nbttagcompound = new CompoundTag();
                nbttagcompound.putByte("Slot", (byte) i);
                nbttagcompound.putInt("Size", size);
                nbttaglist.add(nbttagcompound);
            }
        }
        nbt.put("Sizes", nbttaglist);
    }

    @Override
    public boolean canPlaceAt(Level world, BlockPos pos, Direction side) {
        return world != null && !world.isEmptyBlock(pos);
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
        return new int[]{0};
    }

    @Override
    public int getFilterSize() {
        return 1;
    }

    @Override
    public NonNullList<ItemStack> getInv() {
        return filter;
    }

    @Override
    public NonNullList<Integer> getSizes() {
        return filterSize;
    }

    @Override
    public ItemStack getFilterSlot(int i) {
        return (i >= 0 && i < filter.size()) ? filter.get(i) : ItemStack.EMPTY;
    }

    @Override
    public int getFilterSlotSize(int i) {
        return (i >= 0 && i < filterSize.size()) ? filterSize.get(i) : 0;
    }

    @Override
    public void setFilterSlot(int i, ItemStack stack) {
        if (i >= 0 && i < filter.size()) {
            filter.set(i, stack != null ? stack.copy() : ItemStack.EMPTY);
        }
    }

    @Override
    public void setFilterSlotSize(int i, int size) {
        if (i >= 0 && i < filterSize.size()) {
            filterSize.set(i, size);
        }
    }

    @Override
    public boolean isBlacklist() {
        return blacklist;
    }

    @Override
    public void setBlacklist(boolean black) {
        this.blacklist = black;
    }

    @Override
    public boolean hasStacksizeLimiters() {
        return false;
    }

    @Override
    public void onRemoval(Level world, BlockPos pos, Direction side) {}

    @Override
    public void onTaskSuspension(Level world, Task task) {}
}
