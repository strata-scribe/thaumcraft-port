package thaumcraft.common.items.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.blocks.ThaumcraftBlocks;

public class ItemHandMirror extends Item {

    public ItemHandMirror(Properties properties) {
        super(properties);
    }

    public ItemHandMirror() {
        this(new Item.Properties().stacksTo(1));
    }

    public static boolean hasValidLink(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.contains("linkX") && tag.contains("linkY") && tag.contains("linkZ") && tag.contains("linkDim") && !tag.getStringOr("linkDim", "").isEmpty();
    }

    public static BlockPos getLinkedPos(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !hasValidLink(stack)) return null;
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return new BlockPos(tag.getIntOr("linkX", 0), tag.getIntOr("linkY", 0), tag.getIntOr("linkZ", 0));
    }

    public static String getLinkedDimension(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !hasValidLink(stack)) return null;
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        String dim = tag.getStringOr("linkDim", "");
        return dim.isEmpty() ? null : dim;
    }

    public static void setLink(ItemStack stack, BlockPos pos, String dimension) {
        if (stack == null || stack.isEmpty() || pos == null || dimension == null) return;
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putInt("linkX", pos.getX());
        tag.putInt("linkY", pos.getY());
        tag.putInt("linkZ", pos.getZ());
        tag.putString("linkDim", dimension);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        boolean isMirror = false;
        if (state != null && state.getBlock() != null) {
            try {
                if (ThaumcraftBlocks.mirror != null && state.getBlock() == ThaumcraftBlocks.mirror.get()) {
                    isMirror = true;
                } else {
                    var key = BuiltInRegistries.BLOCK.getKey(state.getBlock());
                    if (key != null && key.toString().toLowerCase().contains("mirror")) {
                        isMirror = true;
                    }
                }
            } catch (Throwable ignored) {
            }
        }

        if (isMirror) {
            if (!level.isClientSide()) {
                ItemStack stack = context.getItemInHand();
                String dimId = level.dimension().identifier().toString();
                setLink(stack, pos, dimId);
            }
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!hasValidLink(stack)) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide()) {
            BlockPos targetPos = getLinkedPos(stack);
            String targetDim = getLinkedDimension(stack);
            String currentDim = level.dimension().identifier().toString();

            InteractionHand otherHand = (hand == InteractionHand.MAIN_HAND) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            ItemStack itemToDeposit = player.getItemInHand(otherHand);

            if (!itemToDeposit.isEmpty() && targetPos != null && targetDim != null) {
                HandMirrorLogic.IItemSlot[] slots = new HandMirrorLogic.IItemSlot[0];
                try {
                    BlockEntity be = level.getBlockEntity(targetPos);
                    if (be instanceof Container container) {
                        slots = wrapContainer(container);
                    }
                } catch (Throwable ignored) {
                }

                String itemId = BuiltInRegistries.ITEM.getKey(itemToDeposit.getItem()).toString();
                int count = itemToDeposit.getCount();
                double maxDistance = 64.0;

                int deposited = HandMirrorLogic.tryDeposit(
                        currentDim, player.getX(), player.getY(), player.getZ(),
                        targetDim, targetPos.getX(), targetPos.getY(), targetPos.getZ(),
                        maxDistance, slots, itemId, count
                );

                if (deposited > 0) {
                    itemToDeposit.shrink(deposited);
                }
            }
        }

        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

    private static HandMirrorLogic.IItemSlot[] wrapContainer(Container container) {
        HandMirrorLogic.IItemSlot[] slots = new HandMirrorLogic.IItemSlot[container.getContainerSize()];
        for (int i = 0; i < slots.length; i++) {
            final int slotIndex = i;
            slots[i] = new HandMirrorLogic.IItemSlot() {
                @Override
                public String getItemId() {
                    ItemStack s = container.getItem(slotIndex);
                    return s.isEmpty() ? null : BuiltInRegistries.ITEM.getKey(s.getItem()).toString();
                }

                @Override
                public int getCount() {
                    return container.getItem(slotIndex).getCount();
                }

                @Override
                public int getMaxStackSize() {
                    return container.getMaxStackSize();
                }

                @Override
                public void setItem(String id, int count) {
                    if (id == null) return;
                    try {
                        var loc = Identifier.tryParse(id);
                        if (loc != null) {
                            BuiltInRegistries.ITEM.get(loc).ifPresent(holder -> {
                                container.setItem(slotIndex, new ItemStack(holder.value(), count));
                            });
                        }
                    } catch (Throwable ignored) {
                    }
                }

                @Override
                public void addCount(int amount) {
                    ItemStack s = container.getItem(slotIndex);
                    if (!s.isEmpty()) {
                        s.grow(amount);
                    }
                }
            };
        }
        return slots;
    }
}
