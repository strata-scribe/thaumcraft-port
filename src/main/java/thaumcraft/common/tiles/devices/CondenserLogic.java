package thaumcraft.common.tiles.devices;

import thaumcraft.api.aura.AuraChunk;
import java.util.Random;
import java.util.List;
import java.util.function.Consumer;

public class CondenserLogic {
    private int cost = 0;
    private final int MAX_COST = 100;
    private final Random random = new Random();

    public interface BlockProvider {
        boolean isLattice(int dx, int dy, int dz);
        boolean isDirtyLattice(int dx, int dz);
        void makeDirty(int dx, int dy, int dz);
    }

    public void tick(AuraChunk chunk, BlockProvider provider, Consumer<Boolean> emitEssentiaOrSlag) {
        if (chunk == null) return;

        int count = countLattices(provider);
        if (count == 0) return;

        float flux = chunk.getFlux();
        if (flux > 0.1f) {
            float draw = Math.min(flux, count * 0.1f);
            chunk.setFlux(chunk.getFlux() - draw);
            cost += (int) (draw * 10);

            if (cost >= MAX_COST) {
                cost -= MAX_COST;
                emitEssentiaOrSlag.accept(true);
            }

            // Randomly clog lattices
            if (random.nextFloat() < draw * 0.05f) {
                clogRandomLattice(provider);
            }
        }
    }

    private int countLattices(BlockProvider provider) {
        int count = 0;
        for (int y = 1; y <= 3; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (provider.isLattice(x, y, z)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private void clogRandomLattice(BlockProvider provider) {
        // Collect all clean lattices
        java.util.ArrayList<int[]> cleanLattices = new java.util.ArrayList<>();
        for (int y = 1; y <= 3; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (provider.isLattice(x, y, z)) {
                        cleanLattices.add(new int[]{x, y, z});
                    }
                }
            }
        }

        if (!cleanLattices.isEmpty()) {
            int[] chosen = cleanLattices.get(random.nextInt(cleanLattices.size()));
            provider.makeDirty(chosen[0], chosen[1], chosen[2]);
        }
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
