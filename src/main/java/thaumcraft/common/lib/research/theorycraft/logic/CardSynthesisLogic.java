package thaumcraft.common.lib.research.theorycraft.logic;

import java.util.List;
import java.util.Random;

public class CardSynthesisLogic {
    public static class SynthesisResult {
        public String aspect1;
        public String aspect2;
        public String aspect3;

        public SynthesisResult(String aspect1, String aspect2, String aspect3) {
            this.aspect1 = aspect1;
            this.aspect2 = aspect2;
            this.aspect3 = aspect3;
        }
    }

    public static SynthesisResult initializeSynthesis(long seed, List<String[]> compoundAspects) {
        if (compoundAspects == null || compoundAspects.isEmpty()) {
            return null;
        }
        Random r = new Random(seed);
        int num = r.nextInt(compoundAspects.size());
        String[] aspect3 = compoundAspects.get(num);
        return new SynthesisResult(aspect3[1], aspect3[2], aspect3[0]);
    }

    public static class ActivationResult {
        public int bonusProgress;
        public int bonusInspiration;

        public ActivationResult(int bonusProgress, int bonusInspiration) {
            this.bonusProgress = bonusProgress;
            this.bonusInspiration = bonusInspiration;
        }
    }

    public static ActivationResult calculateActivation(float randomFloat) {
        int inspiration = 0;
        if (randomFloat < 0.33f) {
            inspiration = 1;
        }
        return new ActivationResult(40, inspiration);
    }
}
