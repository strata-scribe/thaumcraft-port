package thaumcraft.api.research.theorycraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.common.lib.research.theorycraft.CardExperimentationLogic;

public class CardExperimentation extends TheorycraftCard {

	@Override
	public int getInspirationCost() {
		return 2;
	}
	
	@Override
	public String getLocalizedName() {
		return Component.translatable("card.experimentation.name").getString();
	}
	
	@Override
	public String getLocalizedText() {
		return Component.translatable("card.experimentation.text").getString();
	}
	
	@Override
	public boolean activate(Player player, ResearchTableData data) {		
		try {
			String[] s = ResearchCategories.researchCategories.keySet().toArray(new String[] {});
			String cat = s[ player.getRandom().nextInt(s.length) ];
			ResearchCategory rc = ResearchCategories.getResearchCategory(cat);

			int playerKnowledge = 0;
			if (rc != null) {
				playerKnowledge = ThaumcraftCapabilities.getKnowledge(player).getKnowledge(IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, rc);
			}

			double prob = CardExperimentationLogic.calculateSuccessProbability(playerKnowledge, data.inspiration);
			boolean success = player.getRandom().nextDouble() < prob;

			int baseCatPts = player.getRandom().nextIntBetweenInclusive(15, 30);
			int baseBasicsPts = player.getRandom().nextIntBetweenInclusive(1, 10);

			data.addTotal(cat, CardExperimentationLogic.computeProgressPoints(baseCatPts, success));
			data.addTotal("BASICS", CardExperimentationLogic.computeProgressPoints(baseBasicsPts, success));

			int warpPenalty = CardExperimentationLogic.computeWarpPenalty(success, data.inspiration);
			if (warpPenalty > 0) {
				ThaumcraftApi.internalMethods.addWarpToPlayer(player, warpPenalty, IPlayerWarp.EnumWarpType.TEMPORARY);
			}
		} catch (Exception e) {
			return false;
		}		
		return true;
	}
	
	
}
