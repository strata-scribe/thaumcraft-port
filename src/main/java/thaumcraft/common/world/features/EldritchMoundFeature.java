package thaumcraft.common.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import thaumcraft.Thaumcraft;
import thaumcraft.api.blocks.ThaumcraftBlocks;
import net.minecraft.core.registries.BuiltInRegistries;

public class EldritchMoundFeature extends Feature<NoneFeatureConfiguration> {

    public EldritchMoundFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    public EldritchMoundFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        int rand3 = random.nextInt(3);
        int moundRadius = EldritchStructureLogic.calculateMoundRadius(rand3);

        if (origin.getY() - moundRadius <= level.getMinY() || origin.getY() + moundRadius >= level.getMaxY()) {
            return false;
        }

        // Generate spherical subterranean chamber
        for (int dx = -moundRadius; dx <= moundRadius; dx++) {
            for (int dy = -moundRadius; dy <= moundRadius; dy++) {
                for (int dz = -moundRadius; dz <= moundRadius; dz++) {
                    if (EldritchStructureLogic.isInsideMoundChamber(dx, dy, dz, moundRadius)) {
                        BlockPos pos = origin.offset(dx, dy, dz);

                        if (EldritchStructureLogic.isMoundLootChest(dx, dy, dz)) {
                            // Place loot chest
                            level.setBlock(pos, Blocks.CHEST.defaultBlockState(), 2);
                            BlockEntity be = level.getBlockEntity(pos);
                            if (be instanceof ChestBlockEntity chest) {
                                // Realistically we'd set a loot table here
                                // chest.setLootTable(ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(Thaumcraft.MODID, "chests/eldritch_mound")), random.nextLong());
                            }
                        } else if (EldritchStructureLogic.isMoundSpawner(dx, dy, dz)) {
                            // Place spawner
                            level.setBlock(pos, Blocks.SPAWNER.defaultBlockState(), 2);
                            BlockEntity be = level.getBlockEntity(pos);
                            if (be instanceof SpawnerBlockEntity spawner) {
                                BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath(Thaumcraft.MODID, "eldritch_guardian"))
                                    .ifPresent(entityType -> spawner.setEntityId(entityType, random));
                            }
                        } else {
                            // Shell and interior
                            if (EldritchStructureLogic.isInsideMoundChamber(dx, dy, dz, moundRadius - 1)) {
                                if (!EldritchStructureLogic.isMoundLootChest(dx, dy, dz) && !EldritchStructureLogic.isMoundSpawner(dx, dy, dz) && dy >= 0) { // Hollow above floor
                                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                                } else if (dy < 0) { // Solid floor
                                    if (!level.getBlockState(pos).is(ThaumcraftBlocks.stoneAncient.get())) {
                                        level.setBlock(pos, ThaumcraftBlocks.stoneAncient.get().defaultBlockState(), 2);
                                    }
                                }
                            } else {
                                // Outer shell
                                level.setBlock(pos, ThaumcraftBlocks.stoneAncient.get().defaultBlockState(), 2);
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
