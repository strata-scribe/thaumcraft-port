package thaumcraft.common.lib.research;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import thaumcraft.api.research.IScanThing;

public class ScanPotion implements IScanThing {
    private final String research;

    public ScanPotion(String research) {
        this.research = research;
    }

    @Override
    public boolean checkThing(Player player, Object obj) {
        if (obj instanceof ItemStack stack) {
            return !stack.isEmpty() && stack.getItem() instanceof PotionItem;
        }
        return false;
    }

    @Override
    public String getResearchKey(Player player, Object object) {
        return research;
    }
}
