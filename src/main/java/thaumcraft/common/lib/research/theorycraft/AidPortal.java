package thaumcraft.common.lib.research.theorycraft;
import net.minecraft.world.level.block.Blocks;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
// import thaumcraft.common.entities.monster.cult.EntityCultistPortalLesser;


public class AidPortal implements ITheorycraftAid
{
    Object portal;
    java.util.function.Supplier<Object> portalSupplier;
    
    public AidPortal(Object o) {
        portal = o;
    }

    public AidPortal(java.util.function.Supplier<Object> supplier) {
        this.portalSupplier = supplier;
    }
    
    @Override
    public Object getAidObject() {
        if (portal != null) {
            return portal;
        }
        if (portalSupplier != null) {
            return portalSupplier.get();
        }
        return null;
    }
    
    @Override
    public Class<TheorycraftCard>[] getCards() {
        return new Class[] { CardPortal.class };
    }
    
    public static class AidPortalEnd extends AidPortal
    {
        public AidPortalEnd() {
            super(() -> Blocks.END_PORTAL);
        }
    }
    
    public static class AidPortalNether extends AidPortal
    {
        public AidPortalNether() {
            super(() -> net.minecraft.world.level.block.Blocks.NETHER_PORTAL);
        }
    }
    
    public static class AidPortalCrimson extends AidPortal
    {
        public AidPortalCrimson() {
            super(() -> net.minecraft.world.entity.EntityType.ZOMBIE);
        }
    }
}
