package thaumcraft.common.tiles.devices;

/**
 * Pure Java logic helper for Hungry Chest damage mechanics.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class HungryChestDamageLogic {

    private final int biteCooldown;
    private int ticksSinceLastBite;

    public HungryChestDamageLogic(int biteCooldown) {
        this.biteCooldown = biteCooldown;
        this.ticksSinceLastBite = biteCooldown; // Ready to bite initially
    }

    public HungryChestDamageLogic() {
        this(10); // Default 10 tick cooldown
    }

    /**
     * Advances the internal tick timer for the cooldown.
     */
    public void tick() {
        if (ticksSinceLastBite < biteCooldown) {
            ticksSinceLastBite++;
        }
    }

    /**
     * Checks if the chest can bite based on the cooldown.
     *
     * @return true if the cooldown has elapsed.
     */
    public boolean canBite() {
        return ticksSinceLastBite >= biteCooldown;
    }

    /**
     * Resets the cooldown timer, typically called after a successful bite.
     */
    public void resetCooldown() {
        this.ticksSinceLastBite = 0;
    }

    /**
     * Calculates the damage dealt by the hungry chest bite.
     *
     * @return The base damage amount.
     */
    public float calculateBiteDamage() {
        return 2.0f;
    }
}
