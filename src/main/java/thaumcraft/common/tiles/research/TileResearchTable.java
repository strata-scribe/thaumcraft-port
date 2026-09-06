package thaumcraft.common.tiles.research;

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
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;
import thaumcraft.common.container.ResearchTableMenu;
import javax.annotation.Nullable;

public class TileResearchTable extends BlockEntity implements MenuProvider {

    public final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    public TileResearchTable(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.RESEARCH_TABLE.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.thaumcraft.research_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ResearchTableMenu(
                containerId,
                playerInventory,
                this,
                ContainerLevelAccess.create(this.level, this.worldPosition)
        );
    }

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
