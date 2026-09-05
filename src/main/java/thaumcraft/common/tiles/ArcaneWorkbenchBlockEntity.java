package thaumcraft.common.tiles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import thaumcraft.api.crafting.IArcaneWorkbench;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;
import thaumcraft.common.container.ArcaneWorkbenchMenu;

import javax.annotation.Nullable;

/**
 * Block entity for the Arcane Workbench.
 *
 * Slot layout (15 total):
 *   0-8  : 3×3 crafting grid
 *   9-14 : 6 primal-crystal slots (Aer, Terra, Ignis, Aqua, Ordo, Perditio)
 */
public class ArcaneWorkbenchBlockEntity extends BlockEntity implements MenuProvider, IArcaneWorkbench {

    /** Total inventory size. */
    public static final int SLOT_COUNT = 15;

    /** Crafting grid occupies slots 0-8 (9 slots). */
    public static final int CRAFT_SLOTS = 9;

    /** Crystal slots occupy slots 9-14 (6 slots). */
    public static final int CRYSTAL_SLOTS = 6;

    /** The backing inventory. Exposed as a public field so the menu can access it directly. */
    public final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);

    public ArcaneWorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.ARCANE_WORKBENCH.get(), pos, state);
    }

    // -------------------------------------------------------------------------
    // MenuProvider
    // -------------------------------------------------------------------------

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.thaumcraft.arcane_workbench");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ArcaneWorkbenchMenu(
                containerId,
                playerInventory,
                this,
                ContainerLevelAccess.create(this.level, this.worldPosition)
        );
    }

    // -------------------------------------------------------------------------
    // NBT serialisation — MC 26.1.2 uses ValueOutput / ValueInput
    // -------------------------------------------------------------------------

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        net.minecraft.world.ContainerHelper.saveAllItems(output, items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        net.minecraft.world.ContainerHelper.loadAllItems(input, items);
    }
}
