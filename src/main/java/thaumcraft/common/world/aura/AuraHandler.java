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
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aura.AuraChunk;

@EventBusSubscriber(modid = Thaumcraft.MODID)
public class AuraHandler {

    private static final Map<ResourceKey<Level>, Integer> TICK_COUNTERS = new ConcurrentHashMap<>();
    private static final int TICK_INTERVAL = 20;

    public static AuraChunk getAuraChunk(Level dim, ChunkPos pos) {
        if (!(dim instanceof ServerLevel)) {
            return new AuraChunk((short) 100, 100.0f, 0.0f);
        }
        AuraSavedData data = ((ServerLevel) dim).getDataStorage().computeIfAbsent(AuraSavedData.TYPE);
        return data.getChunks().computeIfAbsent(pos, k -> {
            data.setDirty();
            return new AuraChunk((short) 100, 100.0f, 0.0f);
        });
    }

    public static void addAuraChunk(Level dim, ChunkPos pos, AuraChunk chunk) {
        if (!(dim instanceof ServerLevel)) return;
        AuraSavedData data = ((ServerLevel) dim).getDataStorage().computeIfAbsent(AuraSavedData.TYPE);
        data.getChunks().put(pos, chunk);
        data.setDirty();
    }

    public static Map<ChunkPos, AuraChunk> getAuraChunks(Level dim) {
        if (!(dim instanceof ServerLevel)) return new ConcurrentHashMap<>();
        AuraSavedData data = ((ServerLevel) dim).getDataStorage().computeIfAbsent(AuraSavedData.TYPE);
        return data.getChunks();
    }

    public static void clear() {
        TICK_COUNTERS.clear();
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (level.isClientSide() || !(level instanceof ServerLevel)) return;

        ResourceKey<Level> dim = level.dimension();
        int ticks = TICK_COUNTERS.getOrDefault(dim, 0) + 1;
        TICK_COUNTERS.put(dim, ticks);

        if (ticks % TICK_INTERVAL != 0) {
            return;
        }

        performDiffusion((ServerLevel) level);
    }

    public static void performDiffusion(ServerLevel level) {
        AuraSavedData data = level.getDataStorage().computeIfAbsent(AuraSavedData.TYPE);
        Map<ChunkPos, AuraChunk> chunks = data.getChunks();
        if (chunks == null || chunks.isEmpty()) return;

        // Take a snapshot of the chunks to process to avoid CMEs
        Set<ChunkPos> activeChunks = new HashSet<>(chunks.keySet());
        boolean changed = false;

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
            changed = true;
        }

        if (changed) {
            data.setDirty();
        }
    }
}
