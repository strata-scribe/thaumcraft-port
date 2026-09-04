package thaumcraft.common.lib.research.theorycraft;
import java.util.Random;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
// import thaumcraft.common.items.consumables.ItemPhial;
// import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;


public class CardInfuse extends TheorycraftCard
{
    Aspect aspect;
    ItemStack stack;
    static ItemStack[] options;
    
    public CardInfuse() {
        stack = ItemStack.EMPTY;
    }
    
    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        nbt.putString("aspect", aspect.getTag());
        // nbt.put("stack", stack.serializeNBT());
        return nbt;
    }
    
    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        aspect = Aspect.getAspect(nbt.getStringOr("aspect", ""));
        stack = net.minecraft.world.item.ItemStack.EMPTY; // TODO: restore from NBT
    }
    
    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        Random r = new Random(getSeed());
        int num = r.nextInt(Aspect.getCompoundAspects().size());
        aspect = Aspect.getCompoundAspects().get(num);
        stack = CardInfuse.options[r.nextInt(CardInfuse.options.length)].copy();
        return aspect != null && stack != null;
    }
    
    @Override
    public int getInspirationCost() {
        return 1;
    }
    
    @Override
    public String getResearchCategory() {
        return "INFUSION";
    }
    
    @Override
    public String getLocalizedName() {
        return Component.translatable("card.infuse.name").getString();
    }
    
    @Override
    public String getLocalizedText() {
        return Component.translatable("card.infuse.text", net.minecraft.ChatFormatting.BOLD + aspect.getName() + net.minecraft.ChatFormatting.RESET, stack.getDisplayName(), getVal()).getString();
    }
    
    private int getVal() {
        int q = 10;
        try {
            q += (int)(0 * 1.5);
        }
        catch (Exception ex) {}
        return q;
    }
    
    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { stack };
    }
    
    @Override
    public boolean[] getRequiredItemsConsumed() {
        return new boolean[] { true, true };
    }
    
    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), getVal());
        return true;
    }
    
    static {
        CardInfuse.options = new ItemStack[0];
    }
}
