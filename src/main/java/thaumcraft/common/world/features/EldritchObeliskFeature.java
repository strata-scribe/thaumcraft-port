package thaumcraft.common.world.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thaumcraft.api.blocks.ThaumcraftBlocks;

public class EldritchObeliskFeature extends Feature<NoneFeatureConfiguration> {

    public EldritchObeliskFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    public EldritchObeliskFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        int platformRadius = 3; // 7x7 platform
        int rand3 = random.nextInt(3);
        int spireHeight = WorldgenTreeLogic.calculateObeliskSpireHeight(rand3); // 5..7 blocks

        if (origin.getY() <= level.getMinY() || origin.getY() + spireHeight + 2 >= level.getMaxY()) {
            return false;
        }

        // Soil / ground check
        BlockPos below = origin.below();
        if (level.isEmptyBlock(below)) {
            return false;
        }

        // 1. Generate 7x7 Dais Platform with glyphed stone ring
        for (int dx = -platformRadius; dx <= platformRadius; dx++) {
            for (int dz = -platformRadius; dz <= platformRadius; dz++) {
                if (WorldgenTreeLogic.isObeliskPlatformBlock(dx, dz, platformRadius)) {
                    BlockPos platPos = origin.offset(dx, 0, dz);
                    boolean isGlyphRing = (Math.abs(dx) == 2 && Math.abs(dz) <= 2)
                            || (Math.abs(dz) == 2 && Math.abs(dx) <= 2);

                    BlockState platState = isGlyphRing
                            ? ThaumcraftBlocks.stoneAncientGlyphed.get().defaultBlockState()
                            : ThaumcraftBlocks.stoneAncientTile.get().defaultBlockState();

                    level.setBlock(platPos, platState, 2);
                }
            }
        }

        // 2. Place 4 Corner Pedestals at (±3, ±3)
        for (int dx = -platformRadius; dx <= platformRadius; dx++) {
            for (int dz = -platformRadius; dz <= platformRadius; dz++) {
                if (WorldgenTreeLogic.isObeliskPedestal(dx, dz, platformRadius)) {
                    BlockPos pedPos = origin.offset(dx, 1, dz);
                    level.setBlock(pedPos, ThaumcraftBlocks.pedestalAncient.get().defaultBlockState(), 2);
                }
            }
        }

        // 3. Generate Central Monolithic Spire (Eldritch Stone)
        for (int y = 1; y <= spireHeight; y++) {
            BlockPos spirePos = origin.offset(0, y, 0);
            level.setBlock(spirePos, ThaumcraftBlocks.eldritch.get().defaultBlockState(), 2);
        }

        // 4. Place Apex Ancient Stone Capstone
        BlockPos capstonePos = origin.offset(0, spireHeight + 1, 0);
        level.setBlock(capstonePos, ThaumcraftBlocks.stoneAncient.get().defaultBlockState(), 2);

        return true;
    }
}
