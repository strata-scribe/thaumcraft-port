package thaumcraft.common.blocks.essentia;

/**
 * Enumeration of Essentia Smelter tiers.
 *
 * <p>Each tier determines the smelter's essentia buffer capacity,
 * the speed at which essentia is pushed to alembics, and the
 * efficiency of item decomposition (lower flux waste at higher tiers).
 *
 * <p>MC 1.21.4 / NeoForge 26.2 port of the implicit tier system from
 * {@code TileSmelter.getType()} / {@code getEfficiency()} / {@code getSpeed()}.
 */
public enum SmelterTier {

    /**
     * Basic alchemical furnace.
     * Capacity 256, push speed 15 ticks, 80% efficiency (20% flux).
     */
    BASIC(256, 15, 0.8f),

    /**
     * Thaumium smeltery — faster push, higher efficiency.
     * Capacity 384, push speed 10 ticks, 90% efficiency (10% flux).
     */
    THAUMIUM(384, 10, 0.9f),

    /**
     * Void smeltery — largest buffer, best efficiency.
     * Capacity 512, push speed 15 ticks, 95% efficiency (5% flux).
     */
    VOID(512, 15, 0.95f);

    private final int capacity;
    private final int pushSpeed;
    private final float efficiency;

    SmelterTier(int capacity, int pushSpeed, float efficiency) {
        this.capacity = capacity;
        this.pushSpeed = pushSpeed;
        this.efficiency = efficiency;
    }

    /** Maximum essentia the smelter's internal buffer can hold. */
    public int getCapacity() { return capacity; }

    /**
     * Base interval (in ticks) at which essentia is pushed to alembics.
     * Alumentum speed boost multiplies this by 0.8.
     */
    public int getPushSpeed() { return pushSpeed; }

    /**
     * Efficiency factor (0–1).  For each point of aspect during smelting,
     * a random roll > efficiency destroys that point and produces flux.
     * {@link thaumcraft.api.aspects.Aspect#FLUX FLUX} aspect uses
     * {@code efficiency * 0.66} as its threshold.
     */
    public float getEfficiency() { return efficiency; }
}
