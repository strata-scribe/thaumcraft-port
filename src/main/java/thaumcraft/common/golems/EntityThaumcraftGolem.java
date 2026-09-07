package thaumcraft.common.golems;

import net.minecraft.core.NonNullList;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.IGolemProperties;
import java.util.Set;
import java.util.HashSet;

public class EntityThaumcraftGolem extends PathfinderMob implements IGolemAPI {

    private static final EntityDataAccessor<Long> PROPERTIES_ID = SynchedEntityData.defineId(EntityThaumcraftGolem.class, EntityDataSerializers.LONG);
    private GolemProperties properties = new GolemProperties();

    public EntityThaumcraftGolem(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.ARMOR, 0.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PROPERTIES_ID, 0L);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
        if (PROPERTIES_ID.equals(pKey)) {
            // Re-decode properties if this wasn't called locally
            long propsLong = this.entityData.get(PROPERTIES_ID);
            this.properties = GolemProperties.fromLong(propsLong);
            updateAttributesFromProperties();
        }
    }

    public static class CalculatedAttributes {
        public double maxHealth = 10.0D;
        public double armor = 0.0D;
        public double speed = 0.3D;
        public double damage = 1.0D;
        public double knockbackRes = 0.0D;
    }

    // Extracted for unit testing
    public static CalculatedAttributes calculateAttributes(GolemProperties properties, String[] traitNames) {
        CalculatedAttributes attr = new CalculatedAttributes();

        if (properties.getMaterial() != null) {
            attr.maxHealth += properties.getMaterial().healthMod;
            attr.armor += properties.getMaterial().armor;
            attr.damage += properties.getMaterial().damage;
        }

        Set<String> traits = new HashSet<>();
        if (traitNames != null) {
            for (String t : traitNames) traits.add(t);
        } else {
            for (EnumGolemTrait t : properties.getTraits()) {
                traits.add(t.name().toLowerCase());
            }
        }

        if (traits.contains("light")) {
            attr.speed *= 1.2;
        }
        if (traits.contains("fragile")) {
            attr.maxHealth *= 0.75;
        }
        if (traits.contains("heavy")) {
            attr.speed *= 0.8;
            attr.knockbackRes += 0.5;
        }
        if (traits.contains("armored")) {
            attr.armor += 4;
        }
        if (traits.contains("brutal")) {
            attr.damage += 2;
        }

        return attr;
    }

    public void updateAttributesFromProperties() {
        CalculatedAttributes calc = calculateAttributes(properties, null);

        if (this.getAttribute(Attributes.MAX_HEALTH) != null) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(calc.maxHealth);
        }
        if (this.getAttribute(Attributes.ARMOR) != null) {
            this.getAttribute(Attributes.ARMOR).setBaseValue(calc.armor);
        }
        if (this.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(calc.speed);
        }
        if (this.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(calc.damage);
        }
        if (this.getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null) {
            this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(calc.knockbackRes);
        }

        if (this.getHealth() > this.getMaxHealth()) {
            this.setHealth(this.getMaxHealth());
        }
    }

    @Override
    public LivingEntity getGolemEntity() {
        return this;
    }

    @Override
    public IGolemProperties getProperties() {
        return properties;
    }

    @Override
    public void setProperties(IGolemProperties prop) {
        if (prop instanceof GolemProperties) {
            this.properties = (GolemProperties) prop;
            this.entityData.set(PROPERTIES_ID, this.properties.toLong());
            updateAttributesFromProperties();
        }
    }

    @Override
    public Level getGolemWorld() {
        return this.level();
    }

    @Override
    public ItemStack holdItem(ItemStack stack) {
        return stack; // Stub
    }

    @Override
    public ItemStack dropItem(ItemStack stack) {
        return stack; // Stub
    }

    @Override
    public boolean canCarry(ItemStack stack, boolean partial) {
        return false; // Stub
    }

    @Override
    public int canCarryAmount(ItemStack stack) {
        return 0; // Stub
    }

    @Override
    public boolean isCarrying(ItemStack stack) {
        return false; // Stub
    }

    @Override
    public NonNullList<ItemStack> getCarrying() {
        return NonNullList.create(); // Stub
    }

    @Override
    public void addRankXp(int xp) {
        // Stub
    }

    @Override
    public byte getGolemColor() {
        return 0; // Stub
    }

    @Override
    public void swingArm() {
        // Stub
    }

    @Override
    public boolean isInCombat() {
        return false; // Stub
    }
}
