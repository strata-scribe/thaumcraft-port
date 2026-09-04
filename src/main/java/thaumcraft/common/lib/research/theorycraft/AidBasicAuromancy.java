package thaumcraft.common.lib.research.theorycraft;
import thaumcraft.api.blocks.ThaumcraftBlocks;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;


public class AidBasicAuromancy implements ITheorycraftAid
{
    @Override
    public Object getAidObject() {
        return ThaumcraftBlocks.wandWorkbench.get();
    }
    
    @Override
    public Class<TheorycraftCard>[] getCards() {
        return new Class[] { CardFocus.class, CardAwareness.class, CardSpellbinding.class };
    }
}
