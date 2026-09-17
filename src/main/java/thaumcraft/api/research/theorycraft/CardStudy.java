package thaumcraft.api.research.theorycraft;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;



public class CardStudy extends TheorycraftCard {
	
	String cat = "BASICS";
	
	@Override
	public CompoundTag serialize() {
		CompoundTag nbt = super.serialize();
		nbt.putString("cat", cat);
		return nbt;
	}

	@Override
	public void deserialize(CompoundTag nbt) {
		super.deserialize(nbt);
		cat = nbt.getStringOr("cat", "");
	}
	
	@Override
	public String getResearchCategory() {
		return cat;
	}
	
	@Override
	public boolean initialize(Player player, ResearchTableData data) { 
		Random r = new Random(getSeed());
		ArrayList<String> list = data.getAvailableCategories(player);
		cat = list.get(r.nextInt(list.size()));
		return cat!=null;
	}
	
	@Override
	public boolean isAidOnly() {
		return true;
	}

	@Override
	public int getInspirationCost() {
		return 1;
	}
	
	@Override
	public String getLocalizedName() {
		return Component.translatable("card.study.name", ChatFormatting.DARK_BLUE+""+ChatFormatting.BOLD+Component.translatable("tc.research_category."+cat).getString()+ChatFormatting.RESET).getString();
	}
	
	@Override
	public String getLocalizedText() {
		return Component.translatable("card.study.text", ChatFormatting.BOLD+Component.translatable("tc.research_category."+cat).getString()+ChatFormatting.RESET).getString();
	}
	
	@Override
	public boolean activate(Player player, ResearchTableData data) {		
		thaumcraft.api.research.ResearchCategory rc = thaumcraft.api.research.ResearchCategories.getResearchCategory(cat);
		int playerObservationLevel = 0;
		if (rc != null) {
			playerObservationLevel = thaumcraft.api.capabilities.ThaumcraftCapabilities.getKnowledge(player).getKnowledge(thaumcraft.api.capabilities.IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, rc);
		}

		int itemRarity = player.getRandom().nextIntBetweenInclusive(0, 2);
		int aspectDensity = player.getRandom().nextIntBetweenInclusive(1, 5);

		int yield = thaumcraft.common.lib.research.theorycraft.logic.CardStudyLogic.calculateStudyYield(itemRarity, aspectDensity, playerObservationLevel);

		data.addTotal(cat, yield);
		return true;
	}
	
	
}
