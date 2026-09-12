package thaumcraft.common.tiles.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.NoteBlockEvent;
import thaumcraft.common.blocks.devices.BlockArcaneEar;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;
import thaumcraft.common.blocks.devices.logic.ArcaneEarNoteLogic;

@EventBusSubscriber
public class TileArcaneEar extends BlockEntity {
    private int note = 0;
    private NoteBlockInstrument instrument = NoteBlockInstrument.HARP;
    private int activeTicks = 0;

    public TileArcaneEar(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.ARCANE_EAR.get(), pos, state);
    }

    public void tune(Player player) {
        if (player.isShiftKeyDown()) {
            // Cycle instrument
            int nextInstrumentOrdinal = (this.instrument.ordinal() + 1) % NoteBlockInstrument.values().length;
            this.instrument = NoteBlockInstrument.values()[nextInstrumentOrdinal];
            player.sendSystemMessage(Component.literal("Instrument: " + this.instrument.name()));
        } else {
            // Cycle note
            this.note = (this.note + 1) % 25;
            player.sendSystemMessage(Component.literal("Note: " + this.note));
        }
        setChanged();
    }

    public void trigger() {
        if (this.level != null && !this.level.isClientSide()) {
            this.activeTicks = ArcaneEarNoteLogic.getPulseLengthTicks();
            this.level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(BlockArcaneEar.POWERED, true));
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TileArcaneEar be) {
        if (be.activeTicks > 0) {
            be.activeTicks--;
            if (be.activeTicks == 0) {
                level.setBlockAndUpdate(pos, state.setValue(BlockArcaneEar.POWERED, false));
            }
        }
    }

    @Override
    protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
        super.saveAdditional(output);
        output.store("Note", com.mojang.serialization.Codec.INT, this.note);
        output.store("Instrument", com.mojang.serialization.Codec.STRING, this.instrument.getSerializedName());
    }

    @Override
    protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input);
        this.note = input.read("Note", com.mojang.serialization.Codec.INT).orElse(0);
        input.read("Instrument", com.mojang.serialization.Codec.STRING).ifPresent(instString -> {
            for (NoteBlockInstrument inst : NoteBlockInstrument.values()) {
                if (inst.getSerializedName().equals(instString)) {
                    this.instrument = inst;
                    break;
                }
            }
        });
    }

    public int getNote() {
        return note;
    }

    public NoteBlockInstrument getInstrument() {
        return instrument;
    }

    @SubscribeEvent
    public static void onNoteBlockPlay(NoteBlockEvent.Play event) {
        if (event.getLevel() instanceof Level level && !level.isClientSide()) {
            // Check for arcane ears nearby (e.g. 16 block radius)
            BlockPos sourcePos = event.getPos();
            int note = event.getVanillaNoteId();
            NoteBlockInstrument instrument = event.getInstrument();

            // Note: Efficiently querying block entities might be needed if lots of them exist,
            // but for simplicity we can just iterate loaded tile entities or rely on a custom registry.
            // In Neoforge, you can get BlockEntities from chunks, but iterating all in a radius might be costly.
            // As a simple implementation for now, iterate block entities in chunk.
            // But this runs on any block entity so better to iterate blockpos.
            // Optimize search by checking chunk block entities
            int radius = 16;
            net.minecraft.world.phys.AABB area = new net.minecraft.world.phys.AABB(sourcePos).inflate(radius);
            int minX = net.minecraft.util.Mth.floor(area.minX) >> 4;
            int maxX = net.minecraft.util.Mth.floor(area.maxX) >> 4;
            int minZ = net.minecraft.util.Mth.floor(area.minZ) >> 4;
            int maxZ = net.minecraft.util.Mth.floor(area.maxZ) >> 4;

            for (int cx = minX; cx <= maxX; cx++) {
                for (int cz = minZ; cz <= maxZ; cz++) {
                    if (level.hasChunk(cx, cz)) {
                        net.minecraft.world.level.chunk.LevelChunk chunk = level.getChunk(cx, cz);
                        for (BlockEntity be : chunk.getBlockEntities().values()) {
                            if (be instanceof TileArcaneEar ear && be.getBlockPos().closerThan(sourcePos, radius)) {
                                if (ArcaneEarNoteLogic.matches(note, instrument.name(), ear.getNote(), ear.getInstrument().name())) {
                                    ear.trigger();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
