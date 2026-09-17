package thaumcraft.api.research.theorycraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import thaumcraft.common.lib.research.theorycraft.logic.CardRethinkLogic;

public class CardRethink extends TheorycraftCard {
	
	@Override
	public boolean initialize(Player player, ResearchTableData data) {
		return CardRethinkLogic.checkInitialization(data.categoryTotals);
	}

	@Override
	public int getInspirationCost() {
		return -1;
	}
	
	@Override
	public String getLocalizedName() {
		return Component.translatable("card.rethink.name").getString();
	}
	
	@Override
	public String getLocalizedText() {
		return Component.translatable("card.rethink.text").getString();
	}

	@Override
	public boolean activate(Player player, ResearchTableData data) {
		CardRethinkLogic.RethinkResult result = CardRethinkLogic.calculateRethink(
			data.categoryTotals,
			data.bonusDraws,
			player.getRandom().nextIntBetweenInclusive(1, 10),
			data.inspirationStart,
			data.inspiration
		);

		if (result == null) return false;

		data.categoryTotals.clear();
		data.categoryTotals.putAll(result.updatedTotals);
		data.bonusDraws = result.bonusDraws;
		data.addInspiration(result.refundedInspiration);

		return true;
	}
	
	
}
