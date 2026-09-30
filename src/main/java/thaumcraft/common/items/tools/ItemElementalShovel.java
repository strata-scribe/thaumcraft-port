package thaumcraft.common.items.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Shovel of the Earthmover:
 * - Excavates and places 3x3 planar earth/gravel/sand grids aligned to the clicked block face.
 */
public class ItemElementalShovel extends Item {

    public ItemElementalShovel(Properties properties) {
        super(properties);
    }

    public ItemElementalShovel() {
        this(new Item.Properties().stacksTo(1).durability(1500));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        Direction clickedFace = context.getClickedFace();

        ElementalToolLogic.BlockFace face;
        try {
            face = ElementalToolLogic.BlockFace.valueOf(clickedFace.name());
        } catch (IllegalArgumentException e) {
            face = ElementalToolLogic.BlockFace.UP;
        }

        List<ElementalToolLogic.BlockCoordinate> grid = ElementalToolLogic.calculate3x3Grid(
                new ElementalToolLogic.BlockCoordinate(pos.getX(), pos.getY(), pos.getZ()),
                face
        );

        boolean affectedAny = false;
        for (ElementalToolLogic.BlockCoordinate coord : grid) {
            BlockPos targetPos = new BlockPos(coord.x(), coord.y(), coord.z());
            BlockState state = level.getBlockState(targetPos);

            // Flattening check: grass/dirt to dirt path if air above
            if (isFlattenable(state) && level.getBlockState(targetPos.above()).isAir()) {
                if (!level.isClientSide()) {
                    level.setBlock(targetPos, Blocks.DIRT_PATH.defaultBlockState(), 11);
                }
                affectedAny = true;
            } else if (state.is(BlockTags.MINEABLE_WITH_SHOVEL) || isDiggable(state)) {
                // Digging check
                if (!level.isClientSide()) {
                    level.destroyBlock(targetPos, true, player);
                }
                affectedAny = true;
            }
        }

        if (affectedAny && player != null) {
            EquipmentSlot slot = context.getHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            context.getItemInHand().hurtAndBreak(1, player, slot);
        }

        return InteractionResult.SUCCESS;
    }

    private static boolean isFlattenable(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK) ||
               state.is(Blocks.DIRT) ||
               state.is(Blocks.PODZOL) ||
               state.is(Blocks.COARSE_DIRT) ||
               state.is(Blocks.MYCELIUM) ||
               state.is(Blocks.ROOTED_DIRT);
    }

    private static boolean isDiggable(BlockState state) {
        if (state.isAir()) {
            return false;
        }
        return state.is(Blocks.SAND) ||
               state.is(Blocks.GRAVEL) ||
               state.is(Blocks.CLAY) ||
               state.is(Blocks.SOUL_SAND) ||
               state.is(Blocks.SOUL_SOIL) ||
               state.is(Blocks.MUD);
    }
}
