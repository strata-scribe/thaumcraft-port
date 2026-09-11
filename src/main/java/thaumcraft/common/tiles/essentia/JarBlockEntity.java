package thaumcraft.common.tiles.essentia;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;

/**
 * Block entity for the Warded Jar — stores a single type of essentia.
 *
 * <h3>Storage rules</h3>
 * <ul>
 *   <li>Holds at most {@value #CAPACITY} units of a single {@link Aspect}.</li>
 *   <li>If {@link #aspectFilter} is set, only that aspect is accepted.</li>
 *   <li>If the jar is empty ({@code amount == 0}), any accepted aspect can fill it.</li>
 * </ul>
 *
 * <h3>Comparator signal</h3>
 * {@code (amount * 15) / CAPACITY} — computed by {@link thaumcraft.common.blocks.essentia.BlockJar}.
 *
 * <p>MC 1.21.4 / NeoForge 26.2 port of
 * {@code thaumcraft.common.tiles.essentia.TileJarFillable}.
 */
public class JarBlockEntity extends BlockEntity implements IAspectContainer {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    /** Maximum essentia this jar can hold. */
    public static final int CAPACITY = JarLogic.CAPACITY;

    // -------------------------------------------------------------------------
    // State fields
    // -------------------------------------------------------------------------

    public final JarLogic logic;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public JarBlockEntity(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.JAR.get(), pos, state);
        boolean isVoid = state.getBlock() == thaumcraft.api.blocks.ThaumcraftBlocks.jarVoid.get();
        this.logic = new JarLogic(isVoid);
    }

    // -------------------------------------------------------------------------
    // IAspectContainer — query
    // -------------------------------------------------------------------------

    /**
     * Returns the stored essentia as an {@link AspectList}.
     * Returns an empty list when the jar is empty.
     */
    @Override
    public AspectList getAspects() {
        return logic.getAspects();
    }

    @Override
    public void setAspects(AspectList aspects) {
        logic.setAspects(aspects);
        setChanged();
    }

    /**
     * Returns {@code true} if the given aspect can be added.
     * Accepts any aspect when the jar has no filter, or exactly the filter aspect.
     */
    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return logic.doesContainerAccept(tag);
    }

    /**
     * Adds {@code am} units of aspect {@code tag} to this jar.
     *
     * @return leftover that could not be added (0 on full success).
     */
    @Override
    public int addToContainer(Aspect tag, int am) {
        int leftover = logic.addToContainer(tag, am);
        if (leftover != am) setChanged();
        return leftover;
    }

    /**
     * Removes {@code am} units of aspect {@code tag} from this jar.
     *
     * @return {@code true} if the requested amount was available and removed.
     */
    @Override
    public boolean takeFromContainer(Aspect tag, int am) {
        boolean success = logic.takeFromContainer(tag, am);
        if (success) setChanged();
        return success;
    }

    /** @deprecated Jars store only one aspect; bulk removal is not supported. */
    @Deprecated
    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amt) {
        return logic.doesContainerContainAmount(tag, amt);
    }

    @Deprecated
    @Override
    public boolean doesContainerContain(AspectList ot) {
        for (Aspect tag : ot.getAspects()) {
            if (logic.containerContains(tag) > 0) return true;
        }
        return false;
    }

    @Override
    public int containerContains(Aspect tag) {
        return logic.containerContains(tag);
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /** @return the aspect currently stored, or {@code null} when the jar is empty. */
    public Aspect getStoredAspect() {
        return logic.getAspect();
    }

    /** Directly sets the stored aspect (e.g., from labelling interaction). */
    public void setStoredAspect(Aspect aspect) {
        logic.setAspect(aspect);
        setChanged();
    }

    /** @return current stored amount (0–{@value #CAPACITY}). */
    public int getAmount() {
        return logic.getAmount();
    }

    /** @return the aspect filter, or {@code null} if none is set. */
    public Aspect getAspectFilter() {
        return logic.getAspectFilter();
    }

    /** Sets (or clears) the aspect filter. */
    public void setAspectFilter(Aspect filter) {
        logic.setAspectFilter(filter);
        setChanged();
    }

    // -------------------------------------------------------------------------
    // NBT Serialisation — MC 1.21.4 ValueOutput / ValueInput
    // -------------------------------------------------------------------------

    @Override
    protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
        super.saveAdditional(output);

        // Store Aspect tag strings; omit keys entirely when null/empty
        if (logic.getAspect() != null) {
            output.store("Aspect", com.mojang.serialization.Codec.STRING, logic.getAspect().getTag());
        }
        if (logic.getAspectFilter() != null) {
            output.store("AspectFilter", com.mojang.serialization.Codec.STRING, logic.getAspectFilter().getTag());
        }
        output.store("Amount", com.mojang.serialization.Codec.INT, logic.getAmount());
    }

    @Override
    protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input);

        Aspect aspect = input.read("Aspect", com.mojang.serialization.Codec.STRING)
                .map(Aspect::getAspect)
                .orElse(null);
        Aspect aspectFilter = input.read("AspectFilter", com.mojang.serialization.Codec.STRING)
                .map(Aspect::getAspect)
                .orElse(null);
        int amount = input.read("Amount", com.mojang.serialization.Codec.INT).orElse(0);

        logic.setAspect(aspect);
        logic.setAspectFilter(aspectFilter);
        logic.setAmount(amount);

        if (logic.getAmount() == 0) logic.setAspect(null);
    }
}
