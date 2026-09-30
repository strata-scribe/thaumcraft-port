package thaumcraft.common.lib.research;

import net.minecraft.world.entity.player.Player;
import thaumcraft.api.research.IScanThing;

public class ScanSky implements IScanThing {
    private final String research;

    public ScanSky(String research) {
        this.research = research;
    }

    public static boolean isUpwardView(double viewVectorY) {
        return viewVectorY > 0.5;
    }

    @Override
    public boolean checkThing(Player player, Object obj) {
        if (obj != null) return false;
        if (player == null) return false;
        return isLookingAtSky(player);
    }

    protected boolean isLookingAtSky(Player player) {
        return isUpwardView(player.getViewVector(1.0f).y);
    }

    @Override
    public String getResearchKey(Player player, Object object) {
        return research;
    }
}
