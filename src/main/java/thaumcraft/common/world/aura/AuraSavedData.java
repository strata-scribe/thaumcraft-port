package thaumcraft.common.world.aura;

import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.datafix.DataFixTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aura.AuraChunk;
import net.minecraft.world.level.ChunkPos;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.HolderLookup;

public class AuraSavedData extends SavedData {
    private final Map<ChunkPos, AuraChunk> chunks = new ConcurrentHashMap<>();

    // Codec for AuraChunk
    public static final Codec<AuraChunk> AURA_CHUNK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.SHORT.fieldOf("base").forGetter(AuraChunk::getBase),
        Codec.FLOAT.fieldOf("vis").forGetter(AuraChunk::getVis),
        Codec.FLOAT.fieldOf("flux").forGetter(AuraChunk::getFlux),
        Codec.FLOAT.fieldOf("corruption").forGetter(AuraChunk::getCorruption)
    ).apply(instance, (base, vis, flux, corruption) -> {
        AuraChunk chunk = new AuraChunk(base, vis, flux);
        chunk.setCorruption(corruption);
        return chunk;
    }));

    // Codec for Map<ChunkPos, AuraChunk>
    public static final Codec<Map<ChunkPos, AuraChunk>> MAP_CODEC = Codec.unboundedMap(
        Codec.STRING.xmap(
            s -> {
                String[] parts = s.split(",");
                if (parts.length != 2) return new ChunkPos(0, 0);
                try {
                    return new ChunkPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                } catch (NumberFormatException e) {
                    return new ChunkPos(0, 0);
                }
            },
            pos -> pos.x() + "," + pos.z()
        ),
        AURA_CHUNK_CODEC
    );

    public static final Codec<AuraSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        MAP_CODEC.fieldOf("chunks").forGetter(d -> d.chunks)
    ).apply(instance, map -> {
        AuraSavedData data = new AuraSavedData();
        data.chunks.putAll(map);
        return data;
    }));

    public static final SavedDataType<AuraSavedData> TYPE = new SavedDataType<>(
        Identifier.fromNamespaceAndPath(Thaumcraft.MODID, "aura_chunks"),
        AuraSavedData::new,
        CODEC,
        DataFixTypes.SAVED_DATA_COMMAND_STORAGE
    );

    public AuraSavedData() {}

    public Map<ChunkPos, AuraChunk> getChunks() {
        return chunks;
    }
}
