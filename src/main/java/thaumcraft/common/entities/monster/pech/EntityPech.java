package thaumcraft.common.entities.monster.pech;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.items.ThaumcraftItems;
import net.minecraft.world.entity.item.ItemEntity;

public class EntityPech extends PathfinderMob {

    public enum PechType {
        HUNTER(0),
        MAGE(1),
        STALKER(2);

        private final int id;

        PechType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static PechType fromId(int id) {
            for (PechType type : values()) {
                if (type.getId() == id) {
                    return type;
                }
            }
            return HUNTER;
        }
    }

    private static final EntityDataAccessor<Integer> PECH_TYPE = SynchedEntityData.defineId(EntityPech.class, EntityDataSerializers.INT);

    public EntityPech(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PECH_TYPE, PechType.HUNTER.getId());
    }

    public PechType getPechType() {
        return PechType.fromId(this.entityData.get(PECH_TYPE));
    }

    public void setPechType(PechType type) {
        this.entityData.set(PECH_TYPE, type.getId());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setPechType(PechType.fromId(input.getIntOr("PechType", PechType.HUNTER.getId())));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("PechType", this.getPechType().getId());
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return super.hurtServer(level, source, amount);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (!itemstack.isEmpty()) {
            boolean isGoldNugget = itemstack.is(Items.GOLD_NUGGET);
            boolean isGoldIngot = itemstack.is(Items.GOLD_INGOT);
            boolean isGoldBlock = itemstack.is(Items.GOLD_BLOCK);
            boolean isGem = itemstack.is(Items.DIAMOND) || itemstack.is(Items.EMERALD);

            int aspectVisSize = 0;
            try {
                AspectList aspects = AspectHelper.getObjectAspects(itemstack);
                if (aspects != null && aspects.size() > 0) {
                    aspectVisSize = aspects.visSize();
                }
            } catch (Exception e) {
                // Ignore
            }

            int barterValue = PechBarterLogic.getBarterValue(isGoldNugget, isGoldIngot, isGoldBlock, isGem, aspectVisSize);

            if (barterValue > 0) {
                if (!this.level().isClientSide()) {
                    // Consume one item from hand
                    itemstack.shrink(1);

                    // Generate loot
                    ItemStack loot = generateLoot(barterValue);

                    // Drop loot
                    if (!loot.isEmpty()) {
                        ItemEntity drop = new ItemEntity(this.level(), this.getX(), this.getY() + 0.5D, this.getZ(), loot);
                        this.level().addFreshEntity(drop);
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }

        return super.mobInteract(player, hand);
    }

    private ItemStack generateLoot(int barterValue) {
        // Higher barter value means better chance/tier of loot
        if (barterValue >= 27) {
            return new ItemStack(ThaumcraftItems.primordialPearl.get()); // High tier
        } else if (barterValue >= 5) {
            return new ItemStack(ThaumcraftItems.pechWand.get()); // Mid tier
        } else if (barterValue >= 3) {
            return new ItemStack(Items.GOLD_NUGGET, 5); // Example low tier
        } else {
            return new ItemStack(Items.DIRT); // Very low tier / insulting item
        }
    }
}
