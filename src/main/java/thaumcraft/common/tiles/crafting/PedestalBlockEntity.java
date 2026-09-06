package thaumcraft.common.tiles.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;

/**
 * Block entity for the Thaumcraft Pedestal — holds a single item for infusion crafting.
 *
 * <p>MC 26.1.2 / NeoForge 26.2 port of
 * {@code thaumcraft.common.tiles.crafting.TilePedestal}.</p>
 */
public class PedestalBlockEntity extends BlockEntity {

    private ItemStack item = ItemStack.EMPTY;

    public PedestalBlockEntity(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.PEDESTAL.get(), pos, state);
    }

    // -------------------------------------------------------------------------
    // Inventory helpers
    // -------------------------------------------------------------------------

    /** @return the stored item (never null — returns EMPTY). */
    public ItemStack getItem() {
        return item;
    }

    /** Replaces the stored item. */
    public void setItem(ItemStack stack) {
        this.item = stack == null ? ItemStack.EMPTY : stack;
        setChanged();
    }

    /** @return {@code true} if the pedestal contains a non-empty item. */
    public boolean hasItem() {
        return !item.isEmpty();
    }

    /**
     * Called by the infusion matrix to swap the central pedestal's item with the
     * crafting output, then mark + sync.
     */
    public void setItemFromInfusion(ItemStack stack) {
        setItem(stack);
    }

    // -------------------------------------------------------------------------
    // NBT — MC 26.1.2 ValueOutput / ValueInput
    // -------------------------------------------------------------------------

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("PedestalItem", ItemStack.OPTIONAL_CODEC, item);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        item = input.read("PedestalItem", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }
}
