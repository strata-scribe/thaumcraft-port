package thaumcraft.common.lib.research;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.research.IScanThing;

public class ScanEnchantment implements IScanThing {
    private final String research;

    public ScanEnchantment(String research) {
        this.research = research;
    }

    @Override
    public boolean checkThing(Player player, Object obj) {
        if (obj instanceof ItemStack stack) {
            return !stack.isEmpty() && stack.isEnchanted();
        }
        return false;
    }

    @Override
    public String getResearchKey(Player player, Object object) {
        return research;
    }
}
