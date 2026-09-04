package thaumcraft.common.lib.research.theorycraft;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.blocks.ThaumcraftBlocks;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;


public class AidGlyphedStone implements ITheorycraftAid
{
    @Override
    public Object getAidObject() {
        return new ItemStack(ThaumcraftBlocks.stoneAncientGlyphed.get());
    }
    
    @Override
    public Class<TheorycraftCard>[] getCards() {
        return new Class[] { CardGlyphs.class };
    }
}
