package thaumcraft.common.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thaumcraft.api.blocks.ThaumcraftBlocks;

public class GreatwoodFeature extends Feature<NoneFeatureConfiguration> {

    public GreatwoodFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    public GreatwoodFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        // Calculate trunk height using decoupled logic: 11 + rand11 (11..21)
        int rand11 = random.nextInt(11);
        int height = WorldgenTreeLogic.calculateGreatwoodHeight(rand11);

        if (origin.getY() <= level.getMinY() || origin.getY() + height + 3 >= level.getMaxY()) {
            return false;
        }

        // Soil check at trunk base
        BlockPos below = origin.below();
        if (level.isEmptyBlock(below)) {
            return false;
        }

        // Generate 2x2 trunk
        for (int y = 0; y < height; y++) {
            for (int dx = 0; dx < 2; dx++) {
                for (int dz = 0; dz < 2; dz++) {
                    BlockPos trunkPos = origin.offset(dx, y, dz);
                    level.setBlock(trunkPos, ThaumcraftBlocks.logGreatwood.get().defaultBlockState(), 2);
                }
            }
        }

        // Generate dual canopy passes (base canopy 1.2x and upper canopy 1.66x)
        float baseRadius = WorldgenTreeLogic.calculateBaseCanopyRadius(2.5f);
        float upperRadius = WorldgenTreeLogic.calculateUpperCanopyRadius(2.0f);

        for (int y = height - 5; y <= height + 2; y++) {
            boolean isUpper = (y >= height);
            for (int dx = -4; dx <= 5; dx++) {
                for (int dz = -4; dz <= 5; dz++) {
                    if (WorldgenTreeLogic.isInsideDualCanopy(dx, dz, baseRadius, upperRadius, isUpper)) {
                        BlockPos leafPos = origin.offset(dx, y, dz);
                        if (level.isEmptyBlock(leafPos)) {
                            level.setBlock(leafPos, ThaumcraftBlocks.leafGreatwood.get().defaultBlockState(), 2);
                        }
                    }
                }
            }
        }

        // 1-in-8 chance to generate a subterranean cave spider spawner nest
        int roll8 = random.nextInt(8);
        if (WorldgenTreeLogic.shouldSpawnSpiderDungeon(roll8)) {
            BlockPos dungeonCenter = origin.below(2);
            level.setBlock(dungeonCenter, Blocks.SPAWNER.defaultBlockState(), 2);
            level.setBlock(dungeonCenter.offset(1, 0, 0), Blocks.CHEST.defaultBlockState(), 2);
            level.setBlock(dungeonCenter.offset(0, 0, 1), Blocks.COBWEB.defaultBlockState(), 2);
            level.setBlock(dungeonCenter.offset(-1, 0, 0), Blocks.COBWEB.defaultBlockState(), 2);
            level.setBlock(dungeonCenter.offset(0, 0, -1), Blocks.COBWEB.defaultBlockState(), 2);
        }

        return true;
    }
}
