package thaumcraft.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

import java.util.concurrent.CompletableFuture;

public class ThaumcraftBiomeTagProvider extends BiomeTagsProvider {

    public ThaumcraftBiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        TagKey<Biome> MAGICAL = TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("c", "is_magical"));
        TagKey<Biome> SPOOKY = TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("c", "is_spooky"));
        TagKey<Biome> TAINTED = TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("c", "is_tainted"));

        var magicalForest = Identifier.fromNamespaceAndPath("thaumcraft", "magical_forest");
        var taintedLand = Identifier.fromNamespaceAndPath("thaumcraft", "tainted_land");

        var magicalForestKey = net.minecraft.resources.ResourceKey.create(Registries.BIOME, magicalForest);
        var taintedLandKey = net.minecraft.resources.ResourceKey.create(Registries.BIOME, taintedLand);

        this.tag(MAGICAL).addOptional(magicalForestKey);
        this.tag(SPOOKY).addOptional(taintedLandKey);
        this.tag(TAINTED).addOptional(taintedLandKey);
    }
}
