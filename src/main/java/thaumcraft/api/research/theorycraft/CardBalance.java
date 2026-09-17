package thaumcraft.api.research.theorycraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;


public class CardBalance extends TheorycraftCard {

	@Override
	public int getInspirationCost() {
		return 1;
	}
		
	@Override
	public String getLocalizedName() {
		return Component.translatable("card.balance.name").getString();
	}
	
	@Override
	public String getLocalizedText() {
		return Component.translatable("card.balance.text").getString();
	}
	
	@Override
	public boolean initialize(Player player, ResearchTableData data) {
		return thaumcraft.common.lib.research.theorycraft.logic.CardBalanceLogic.canInitialize(data.categoryTotals, data.categoriesBlocked);
	}

	@Override
	public boolean activate(Player player, ResearchTableData data) {
		boolean success = thaumcraft.common.lib.research.theorycraft.logic.CardBalanceLogic.calculateBalancedTotals(data.categoryTotals, data.categoriesBlocked);
		if (!success) return false;

		data.addTotal("BASICS", 5);
		data.penaltyStart++;
		return true;
	}
	
	
}
