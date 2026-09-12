package thaumcraft.common.crafting.logic;

public class ArcaneWorkbenchVisSourceLogic {

    public static class VisDrawResult {
        public final float auraDrawn;
        public final float[] batteryDraws;

        public VisDrawResult(float auraDrawn, float[] batteryDraws) {
            this.auraDrawn = auraDrawn;
            this.batteryDraws = batteryDraws;
        }

        public boolean isSuccess(float requiredVis) {
            float totalDrawn = auraDrawn;
            for (float draw : batteryDraws) {
                totalDrawn += draw;
            }
            return totalDrawn >= requiredVis;
        }
    }

    public static VisDrawResult calculateVisDraw(float visCost, float availableAura, float[] availableBatteryVis) {
        float remainingCost = visCost;

        float auraDrawn = Math.min(remainingCost, availableAura);
        remainingCost -= auraDrawn;

        float[] batteryDraws = new float[availableBatteryVis.length];

        for (int i = 0; i < availableBatteryVis.length; i++) {
            if (remainingCost <= 0) {
                break;
            }
            float batteryDrawn = Math.min(remainingCost, availableBatteryVis[i]);
            batteryDraws[i] = batteryDrawn;
            remainingCost -= batteryDrawn;
        }

        return new VisDrawResult(auraDrawn, batteryDraws);
    }
}
