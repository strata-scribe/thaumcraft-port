package thaumcraft.common.items.casters.foci;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.api.casters.focus.FocusExchangeLogic;

public class FocusEffectExchange extends thaumcraft.api.casters.focus.FocusEffectExchange {

    public FocusEffectExchange() {
        super();
    }

    @Override
    public boolean execute(HitResult target, @Nullable Trajectory trajectory, float finalPower, int num) {
        if (target == null || getPackage() == null || getPackage().world == null) {
            return false;
        }
        Level world = getPackage().world;
        LivingEntity caster = getPackage().getCaster();

        if (caster instanceof Player player && target instanceof BlockHitResult bhr) {
            BlockPos pos = bhr.getBlockPos();
            BlockState targetState = world.getBlockState(pos);
            ItemStack mainHandItem = player.getMainHandItem();

            if (player.isCrouching()) {
                // Select block
                if (mainHandItem != null && !mainHandItem.isEmpty()) {
                    Identifier blockId = BuiltInRegistries.BLOCK.getKey(targetState.getBlock());
                    CompoundTag tag = mainHandItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
                    tag.putString("exchange_block", blockId.toString());
                    mainHandItem.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                    world.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.5f, 2.0f);
                    return true;
                }
            } else {
                // Execute exchange
                if (mainHandItem != null && mainHandItem.has(DataComponents.CUSTOM_DATA)) {
                    CompoundTag tag = mainHandItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
                    if (tag.contains("exchange_block")) {
                        String blockIdStr = tag.getString("exchange_block").orElse("");
                        Identifier blockId = Identifier.parse(blockIdStr);
                        Block savedBlock = BuiltInRegistries.BLOCK.getOptional(blockId).orElse(null);

                        if (savedBlock != null) {
                            // Find in inventory
                            int slotFound = -1;
                            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                                ItemStack invStack = player.getInventory().getItem(i);
                                if (!invStack.isEmpty() && invStack.getItem() == savedBlock.asItem()) {
                                    slotFound = i;
                                    break;
                                }
                            }

                            if (slotFound >= 0) {
                                float targetHardness = targetState.getDestroySpeed(world, pos);
                                float sourceHardness = savedBlock.defaultBlockState().getDestroySpeed(world, pos);

                                if (FocusExchangeLogic.isHardnessCompatible(targetHardness, sourceHardness)) {
                                    double distance = player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                                    distance = Math.sqrt(distance);
                                    float visCost = FocusExchangeLogic.calculateVisCost(targetHardness, sourceHardness, distance);
                                    // Normally we subtract vis cost here via aura logic.

                                    // Remove old block
                                    world.destroyBlock(pos, true, caster);
                                    // Consume item
                                    player.getInventory().getItem(slotFound).shrink(1);
                                    // Place new block
                                    world.setBlock(pos, savedBlock.defaultBlockState(), 3);
                                    world.playSound(null, pos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.5f, 1.0f);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void renderParticleFX(Level world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
        // Client side
    }

    @Override
    public void onCast(Entity caster) {
        if (caster != null && caster.level() != null) {
            caster.level().playSound(null, caster.blockPosition().above(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.1f, 2.0f);
        }
    }
}
