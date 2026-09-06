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
    public static final int CAPACITY = 250;

    // -------------------------------------------------------------------------
    // State fields
    // -------------------------------------------------------------------------

    /** The aspect currently stored in this jar, or {@code null} when empty. */
    private Aspect aspect = null;

    /** The amount of essentia stored (0–{@value #CAPACITY}). */
    private int amount = 0;

    /**
     * Optional aspect filter.  When set, only this aspect is accepted.
     * When {@code null} any single aspect can fill the jar.
     */
    private Aspect aspectFilter = null;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public JarBlockEntity(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.JAR.get(), pos, state);
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
        AspectList list = new AspectList();
        if (aspect != null && amount > 0) {
            list.add(aspect, amount);
        }
        return list;
    }

    @Override
    public void setAspects(AspectList aspects) {
        if (aspects != null && aspects.size() > 0) {
            aspect = aspects.getAspectsSortedByAmount()[0];
            amount = aspects.getAmount(aspect);
        } else {
            aspect = null;
            amount = 0;
        }
        setChanged();
    }

    /**
     * Returns {@code true} if the given aspect can be added.
     * Accepts any aspect when the jar has no filter, or exactly the filter aspect.
     */
    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return aspectFilter == null || tag == aspectFilter;
    }

    /**
     * Adds {@code am} units of aspect {@code tag} to this jar.
     *
     * @return leftover that could not be added (0 on full success).
     */
    @Override
    public int addToContainer(Aspect tag, int am) {
        if (am == 0) return 0;
        // Must be the same aspect already in the jar (or the jar must be empty)
        if (aspect != null && tag != aspect) return am;
        if (!doesContainerAccept(tag)) return am;

        aspect = tag;
        int canAdd = Math.min(am, CAPACITY - amount);
        amount += canAdd;
        setChanged();
        return am - canAdd;
    }

    /**
     * Removes {@code am} units of aspect {@code tag} from this jar.
     *
     * @return {@code true} if the requested amount was available and removed.
     */
    @Override
    public boolean takeFromContainer(Aspect tag, int am) {
        if (tag != aspect || amount < am) return false;
        amount -= am;
        if (amount <= 0) {
            amount = 0;
            aspect = null;
        }
        setChanged();
        return true;
    }

    /** @deprecated Jars store only one aspect; bulk removal is not supported. */
    @Deprecated
    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amt) {
        return tag == aspect && amount >= amt;
    }

    @Deprecated
    @Override
    public boolean doesContainerContain(AspectList ot) {
        for (Aspect tag : ot.getAspects()) {
            if (tag == aspect && amount > 0) return true;
        }
        return false;
    }

    @Override
    public int containerContains(Aspect tag) {
        return tag == aspect ? amount : 0;
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /** @return the aspect currently stored, or {@code null} when the jar is empty. */
    public Aspect getStoredAspect() {
        return aspect;
    }

    /** Directly sets the stored aspect (e.g., from labelling interaction). */
    public void setStoredAspect(Aspect aspect) {
        this.aspect = aspect;
        setChanged();
    }

    /** @return current stored amount (0–{@value #CAPACITY}). */
    public int getAmount() {
        return amount;
    }

    /** @return the aspect filter, or {@code null} if none is set. */
    public Aspect getAspectFilter() {
        return aspectFilter;
    }

    /** Sets (or clears) the aspect filter. */
    public void setAspectFilter(Aspect filter) {
        this.aspectFilter = filter;
        setChanged();
    }

    // -------------------------------------------------------------------------
    // NBT Serialisation — MC 1.21.4 ValueOutput / ValueInput
    // -------------------------------------------------------------------------

    @Override
    protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
        super.saveAdditional(output);

        // Store Aspect tag strings; omit keys entirely when null/empty
        if (aspect != null) {
            output.store("Aspect", com.mojang.serialization.Codec.STRING, aspect.getTag());
        }
        if (aspectFilter != null) {
            output.store("AspectFilter", com.mojang.serialization.Codec.STRING, aspectFilter.getTag());
        }
        output.store("Amount", com.mojang.serialization.Codec.INT, amount);
    }

    @Override
    protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input);

        aspect = input.read("Aspect", com.mojang.serialization.Codec.STRING)
                .map(Aspect::getAspect)
                .orElse(null);
        aspectFilter = input.read("AspectFilter", com.mojang.serialization.Codec.STRING)
                .map(Aspect::getAspect)
                .orElse(null);
        amount = input.read("Amount", com.mojang.serialization.Codec.INT).orElse(0);
        // Guard: clamp amount to valid range
        amount = Math.max(0, Math.min(CAPACITY, amount));
        if (amount == 0) aspect = null;
    }
}
