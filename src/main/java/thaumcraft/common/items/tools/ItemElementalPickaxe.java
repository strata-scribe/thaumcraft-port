package thaumcraft.common.items.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;

import java.util.ArrayList;
import java.util.List;

/**
 * Pickaxe of the Core:
 * - Detects rare subterranean ores via sounding pulses.
 * - Yields bonus native clusters during ore excavation.
 */
public class ItemElementalPickaxe extends Item {

    public static final int SOUNDING_RADIUS = 16;

    public ItemElementalPickaxe(Properties properties) {
        super(properties);
    }

    public ItemElementalPickaxe() {
        this(new Item.Properties().stacksTo(1).durability(1500));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // Server side: Ore sounding pulse within radius 16 of player position
        int px = player.getBlockX();
        int py = player.getBlockY();
        int pz = player.getBlockZ();
        List<ElementalToolLogic.SoundedOre> detectedOres = new ArrayList<>();

        for (int dx = -SOUNDING_RADIUS; dx <= SOUNDING_RADIUS; dx++) {
            for (int dy = -SOUNDING_RADIUS; dy <= SOUNDING_RADIUS; dy++) {
                for (int dz = -SOUNDING_RADIUS; dz <= SOUNDING_RADIUS; dz++) {
                    double distSq = dx * dx + dy * dy + dz * dz;
                    if (distSq <= SOUNDING_RADIUS * SOUNDING_RADIUS) {
                        BlockPos pos = new BlockPos(px + dx, py + dy, pz + dz);
                        BlockState state = level.getBlockState(pos);
                        int tier = getOreRarityTier(state);
                        if (tier > 0) {
                            detectedOres.add(new ElementalToolLogic.SoundedOre(
                                    new ElementalToolLogic.BlockCoordinate(pos.getX(), pos.getY(), pos.getZ()),
                                    tier,
                                    Math.sqrt(distSq)
                            ));
                        }
                    }
                }
            }
        }

        ElementalToolLogic.SoundedOre prioritized = ElementalToolLogic.findPrioritizedOre(detectedOres);
        if (prioritized != null) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 1.0f);
        } else {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.STONE_HIT, SoundSource.PLAYERS, 0.8f, 0.6f);
        }

        EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        player.getItemInHand(hand).hurtAndBreak(1, player, slot);

        return InteractionResult.SUCCESS;
    }

    public static int getOreRarityTier(BlockState state) {
        if (state == null || state.isAir()) {
            return 0;
        }
        String name = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString().toLowerCase();
        if (name.contains("thaumcraft") || name.contains("amber") || name.contains("cinnabar") || name.contains("infused")) {
            return ElementalToolLogic.ORE_TIER_THAUMIC;
        }
        if (name.contains("diamond") || name.contains("emerald") || name.contains("debris") || name.contains("netherite")) {
            return ElementalToolLogic.ORE_TIER_RARE;
        }
        if (name.contains("gold") || name.contains("lapis") || name.contains("redstone")) {
            return ElementalToolLogic.ORE_TIER_PRECIOUS;
        }
        if (state.is(Tags.Blocks.ORES) || name.contains("ore") || name.contains("coal") || name.contains("iron") || name.contains("copper")) {
            return ElementalToolLogic.ORE_TIER_COMMON;
        }
        return 0;
    }
}
