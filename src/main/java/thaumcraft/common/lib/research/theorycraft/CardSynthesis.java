package thaumcraft.common.lib.research.theorycraft;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.common.lib.research.theorycraft.logic.CardSynthesisLogic;

public class CardSynthesis extends TheorycraftCard
{
    Aspect aspect1;
    Aspect aspect2;
    Aspect aspect3;
    
    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        if (aspect1 != null) nbt.putString("aspect1", aspect1.getTag());
        if (aspect2 != null) nbt.putString("aspect2", aspect2.getTag());
        if (aspect3 != null) nbt.putString("aspect3", aspect3.getTag());
        return nbt;
    }
    
    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        aspect1 = Aspect.getAspect(nbt.getStringOr("aspect1", ""));
        aspect2 = Aspect.getAspect(nbt.getStringOr("aspect2", ""));
        aspect3 = Aspect.getAspect(nbt.getStringOr("aspect3", ""));
    }
    
    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        List<String[]> compoundAspects = new ArrayList<>();
        for (Aspect a : Aspect.getCompoundAspects()) {
            compoundAspects.add(new String[] { a.getTag(), a.getComponents()[0].getTag(), a.getComponents()[1].getTag() });
        }

        CardSynthesisLogic.SynthesisResult result = CardSynthesisLogic.initializeSynthesis(getSeed(), compoundAspects);
        if (result != null) {
            aspect3 = Aspect.getAspect(result.aspect3);
            aspect1 = Aspect.getAspect(result.aspect1);
            aspect2 = Aspect.getAspect(result.aspect2);
            return true;
        }
        return false;
    }
    
    @Override
    public int getInspirationCost() {
        return 1;
    }
    
    @Override
    public String getResearchCategory() {
        return "ALCHEMY";
    }
    
    @Override
    public String getLocalizedName() {
        return Component.translatable("card.synthesis.name").getString();
    }
    
    @Override
    public String getLocalizedText() {
        return Component.translatable("card.synthesis.text", ChatFormatting.BOLD + aspect1.getName() + ChatFormatting.RESET, ChatFormatting.BOLD + aspect2.getName() + ChatFormatting.RESET).getString();
    }
    
    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { ThaumcraftApiHelper.makeCrystal(aspect1), ThaumcraftApiHelper.makeCrystal(aspect2) };
    }
    
    @Override
    public boolean[] getRequiredItemsConsumed() {
        return new boolean[] { true, true };
    }
    
    @Override
    public boolean activate(Player player, ResearchTableData data) {
        CardSynthesisLogic.ActivationResult actResult = CardSynthesisLogic.calculateActivation(player.getRandom().nextFloat());
        data.addTotal(getResearchCategory(), actResult.bonusProgress);
        if (actResult.bonusInspiration > 0) {
            data.addInspiration(actResult.bonusInspiration);
        }

        ItemStack res = ThaumcraftApiHelper.makeCrystal(aspect3);
        if (!player.getInventory().add(res)) {
            player.drop(res, true);
        }
        return true;
    }
}
