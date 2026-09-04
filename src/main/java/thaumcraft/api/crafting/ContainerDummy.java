package thaumcraft.api.crafting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class ContainerDummy extends AbstractContainerMenu {

	public ContainerDummy() {
		super(null, 0);
	}

	@Override
	public boolean stillValid(Player player) {
		return false;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slotIndex) {
		return ItemStack.EMPTY;
	}

}
