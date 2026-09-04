package thaumcraft.common.lib.research.theorycraft;
import java.util.Random;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
// import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;


public class CardTinker extends TheorycraftCard
{
    ItemStack stack;
    static ItemStack[] options;
    
    public CardTinker() {
        stack = ItemStack.EMPTY;
    }
    
    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        // nbt.put("stack", stack.serializeNBT());
        return nbt;
    }
    
    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        stack = net.minecraft.world.item.ItemStack.EMPTY; // TODO: restore from NBT
    }
    
    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        Random r = new Random(getSeed());
        stack = CardTinker.options[r.nextInt(CardTinker.options.length)].copy();
        return stack != null;
    }
    
    @Override
    public int getInspirationCost() {
        return 1;
    }
    
    @Override
    public String getResearchCategory() {
        return "ARTIFICE";
    }
    
    private int getVal() {
        int q = 0;
        try {
            q += (int)0;
        }
        catch (Exception ex) {}
        return q;
    }
    
    @Override
    public String getLocalizedName() {
        return Component.translatable("card.tinker.name").getString();
    }
    
    @Override
    public String getLocalizedText() {
        int a = getVal() * 2;
        int b = a + 10;
        return Component.translatable("card.tinker.text").getString();
    }
    
    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { stack };
    }
    
    @Override
    public boolean activate(Player player, ResearchTableData data) {
        int q = getVal() * 2;
        data.addTotal(getResearchCategory(), player.getRandom().nextIntBetweenInclusive(q, q + 10));
        return true;
    }
    
    static {
        CardTinker.options = new ItemStack[0];
    }
}
