package thaumcraft.data.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import thaumcraft.common.entities.ThaumcraftEntities;
import thaumcraft.api.items.ThaumcraftItems;
import java.util.stream.Stream;

public class ThaumcraftEntityLootSubProvider extends EntityLootSubProvider {
    public ThaumcraftEntityLootSubProvider(HolderLookup.Provider registries) {
        super(FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    public void generate() {
        this.add(ThaumcraftEntities.PECH.get(), LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(ThaumcraftItems.lootBag.get()))
                .when(net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition.randomChance(0.2F))
            )
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(1.0F, 3.0F))
                .add(LootItem.lootTableItem(ThaumcraftItems.nuggets.get()))
            )
        );

        this.add(ThaumcraftEntities.TAINTACLE.get(), LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(ThaumcraftItems.bottleTaint.get()))
                .when(net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition.randomChance(0.5F))
            )
        );

        this.add(ThaumcraftEntities.TAINT_CRAWLER.get(), LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(ThaumcraftItems.vitiumSlag.get()))
                .when(net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition.randomChance(0.25F))
            )
        );

        this.add(ThaumcraftEntities.ELDRITCH_CRAB.get(), LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(0.0F, 1.0F))
                .add(LootItem.lootTableItem(Items.ENDER_PEARL))
            )
        );

        this.add(ThaumcraftEntities.INHABITED_ZOMBIE.get(), LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(0.0F, 2.0F))
                .add(LootItem.lootTableItem(Items.ROTTEN_FLESH))
            )
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(ThaumcraftItems.eldritchEye.get()))
                .when(net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition.randomChance(0.1F))
            )
        );

        this.add(ThaumcraftEntities.TAINT_SEED.get(), LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(UniformGenerator.between(1.0F, 2.0F))
                .add(LootItem.lootTableItem(ThaumcraftItems.vitiumSlag.get()))
            )
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(ThaumcraftItems.voidSeed.get()))
                .when(net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition.randomChance(0.3F))
            )
        );
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return Stream.of(
            ThaumcraftEntities.PECH.get(),
            ThaumcraftEntities.TAINTACLE.get(),
            ThaumcraftEntities.TAINT_CRAWLER.get(),
            ThaumcraftEntities.ELDRITCH_CRAB.get(),
            ThaumcraftEntities.INHABITED_ZOMBIE.get(),
            ThaumcraftEntities.TAINT_SEED.get()
        );
    }
}
