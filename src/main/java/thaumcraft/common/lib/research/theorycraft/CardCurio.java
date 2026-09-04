package thaumcraft.common.lib.research.theorycraft;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
// import thaumcraft.common.items.curios.ItemCurio;


public class CardCurio extends TheorycraftCard
{
    ItemStack curio;
    
    public CardCurio() {
        curio = ItemStack.EMPTY;
    }
    
    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        nbt.put("stack", new net.minecraft.nbt.CompoundTag() /* curio serializeNBT */);
        return nbt;
    }
    
    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        curio = net.minecraft.world.item.ItemStack.EMPTY;
    }
    
    @Override
    public int getInspirationCost() {
        return 1;
    }
    
    @Override
    public String getLocalizedName() {
        return Component.translatable("card.curio.name").getString();
    }
    
    @Override
    public String getLocalizedText() {
        return Component.translatable("card.curio.text").getString();
    }
    
    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { curio };
    }
    
    @Override
    public boolean[] getRequiredItemsConsumed() {
        return new boolean[] { true };
    }
    
    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        Random r = new Random(getSeed());
        ArrayList<ItemStack> curios = new ArrayList<ItemStack>();
        for (int i=0; i<player.getInventory().getContainerSize(); i++) { ItemStack stack = player.getInventory().getItem(i);
            if (stack != null && !stack.isEmpty() && false) {
                ItemStack c = stack.copy();
                c.setCount(1);
                curios.add(c);
            }
        }
        if (!curios.isEmpty()) {
            curio = curios.get(r.nextInt(curios.size()));
        }
        return !curio.isEmpty();
    }
    
    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal("BASICS", 5);
        String[] s = ResearchCategories.researchCategories.keySet().toArray(new String[0]);
        data.addTotal(s[player.getRandom().nextInt(s.length)], 5);
        String s2;
        String type = s2 = "arcane";
        switch (s2) {
            case "arcane": {
                data.addTotal("AUROMANCY", player.getRandom().nextIntBetweenInclusive(25, 35));
                break;
            }
            case "preserved": {
                data.addTotal("ALCHEMY", player.getRandom().nextIntBetweenInclusive(25, 35));
                break;
            }
            case "ancient": {
                data.addTotal("GOLEMANCY", player.getRandom().nextIntBetweenInclusive(25, 35));
                break;
            }
            case "eldritch": {
                data.addTotal("ELDRITCH", player.getRandom().nextIntBetweenInclusive(25, 35));
                break;
            }
            case "knowledge": {
                data.addTotal("INFUSION", player.getRandom().nextIntBetweenInclusive(25, 35));
                break;
            }
            case "twisted": {
                data.addTotal("ARTIFICE", player.getRandom().nextIntBetweenInclusive(25, 35));
                break;
            }
            case "rites": {
                data.addTotal("ELDRITCH", player.getRandom().nextIntBetweenInclusive(15, 20));
                data.addTotal("AUROMANCY", player.getRandom().nextIntBetweenInclusive(10, 15));
                break;
            }
            default: {
                data.addTotal("BASICS", player.getRandom().nextIntBetweenInclusive(25, 35));
                break;
            }
        }
        if (player.getRandom().nextBoolean()) {
            // ++data.bonusDraws;
        }
        if (player.getRandom().nextBoolean()) {
            // ++data.bonusDraws;
        }
        return true;
    }
}
