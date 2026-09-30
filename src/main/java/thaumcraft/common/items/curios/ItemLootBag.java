package thaumcraft.common.items.curios;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.loot.logic.LootBagRarityRollLogic;
import thaumcraft.common.lib.SoundsTC;

public class ItemLootBag extends Item {

    private final int rarityTier;

    public ItemLootBag(Properties properties, int rarityTier) {
        super(properties);
        this.rarityTier = rarityTier;
    }

    public ItemLootBag(Properties properties) {
        this(properties, 0);
    }

    public int getRarityTier() {
        return rarityTier;
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (!world.isClientSide()) {
            LootBagRarityRollLogic.LootBagResult result = LootBagRarityRollLogic.calculateLootBagResult(
                    world.getRandom().nextDouble(),
                    world.getRandom().nextDouble(),
                    this.rarityTier
            );

            if (result.goldCoins > 0) {
                giveItem(player, new ItemStack(Items.GOLD_NUGGET, result.goldCoins));
            }
            if (result.aspectCrystals > 0) {
                giveItem(player, new ItemStack(ItemsTC.crystalEssence, result.aspectCrystals));
            }
            if (result.treasureTier != null) {
                switch (result.treasureTier) {
                    case COMMON:
                        giveItem(player, new ItemStack(ItemsTC.amber, 1));
                        break;
                    case UNCOMMON:
                        giveItem(player, new ItemStack(ItemsTC.baubles, 1));
                        break;
                    case RARE:
                        giveItem(player, new ItemStack(ItemsTC.primordialPearl, 1));
                        break;
                }
            }

            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundsTC.POOF.get(), SoundSource.PLAYERS, 1.0F, 1.0F + (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.2F);

            itemstack.shrink(1);
        }

        return InteractionResult.CONSUME;
    }

    private void giveItem(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }
}
