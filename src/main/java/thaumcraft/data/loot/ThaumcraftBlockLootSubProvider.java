package thaumcraft.data.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import java.util.Set;
import java.util.stream.Collectors;
import thaumcraft.api.blocks.ThaumcraftBlocks;
import thaumcraft.api.items.ThaumcraftItems;

public class ThaumcraftBlockLootSubProvider extends BlockLootSubProvider {
    public ThaumcraftBlockLootSubProvider(Set<Item> explosionResistant, FeatureFlagSet enabledFeatures, HolderLookup.Provider registries) {
        super(explosionResistant, enabledFeatures, registries);
    }

    @Override
    protected void generate() {
        this.dropSelf(ThaumcraftBlocks.stoneArcane.get());
        this.dropSelf(ThaumcraftBlocks.stoneArcaneBrick.get());

        // Crystal ores drop crystal essence when broken with fortune
        this.add(ThaumcraftBlocks.crystalAir.get(), (block) -> this.createOreDrop(block, ThaumcraftItems.crystalEssence.get()));
        this.add(ThaumcraftBlocks.crystalFire.get(), (block) -> this.createOreDrop(block, ThaumcraftItems.crystalEssence.get()));
        this.add(ThaumcraftBlocks.crystalWater.get(), (block) -> this.createOreDrop(block, ThaumcraftItems.crystalEssence.get()));
        this.add(ThaumcraftBlocks.crystalEarth.get(), (block) -> this.createOreDrop(block, ThaumcraftItems.crystalEssence.get()));
        this.add(ThaumcraftBlocks.crystalOrder.get(), (block) -> this.createOreDrop(block, ThaumcraftItems.crystalEssence.get()));
        this.add(ThaumcraftBlocks.crystalEntropy.get(), (block) -> this.createOreDrop(block, ThaumcraftItems.crystalEssence.get()));
        this.add(ThaumcraftBlocks.crystalTaint.get(), (block) -> this.createOreDrop(block, ThaumcraftItems.crystalEssence.get()));

        this.add(ThaumcraftBlocks.oreAmber.get(), (block) -> this.createOreDrop(block, ThaumcraftItems.amber.get()));
        this.add(ThaumcraftBlocks.oreCinnabar.get(), (block) -> this.createOreDrop(block, ThaumcraftItems.quicksilver.get()));
        this.add(ThaumcraftBlocks.oreQuartz.get(), (block) -> this.createOreDrop(block, net.minecraft.world.item.Items.QUARTZ));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ThaumcraftBlocks.BLOCKS.getEntries().stream()
            .map(holder -> (Block) holder.get())
            .collect(Collectors.toList());
    }
}
