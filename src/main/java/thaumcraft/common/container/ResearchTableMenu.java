package thaumcraft.common.container;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.tiles.research.TileResearchTable;
import thaumcraft.api.blocks.ThaumcraftBlocks;

public class ResearchTableMenu extends AbstractContainerMenu {

    private final ContainerLevelAccess access;
    private final TileResearchTable tile;
    private final Container container;

    public ResearchTableMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, null, ContainerLevelAccess.NULL);
    }

    public ResearchTableMenu(int containerId, Inventory playerInventory, TileResearchTable tile, ContainerLevelAccess access) {
        super(ThaumcraftMenus.RESEARCH_TABLE.get(), containerId);
        this.access = access;
        this.tile = tile;

        this.container = new SimpleContainer(2);
        if (tile != null) {
            for (int i = 0; i < 2; i++) {
                this.container.setItem(i, tile.items.get(i));
            }
        }

        // Slot 0: Scribing Tools
        this.addSlot(new Slot(this.container, 0, 16, 16));
        // Slot 1: Paper
        this.addSlot(new Slot(this.container, 1, 16, 34));

        // Player Inventory
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        // Player Hotbar
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ThaumcraftBlocks.researchTable.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (this.tile != null) {
            for (int i = 0; i < 2; i++) {
                this.tile.items.set(i, this.container.getItem(i));
            }
            this.tile.setChanged();
        }
    }

    public static ResearchTableMenu createClientSide(int windowId, Inventory playerInventory, FriendlyByteBuf buf) {
        return new ResearchTableMenu(windowId, playerInventory, buf);
    }
}
