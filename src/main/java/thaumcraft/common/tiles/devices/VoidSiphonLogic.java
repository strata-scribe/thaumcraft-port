package thaumcraft.common.tiles.devices;

public class VoidSiphonLogic {
    private int ticks = 0;
    private final int cycleTime;

    public VoidSiphonLogic() {
        this.cycleTime = 20; // Example: 1 second per cycle
    }

    public VoidSiphonLogic(int cycleTime) {
        this.cycleTime = cycleTime;
    }

    public void tick() {
        ticks++;
    }

    public boolean shouldSiphon() {
        if (ticks >= cycleTime) {
            ticks = 0;
            return true;
        }
        return false;
    }

    public float calculateDecayRate(float riftSize) {
        // Example logic: shrink rift by a tiny amount, maybe scaled by size
        return Math.max(0.01f, riftSize * 0.05f);
    }

    public boolean shouldGenerateSeed(java.util.Random random, float riftSize) {
        // Base chance to generate seed. Larger rifts have better odds
        // e.g., 10% base + up to 40% from size
        float chance = 0.10f + Math.min(0.40f, riftSize / 100.0f);
        return random.nextFloat() < chance;
    }
}
