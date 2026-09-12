package thaumcraft.common.tiles.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.items.ThaumcraftItems;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;
import thaumcraft.common.entities.monster.boss.EntityFluxRift;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class VoidSiphonBlockEntity extends BlockEntity {
    @Nullable
    private Aspect filterAspect = null;

    private final VoidSiphonLogic logic = new VoidSiphonLogic();
    private final Random random = new Random();
    private int processingCycles = 0;

    public VoidSiphonBlockEntity(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.VOID_SIPHON.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, VoidSiphonBlockEntity blockEntity) {
        blockEntity.logic.tick();

        if (blockEntity.logic.shouldSiphon()) {
            AABB riftAabb = new AABB(pos).inflate(8.0);
            List<EntityFluxRift> rifts = level.getEntitiesOfClass(EntityFluxRift.class, riftAabb);

            for (EntityFluxRift rift : rifts) {
                if (!rift.isAlive()) continue;

                float currentSize = rift.getRiftSize();
                if (currentSize > 0) {
                    float decay = blockEntity.logic.calculateDecayRate(currentSize);
                    float newSize = Math.max(0, currentSize - decay);
                    rift.setRiftSize(newSize);

                    blockEntity.processingCycles++;

                    if (VoidSiphonYieldLogic.shouldGenerateSeed(rift.getStability(), blockEntity.processingCycles, blockEntity.random)) {
                        blockEntity.processingCycles = 0; // Reset cycles on successful generation
                        ItemStack seedStack = new ItemStack(ThaumcraftItems.voidSeed.get());
                        ItemEntity seedEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, seedStack);
                        level.addFreshEntity(seedEntity);
                    }

                    if (newSize <= 0) {
                        rift.discard();
                    }
                    break; // Siphon from one rift per cycle
                }
            }
        }

        // Original item processing logic
        if (level.getGameTime() % 20 == 0) {
            AABB aabb = new AABB(pos).inflate(3.0);
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, aabb);

            for (ItemEntity itemEntity : items) {
                if (!itemEntity.isAlive()) continue;

                if (blockEntity.filterAspect != null) {
                    AspectList aspects = AspectHelper.getObjectAspects(itemEntity.getItem());
                    if (aspects == null || aspects.getAmount(blockEntity.filterAspect) <= 0) {
                        continue;
                    }
                }

                itemEntity.discard();
            }
        }
    }

    public void setFilterAspect(@Nullable Aspect aspect) {
        this.filterAspect = aspect;
        setChanged();
    }

    @Nullable
    public Aspect getFilterAspect() {
        return filterAspect;
    }

    @Override
    protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
        super.saveAdditional(output);
        if (filterAspect != null) {
            output.store("FilterAspect", com.mojang.serialization.Codec.STRING, filterAspect.getTag());
        }
        output.putInt("ProcessingCycles", this.processingCycles);
    }

    @Override
    public void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input);
        java.util.Optional<String> filterAspectOpt = input.read("FilterAspect", com.mojang.serialization.Codec.STRING);
        if (filterAspectOpt.isPresent()) {
            this.filterAspect = Aspect.getAspect(filterAspectOpt.get());
        } else {
            this.filterAspect = null;
        }
        this.processingCycles = input.getIntOr("ProcessingCycles", 0);
    }
}
