package thaumcraft.common.lib.research.theorycraft;
import thaumcraft.api.blocks.ThaumcraftBlocks;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;


public class AidBasicGolemancy implements ITheorycraftAid
{
    @Override
    public Object getAidObject() {
        return ThaumcraftBlocks.golemBuilder.get();
    }
    
    @Override
    public Class<TheorycraftCard>[] getCards() {
        return new Class[] { CardSculpting.class, CardScripting.class, CardSynergy.class };
    }
}
