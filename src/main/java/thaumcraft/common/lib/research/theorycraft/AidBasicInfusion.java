package thaumcraft.common.lib.research.theorycraft;
import thaumcraft.api.blocks.ThaumcraftBlocks;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;


public class AidBasicInfusion implements ITheorycraftAid
{
    @Override
    public Object getAidObject() {
        return ThaumcraftBlocks.infusionMatrix.get();
    }
    
    @Override
    public Class<TheorycraftCard>[] getCards() {
        return new Class[] { CardMeasure.class, CardChannel.class, CardInfuse.class };
    }
}
