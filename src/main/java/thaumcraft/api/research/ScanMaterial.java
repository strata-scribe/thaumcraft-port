package thaumcraft.api.research;
import net.minecraft.world.entity.player.Player;
public class ScanMaterial implements IScanThing {
    public ScanMaterial(String research, Object... mats) {}
    @Override public boolean checkThing(Player player, Object obj) { return false; }
    @Override public String getResearchKey(Player player, Object object) { return null; }
}