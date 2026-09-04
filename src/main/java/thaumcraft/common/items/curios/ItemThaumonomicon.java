package thaumcraft.common.items.curios;

import net.minecraft.world.InteractionHand;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.SoundsTC;
// import thaumcraft.client.gui.GuiThaumonomicon;

import java.util.Collection;

public class ItemThaumonomicon extends Item {
    public ItemThaumonomicon(Properties properties) {
        super(properties);
    }

    @Override
    public net.minecraft.world.InteractionResult use(Level level, Player player, net.minecraft.world.InteractionHand hand) {
        if (!level.isClientSide()) {
            Collection<ResearchCategory> rc = ResearchCategories.researchCategories.values();
            for (ResearchCategory cat : rc) {
                Collection<ResearchEntry> rl = cat.research.values();
                for (ResearchEntry ri : rl) {
                    if (ThaumcraftCapabilities.knowsResearch(player, ri.getKey()) && ri.getSiblings() != null) {
                        for (String sib : ri.getSiblings()) {
                            if (!ThaumcraftCapabilities.knowsResearch(player, sib)) {
                                ResearchManager.completeResearch(player, sib);
                            }
                        }
                    }
                }
            }
            ThaumcraftCapabilities.getKnowledge(player).sync((ServerPlayer) player);
        } else {
            level.playLocalSound(player.getX(), player.getY(), player.getZ(), net.minecraft.sounds.SoundEvents.BOOK_PAGE_TURN, SoundSource.PLAYERS, 1.0F, 1.0F, false);
            thaumcraft.client.ClientHooks.openThaumonomicon();
        }
        
        return net.minecraft.world.InteractionResult.SUCCESS;
    }
}
