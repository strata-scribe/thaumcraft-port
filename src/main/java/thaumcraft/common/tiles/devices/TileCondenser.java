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

public class TileCondenser extends BlockEntity implements IEssentiaTransport {

    private final CondenserLogic logic = new CondenserLogic();
    private int ticks = 0;

    // Essentia export logic
    private Aspect essentiaType = null;
    private int essentiaAmount = 0;

    public TileCondenser(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.CONDENSER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TileCondenser tile) {
        if (level.isClientSide()) return;

        tile.ticks++;
        if (tile.ticks % 5 != 0) return; // run every 5 ticks

        AuraChunk chunk = AuraHandler.getAuraChunk(level.dimension(), new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4));

        tile.logic.tick(chunk, new CondenserLogic.BlockProvider() {
            @Override
            public boolean isLattice(int dx, int dy, int dz) {
                BlockState s = level.getBlockState(pos.offset(dx, dy, dz));
                return s.getBlock() == ThaumcraftBlocks.condenserlattice.get();
            }

            @Override
            public boolean isDirtyLattice(int dx, int dz) {
                return false; // For logic purposes, handled in makeDirty
            }

            @Override
            public void makeDirty(int dx, int dy, int dz) {
                level.setBlockAndUpdate(pos.offset(dx, dy, dz), ThaumcraftBlocks.condenserlatticeDirty.get().defaultBlockState());
            }
        }, (success) -> {
            // Check if we can output essentia
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
        });

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
        output.store("cost", com.mojang.serialization.Codec.INT, logic.getCost());
        if (essentiaType != null) {
            output.store("essentiaType", com.mojang.serialization.Codec.STRING, essentiaType.getTag());
            output.store("essentiaAmount", com.mojang.serialization.Codec.INT, essentiaAmount);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        logic.setCost(input.read("cost", com.mojang.serialization.Codec.INT).orElse(0));
        String aspectTag = input.read("essentiaType", com.mojang.serialization.Codec.STRING).orElse(null);
        if (aspectTag != null) {
            essentiaType = Aspect.getAspect(aspectTag);
            essentiaAmount = input.read("essentiaAmount", com.mojang.serialization.Codec.INT).orElse(0);
        }
    }
}
