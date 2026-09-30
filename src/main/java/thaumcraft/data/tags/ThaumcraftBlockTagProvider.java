package thaumcraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import thaumcraft.Thaumcraft;
import thaumcraft.api.blocks.ThaumcraftBlocks;

public class ThaumcraftBlockTagProvider extends BlockTagsProvider {

    public ThaumcraftBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Thaumcraft.MODID);
    }

    public ThaumcraftBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId) {
        super(output, lookupProvider, modId);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(Tags.Blocks.ORES).add(
            ThaumcraftBlocks.oreAmber.get(),
            ThaumcraftBlocks.oreCinnabar.get(),
            ThaumcraftBlocks.oreQuartz.get()
        );

        tag(BlockTags.LOGS).add(
            ThaumcraftBlocks.logGreatwood.get(),
            ThaumcraftBlocks.logSilverwood.get()
        );
        tag(BlockTags.LOGS_THAT_BURN).add(
            ThaumcraftBlocks.logGreatwood.get(),
            ThaumcraftBlocks.logSilverwood.get()
        );
        tag(BlockTags.OVERWORLD_NATURAL_LOGS).add(
            ThaumcraftBlocks.logGreatwood.get(),
            ThaumcraftBlocks.logSilverwood.get()
        );

        tag(BlockTags.LEAVES).add(
            ThaumcraftBlocks.leafGreatwood.get(),
            ThaumcraftBlocks.leafSilverwood.get()
        );
    }
}
