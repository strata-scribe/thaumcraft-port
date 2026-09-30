package thaumcraft.common.items.tools;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.items.tools.logic.SanityCheckerLogic;

public class ItemSanityChecker extends Item {

    public ItemSanityChecker(Properties properties) {
        super(properties);
    }

    public ItemSanityChecker() {
        this(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            try {
                IPlayerWarp warp = ThaumcraftCapabilities.getWarp(player);
                if (warp != null) {
                    int perm = warp.get(IPlayerWarp.EnumWarpType.PERMANENT);
                    int normal = warp.get(IPlayerWarp.EnumWarpType.NORMAL);
                    int temp = warp.get(IPlayerWarp.EnumWarpType.TEMPORARY);
                    int total = SanityCheckerLogic.calculateTotalWarp(perm, normal, temp);
                    String danger = SanityCheckerLogic.getWarpDangerLevel(total);
                }
            } catch (Throwable ignored) {
            }
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }
}
