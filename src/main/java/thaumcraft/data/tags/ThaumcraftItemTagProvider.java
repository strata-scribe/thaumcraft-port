package thaumcraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import thaumcraft.Thaumcraft;
import thaumcraft.api.blocks.ThaumcraftBlocks;
import thaumcraft.api.items.ThaumcraftItems;

public class ThaumcraftItemTagProvider extends ItemTagsProvider {

    public ThaumcraftItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Thaumcraft.MODID);
    }

    public ThaumcraftItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId) {
        super(output, lookupProvider, modId);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(Tags.Items.INGOTS).add(
            ThaumcraftItems.ingotThaumium.get(),
            ThaumcraftItems.ingotVoid.get(),
            ThaumcraftItems.ingotBrass.get()
        );

        tag(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/thaumium"))).add(ThaumcraftItems.ingotThaumium.get());
        tag(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/void"))).add(ThaumcraftItems.ingotVoid.get());
        tag(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/brass"))).add(ThaumcraftItems.ingotBrass.get());

        tag(Tags.Items.NUGGETS).add(
            ThaumcraftItems.nuggetThaumium.get(),
            ThaumcraftItems.nuggetVoid.get(),
            ThaumcraftItems.nuggetBrass.get(),
            ThaumcraftItems.nuggetQuartz.get()
        );

        tag(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "clusters/iron"))).add(ThaumcraftItems.clusterIron.get());
        tag(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "clusters/gold"))).add(ThaumcraftItems.clusterGold.get());
        tag(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "clusters/copper"))).add(ThaumcraftItems.clusterCopper.get());
        tag(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "clusters/tin"))).add(ThaumcraftItems.clusterTin.get());
        tag(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "clusters/silver"))).add(ThaumcraftItems.clusterSilver.get());
        tag(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "clusters/lead"))).add(ThaumcraftItems.clusterLead.get());
        tag(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "clusters/cinnabar"))).add(ThaumcraftItems.clusterCinnabar.get());
        tag(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "clusters/quartz"))).add(ThaumcraftItems.clusterQuartz.get());

        tag(ItemTags.LOGS).add(
            ThaumcraftBlocks.logGreatwood.get().asItem(),
            ThaumcraftBlocks.logSilverwood.get().asItem()
        );
        tag(ItemTags.LOGS_THAT_BURN).add(
            ThaumcraftBlocks.logGreatwood.get().asItem(),
            ThaumcraftBlocks.logSilverwood.get().asItem()
        );
        tag(ItemTags.LEAVES).add(
            ThaumcraftBlocks.leafGreatwood.get().asItem(),
            ThaumcraftBlocks.leafSilverwood.get().asItem()
        );
    }
}
