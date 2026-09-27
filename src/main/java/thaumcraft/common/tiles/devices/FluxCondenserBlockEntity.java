package thaumcraft.common.tiles.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;
import thaumcraft.common.world.aura.AuraHandler;
import thaumcraft.api.aura.AuraChunk;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.blocks.ThaumcraftBlocks;

public class FluxCondenserBlockEntity extends BlockEntity implements IEssentiaTransport {

    private FluxCondenserFiltrationLogic logic = new FluxCondenserFiltrationLogic(0, 0);
    private int ticks = 0;

    // Essentia export logic
    private Aspect essentiaType = null;
    private int essentiaAmount = 0;

    public FluxCondenserBlockEntity(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.CONDENSER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FluxCondenserBlockEntity tile) {
        if (level.isClientSide()) return;

        tile.ticks++;
        if (tile.ticks % 5 != 0) return; // run every 5 ticks

        AuraChunk chunk = AuraHandler.getAuraChunk(level, new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4));
        if (chunk == null) return;

        // Count lattices
        int cleanCount = 0;
        java.util.ArrayList<int[]> cleanLatticesPositions = new java.util.ArrayList<>();
        for (int y = 1; y <= 3; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockState s = level.getBlockState(pos.offset(x, y, z));
                    if (s.getBlock() == ThaumcraftBlocks.condenserlattice.get()) {
                        cleanCount++;
                        cleanLatticesPositions.add(new int[]{x, y, z});
                    }
                }
            }
        }

        // Use existing state to preserve vitiumResidue between ticks, but update clean count
        tile.logic.setCleanLattices(cleanCount);
        tile.logic.setCloggedLattices(0); // We only care about how many got clogged this tick

        float flux = chunk.getFlux();
        if (flux > 0.1f) {
            java.util.Random rnd = new java.util.Random(level.getRandom().nextLong());
            float extracted = tile.logic.processFlux(flux, rnd);
            if (extracted > 0) {
                chunk.setFlux(chunk.getFlux() - extracted);

                int newlyClogged = tile.logic.getCloggedLattices();
                if (newlyClogged > 0) {
                    for (int i = 0; i < newlyClogged; i++) {
                        if (!cleanLatticesPositions.isEmpty()) {
                            int idx = level.getRandom().nextInt(cleanLatticesPositions.size());
                            int[] chosen = cleanLatticesPositions.remove(idx);
                            level.setBlockAndUpdate(pos.offset(chosen[0], chosen[1], chosen[2]), ThaumcraftBlocks.condenserlatticeDirty.get().defaultBlockState());
                        }
                    }
                }

                while (tile.logic.extractVitiumAspect()) {
                    if (tile.essentiaType == null || (tile.essentiaType == Aspect.FLUX && tile.essentiaAmount < 1)) {
                        tile.essentiaType = Aspect.FLUX;
                        tile.essentiaAmount++;
                        tile.setChanged();
                    } else {
                        // Drop vitium slag
                        ItemStack slag = new ItemStack(ItemsTC.vitiumSlag);
                        ItemEntity item = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, slag);
                        level.addFreshEntity(item);
                    }
                }
            }
        }

        // Push essentia downwards
        if (tile.essentiaAmount > 0) {
            BlockEntity be = level.getBlockEntity(pos.below());
            if (be instanceof IEssentiaTransport) {
                IEssentiaTransport et = (IEssentiaTransport) be;
                if (et.canInputFrom(Direction.UP) && et.getSuctionAmount(Direction.UP) > tile.getMinimumSuction() && (et.getSuctionType(Direction.UP) == null || et.getSuctionType(Direction.UP) == Aspect.FLUX)) {
                    int added = et.addEssentia(Aspect.FLUX, tile.essentiaAmount, Direction.UP);
                    if (added > 0) {
                        tile.essentiaAmount -= added;
                        if (tile.essentiaAmount <= 0) {
                            tile.essentiaType = null;
                        }
                        tile.setChanged();
                    }
                }
            }
        }
    }

    @Override
    public boolean isConnectable(Direction face) {
        return face == Direction.DOWN;
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return false;
    }

    @Override
    public boolean canOutputTo(Direction face) {
        return face == Direction.DOWN;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
    }

    @Override
    public Aspect getSuctionType(Direction face) {
        return null;
    }

    @Override
    public int getSuctionAmount(Direction face) {
        return 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, Direction face) {
        if (face == Direction.DOWN && this.essentiaType == aspect && this.essentiaAmount > 0) {
            int toTake = Math.min(amount, this.essentiaAmount);
            this.essentiaAmount -= toTake;
            if (this.essentiaAmount <= 0) this.essentiaType = null;
            setChanged();
            return toTake;
        }
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, Direction face) {
        return 0;
    }

    @Override
    public Aspect getEssentiaType(Direction face) {
        return essentiaType;
    }

    @Override
    public int getEssentiaAmount(Direction face) {
        return essentiaAmount;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("cleanLattices", com.mojang.serialization.Codec.INT, logic.getCleanLattices());
        output.store("cloggedLattices", com.mojang.serialization.Codec.INT, logic.getCloggedLattices());
        output.store("vitiumResidue", com.mojang.serialization.Codec.FLOAT, logic.getVitiumResidue());
        if (essentiaType != null) {
            output.store("essentiaType", com.mojang.serialization.Codec.STRING, essentiaType.getTag());
            output.store("essentiaAmount", com.mojang.serialization.Codec.INT, essentiaAmount);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        int clean = input.read("cleanLattices", com.mojang.serialization.Codec.INT).orElse(0);
        int clogged = input.read("cloggedLattices", com.mojang.serialization.Codec.INT).orElse(0);
        float residue = input.read("vitiumResidue", com.mojang.serialization.Codec.FLOAT).orElse(0.0f);
        logic = new FluxCondenserFiltrationLogic(clean, clogged);
        logic.setVitiumResidue(residue);

        String aspectTag = input.read("essentiaType", com.mojang.serialization.Codec.STRING).orElse(null);
        if (aspectTag != null) {
            essentiaType = Aspect.getAspect(aspectTag);
            essentiaAmount = input.read("essentiaAmount", com.mojang.serialization.Codec.INT).orElse(0);
        }
    }
}
