package thaumcraft.common.lib.research.theorycraft.logic;

import java.util.Random;

public class CardNotationLogic {

    public static boolean validateMaterials(int availableInk, int availablePaper) {
        return availableInk >= 1 && availablePaper >= 1;
    }

    public static int computeKnowledgeGain(long seed) {
        Random rand = new Random(seed);
        return 10 + rand.nextInt(16); // 10 to 25 inclusive
    }

    public static boolean calculateInspirationPreservation(int playerCognition, float randomFloat) {
        float chance = playerCognition * 0.1f;
        return randomFloat < chance;
    }
}
