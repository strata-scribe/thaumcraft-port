package thaumcraft.common.world.aura;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aura.AuraChunk;

@EventBusSubscriber(modid = Thaumcraft.MODID)
public class AuraHandler {

    // Thread-safe map of Level -> ChunkPos -> AuraChunk
    private static final Map<ResourceKey<Level>, Map<ChunkPos, AuraChunk>> AURA_CHUNKS = new ConcurrentHashMap<>();

    private static final Map<ResourceKey<Level>, Integer> TICK_COUNTERS = new ConcurrentHashMap<>();
    private static final int TICK_INTERVAL = 20;

    public static AuraChunk getAuraChunk(ResourceKey<Level> dim, ChunkPos pos) {
        Map<ChunkPos, AuraChunk> levelMap = AURA_CHUNKS.computeIfAbsent(dim, k -> new ConcurrentHashMap<>());
        return levelMap.computeIfAbsent(pos, k -> new AuraChunk((short) 100, 100.0f, 0.0f));
    }

    public static void addAuraChunk(ResourceKey<Level> dim, ChunkPos pos, AuraChunk chunk) {
        AURA_CHUNKS.computeIfAbsent(dim, k -> new ConcurrentHashMap<>()).put(pos, chunk);
    }

    public static Map<ChunkPos, AuraChunk> getAuraChunks(ResourceKey<Level> dim) {
        return AURA_CHUNKS.computeIfAbsent(dim, k -> new ConcurrentHashMap<>());
    }

    public static void clear() {
        AURA_CHUNKS.clear();
        TICK_COUNTERS.clear();
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (level.isClientSide()) return;

        ResourceKey<Level> dim = level.dimension();
        int ticks = TICK_COUNTERS.getOrDefault(dim, 0) + 1;
        TICK_COUNTERS.put(dim, ticks);

        if (ticks % TICK_INTERVAL != 0) {
            return;
        }

        performDiffusion(dim);
    }

    public static void performDiffusion(ResourceKey<Level> dim) {
        Map<ChunkPos, AuraChunk> chunks = AURA_CHUNKS.get(dim);
        if (chunks == null || chunks.isEmpty()) return;

        // Take a snapshot of the chunks to process to avoid CMEs
        Set<ChunkPos> activeChunks = new HashSet<>(chunks.keySet());

        for (ChunkPos pos : activeChunks) {
            AuraChunk chunk = chunks.get(pos);
            if (chunk == null) continue;

            List<AuraChunk> neighbors = new ArrayList<>();
            // Diffuse to neighbors
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                ChunkPos neighborPos = new ChunkPos(pos.x() + dir.getStepX(), pos.z() + dir.getStepZ());
                // Only diffuse if neighbor is loaded/exists in map
                if (chunks.containsKey(neighborPos)) {
                    neighbors.add(chunks.get(neighborPos));
                }
            }

            AuraDiffusionSimulationLogic.diffuseChunk(chunk, neighbors);
        }
    }
}
