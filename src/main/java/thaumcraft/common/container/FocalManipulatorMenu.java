package thaumcraft.common.container;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.SimpleContainer;
import thaumcraft.api.blocks.ThaumcraftBlocks;
import thaumcraft.common.tiles.devices.TileFocalManipulator;

public class FocalManipulatorMenu extends AbstractContainerMenu {

    private final TileFocalManipulator manipulator;
    private final ContainerLevelAccess access;

    public FocalManipulatorMenu(int containerId, Inventory playerInventory, TileFocalManipulator manipulator, ContainerLevelAccess access) {
        super(ThaumcraftMenus.FOCAL_MANIPULATOR.get(), containerId);
        this.manipulator = manipulator;
        this.access = access;

        // Focus slot (index 0)
        this.addSlot(new Slot(manipulator, 0, 80, 15) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                // Should check if it's a focus
                return true;
            }
        });

        // Crystal slots (index 1 to 6)
        for (int i = 0; i < 6; i++) {
            this.addSlot(new Slot(manipulator, i + 1, 35 + i * 18, 50));
        }

        // Player Inventory (index 7 to 33)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Player Hotbar (index 34 to 42)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    public static FocalManipulatorMenu createClientSide(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        BlockPos pos = BlockPos.ZERO;
        if (buf != null && buf.isReadable()) {
            pos = buf.readBlockPos();
        } else if (playerInventory.player != null) {
            pos = playerInventory.player.blockPosition();
        }

        TileFocalManipulator be = null;
        if (playerInventory.player != null && playerInventory.player.level().getBlockEntity(pos) instanceof TileFocalManipulator found) {
            be = found;
        }
        if (be == null) {
            be = new TileFocalManipulator(pos, ThaumcraftBlocks.focalManipulator.get().defaultBlockState());
        }

        return new FocalManipulatorMenu(containerId, playerInventory, be,
                playerInventory.player != null ? ContainerLevelAccess.create(playerInventory.player.level(), pos) : ContainerLevelAccess.NULL);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < 7) { // Container slots
                if (!this.moveItemStackTo(itemstack1, 7, 43, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 7, false)) { // Player slots to container
                if (index < 34) { // Player inventory to hotbar
                    if (!this.moveItemStackTo(itemstack1, 34, 43, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 7, 34, false)) { // Hotbar to player inventory
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(this.access, playerIn, ThaumcraftBlocks.focalManipulator.get());
    }

    public void syncFocusNodeConfigs() {
        // Stub implementation
    }
}
