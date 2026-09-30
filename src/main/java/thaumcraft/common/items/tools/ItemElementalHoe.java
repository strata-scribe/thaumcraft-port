package thaumcraft.common.items.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Hoe of the Growth:
 * - Accelerates crop growth across an expanded area similar to bonemeal.
 */
public class ItemElementalHoe extends Item {

    public ItemElementalHoe(Properties properties) {
        super(properties);
    }

    public ItemElementalHoe() {
        this(new Item.Properties().stacksTo(1).durability(1500));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);

        // Check if clicked block is a crop / BonemealableBlock
        if (!(clickedState.getBlock() instanceof BonemealableBlock)) {
            return InteractionResult.PASS;
        }

        boolean acceleratedAny = false;
        RandomSource random = level.getRandom();

        // Iterates 3x3 horizontal area around clicked pos
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos targetPos = clickedPos.offset(dx, 0, dz);
                BlockState targetState = level.getBlockState(targetPos);
                if (targetState.getBlock() instanceof BonemealableBlock bonemealable) {
                    boolean isCenter = (dx == 0 && dz == 0);
                    double randomRoll = random.nextDouble();
                    if (ElementalToolLogic.shouldAccelerateCropGrowth(randomRoll, isCenter)) {
                        if (bonemealable.isValidBonemealTarget(level, targetPos, targetState)) {
                            if (level instanceof ServerLevel serverLevel) {
                                bonemealable.performBonemeal(serverLevel, random, targetPos, targetState);
                                serverLevel.levelEvent(1505, targetPos, 0); // Bonemeal particle effect
                            }
                            acceleratedAny = true;
                        }
                    }
                }
            }
        }

        if (acceleratedAny) {
            Player player = context.getPlayer();
            if (player != null) {
                EquipmentSlot slot = context.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                context.getItemInHand().hurtAndBreak(1, player, slot);
            }
        }

        return InteractionResult.SUCCESS;
    }
}
