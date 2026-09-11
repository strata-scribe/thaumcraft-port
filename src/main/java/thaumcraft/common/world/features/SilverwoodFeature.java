package thaumcraft.common.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thaumcraft.api.blocks.ThaumcraftBlocks;

public class SilverwoodFeature extends Feature<NoneFeatureConfiguration> {

    public SilverwoodFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    public SilverwoodFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        // Calculate Silverwood height using decoupled logic: 7 + rand4 (7..10)
        int rand4 = random.nextInt(4);
        int height = WorldgenTreeLogic.calculateSilverwoodHeight(rand4);

        if (origin.getY() <= level.getMinY() || origin.getY() + height + 4 >= level.getMaxY()) {
            return false;
        }

        // Soil check at trunk base
        BlockPos below = origin.below();
        if (level.isEmptyBlock(below)) {
            return false;
        }

        // Generate cross-shaped trunk
        for (int y = 0; y < height; y++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (WorldgenTreeLogic.isSilverwoodTrunkBlock(dx, dz)) {
                        BlockPos trunkPos = origin.offset(dx, y, dz);
                        level.setBlock(trunkPos, ThaumcraftBlocks.logSilverwood.get().defaultBlockState(), 2);
                    }
                }
            }
        }

        // Generate 4 diagonal root buttresses at ground level
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (WorldgenTreeLogic.isSilverwoodButtressBlock(dx, dz)) {
                    BlockPos buttressPos = origin.offset(dx, 0, dz);
                    level.setBlock(buttressPos, ThaumcraftBlocks.logSilverwood.get().defaultBlockState(), 2);
                }
            }
        }

        // Generate spherical foliage canopy centered at tree apex
        int radiusBonus = random.nextInt(4);
        for (int dy = -3; dy <= 3; dy++) {
            for (int dx = -3; dx <= 3; dx++) {
                for (int dz = -3; dz <= 3; dz++) {
                    if (WorldgenTreeLogic.isSilverwoodCanopyBlock(dx, dy, dz, radiusBonus)) {
                        BlockPos leafPos = origin.offset(dx, height + dy, dz);
                        if (level.isEmptyBlock(leafPos)) {
                            level.setBlock(leafPos, ThaumcraftBlocks.leafSilverwood.get().defaultBlockState(), 2);
                        }
                    }
                }
            }
        }

        // Generate Shimmerleaf flowers around trunk base
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (Math.abs(dx) == 2 || Math.abs(dz) == 2) {
                    if (random.nextInt(3) == 0) {
                        BlockPos flowerPos = origin.offset(dx, 0, dz);
                        if (level.isEmptyBlock(flowerPos) && !level.isEmptyBlock(flowerPos.below())) {
                            level.setBlock(flowerPos, ThaumcraftBlocks.shimmerleaf.get().defaultBlockState(), 2);
                        }
                    }
                }
            }
        }

        return true;
    }
}
