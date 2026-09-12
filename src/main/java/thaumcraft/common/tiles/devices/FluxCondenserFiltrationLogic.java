package thaumcraft.common.tiles.devices;

import java.util.Random;

public class FluxCondenserFiltrationLogic {

    private int cleanLattices;
    private int cloggedLattices;
    private float vitiumResidue;

    private float baseExtractionRate = 0.5f;
    private float clogProbability = 0.10f; // 10% chance per 1 flux extracted
    private float residuePerFlux = 0.8f; // 0.8 vitium residue per 1 flux

    public FluxCondenserFiltrationLogic(int cleanLattices, int cloggedLattices) {
        this.cleanLattices = Math.max(0, cleanLattices);
        this.cloggedLattices = Math.max(0, cloggedLattices);
        this.vitiumResidue = 0.0f;
    }

    public int getCleanLattices() { return cleanLattices; }
    public void setCleanLattices(int cleanLattices) { this.cleanLattices = Math.max(0, cleanLattices); }

    public int getCloggedLattices() { return cloggedLattices; }
    public void setCloggedLattices(int cloggedLattices) { this.cloggedLattices = Math.max(0, cloggedLattices); }

    public float getVitiumResidue() { return vitiumResidue; }
    public void setVitiumResidue(float vitiumResidue) { this.vitiumResidue = Math.max(0, vitiumResidue); }

    public float getBaseExtractionRate() { return baseExtractionRate; }
    public void setBaseExtractionRate(float baseExtractionRate) { this.baseExtractionRate = baseExtractionRate; }

    public float getClogProbability() { return clogProbability; }
    public void setClogProbability(float clogProbability) { this.clogProbability = clogProbability; }

    public float getResiduePerFlux() { return residuePerFlux; }
    public void setResiduePerFlux(float residuePerFlux) { this.residuePerFlux = residuePerFlux; }

    /**
     * Calculates the maximum amount of flux that can be extracted per operation.
     */
    public float calculateExtractionRate() {
        return cleanLattices * baseExtractionRate;
    }

    /**
     * Processes available flux, returns the amount of flux extracted.
     * Updates clogging progression and vitium residue.
     */
    public float processFlux(float availableFlux, Random random) {
        if (cleanLattices <= 0 || availableFlux <= 0) {
            return 0f;
        }

        float extractionRate = calculateExtractionRate();
        float extracted = Math.min(availableFlux, extractionRate);

        vitiumResidue += extracted * residuePerFlux;

        // Clogging logic based on extracted flux
        int rolls = (int) extracted;
        float fractional = extracted - rolls;

        for (int i = 0; i < rolls; i++) {
            if (cleanLattices > 0 && random.nextFloat() < clogProbability) {
                clogLattice();
            }
        }

        if (cleanLattices > 0 && random.nextFloat() < (clogProbability * fractional)) {
            clogLattice();
        }

        return extracted;
    }

    private void clogLattice() {
        if (cleanLattices > 0) {
            cleanLattices--;
            cloggedLattices++;
        }
    }

    /**
     * Attempts to consume 1 full unit of Vitium residue to produce 1 Vitium aspect.
     * Returns true if successful.
     */
    public boolean extractVitiumAspect() {
        if (vitiumResidue >= 1.0f) {
            vitiumResidue -= 1.0f;
            return true;
        }
        return false;
    }
}
