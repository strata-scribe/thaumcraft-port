package thaumcraft.common.items.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Primal Crusher:
 * - Infuses mining and digging into a single primal tool.
 * - Excavates 3x3 planar areas of stone, dirt, sand, and gravel.
 * - Inflicts bonus damage against eldritch and tainted entities.
 */
public class ItemPrimalCrusher extends Item {

    public ItemPrimalCrusher(Properties properties) {
        super(properties);
    }

    public ItemPrimalCrusher() {
        this(new Item.Properties().stacksTo(1).durability(500));
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        float damageBonus = PrimalCrusherLogic.calculateDamageBonus(target.getType().getDescriptionId(), 8.0f);
        if (damageBonus > 8.0f && attacker != null) {
            DamageSource source = (attacker instanceof Player player)
                    ? attacker.damageSources().playerAttack(player)
                    : attacker.damageSources().mobAttack(attacker);
            target.hurt(source, damageBonus - 8.0f);
        }
        if (attacker != null) {
            stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        Direction face = context.getClickedFace();
        PrimalCrusherLogic.BlockFace logicFace;
        try {
            logicFace = PrimalCrusherLogic.BlockFace.valueOf(face.name());
        } catch (IllegalArgumentException e) {
            logicFace = PrimalCrusherLogic.BlockFace.UP;
        }

        List<PrimalCrusherLogic.BlockCoordinate> grid = PrimalCrusherLogic.calculate3x3Grid(
                new PrimalCrusherLogic.BlockCoordinate(pos.getX(), pos.getY(), pos.getZ()),
                logicFace
        );

        boolean minedAny = false;
        for (PrimalCrusherLogic.BlockCoordinate coord : grid) {
            BlockPos targetPos = new BlockPos(coord.x(), coord.y(), coord.z());
            BlockState state = level.getBlockState(targetPos);
            String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
            if (PrimalCrusherLogic.isEffectiveMaterial(blockId) || PrimalCrusherLogic.isEffectiveMaterial(state.getBlock().getDescriptionId())) {
                if (!level.isClientSide()) {
                    level.destroyBlock(targetPos, true, player);
                }
                minedAny = true;
            }
        }

        if (minedAny) {
            if (player != null) {
                EquipmentSlot slot = context.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                context.getItemInHand().hurtAndBreak(1, player, slot);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
