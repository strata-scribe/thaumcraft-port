package thaumcraft.api.research.theorycraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import thaumcraft.common.lib.research.theorycraft.logic.CardPonderLogic;

import java.util.HashSet;
import java.util.Map;

public class CardPonder extends TheorycraftCard {

	@Override
	public int getInspirationCost() {
		return CardPonderLogic.INSPIRATION_COST;
	}
		
	@Override
	public String getLocalizedName() {
		return Component.translatable("card.ponder.name").getString();
	}
	
	@Override
	public String getLocalizedText() {
		return Component.translatable("card.ponder.text").getString();
	}
	
	@Override
	public boolean initialize(Player player, ResearchTableData data) {
		return data.categoriesBlocked.size()<data.categoryTotals.size();
	}

	@Override
	public boolean activate(Player player, ResearchTableData data) {
		CardPonderLogic.AllocationResult result = CardPonderLogic.calculateProgressAllocation(
				data.categoryTotals.keySet(),
				new HashSet<>(data.categoriesBlocked)
		);

		if (result.earlyExit) {
			return false;
		}

		for (Map.Entry<String, Integer> entry : result.allocations.entrySet()) {
			data.addTotal(entry.getKey(), entry.getValue());
		}

		data.bonusDraws++;
		return result.success;
	}
	
	
}
