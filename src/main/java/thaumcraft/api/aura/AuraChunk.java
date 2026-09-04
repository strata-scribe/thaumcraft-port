package thaumcraft.api.aura;

public class AuraChunk {
    private short base;
    private float vis;
    private float flux;
    private float corruption;

    public AuraChunk(short base, float vis, float flux) {
        this.base = base;
        this.vis = vis;
        this.flux = flux;
        this.corruption = 0.0f;
    }

    public float getVis() { return vis; }
    public void setVis(float vis) { this.vis = vis; }
    public float getFlux() { return flux; }
    public void setFlux(float flux) { this.flux = flux; }
    public short getBase() { return base; }
    public void setBase(short base) { this.base = base; }

    public float getCorruption() { return corruption; }
    public void setCorruption(float corruption) { this.corruption = corruption; }

    /**
     * Recharges vis up to the base limit.
     */
    public void rechargeVis(float amount) {
        this.vis += amount;
        if (this.vis > this.base) {
            this.vis = this.base;
        }
    }

    /**
     * Spills flux above the threshold and returns the amount spilled.
     */
    public float spillFlux(float threshold) {
        if (this.flux > threshold) {
            float spilled = this.flux - threshold;
            this.flux = threshold;
            return spilled;
        }
        return 0f;
    }

    /**
     * Degrades corruption by the given rate.
     */
    public void degradeCorruption(float rate) {
        if (this.corruption > 0) {
            this.corruption -= this.corruption * rate;
            if (this.corruption < 0.001f) {
                this.corruption = 0f;
            }
        }
    }
}
