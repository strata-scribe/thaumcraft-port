package thaumcraft.client.gui.logic;

public class AuraHudFormattingLogic {

    public static float calculateBarPercentage(float amount, float base) {
        if (base <= 0) {
            return 0.0f;
        }
        return Math.max(0.0f, Math.min(1.0f, amount / base));
    }

    public static int getFluxBarColor(float flux, float base, int normalColor, int overflowColor) {
        if (base <= 0 || flux > base) {
            return overflowColor;
        }
        return normalColor;
    }

    public static String formatLocalizedText(String translationKey, float amount) {
        return translationKey + ": " + Math.round(amount);
    }

    public static int calculateBarX(int screenWidth, int barWidth, int xOffset) {
        return (screenWidth / 2) - (barWidth / 2) + xOffset;
    }

    public static int calculateBarY(int screenHeight, int yOffset) {
        return screenHeight + yOffset;
    }

    public static int calculateBarWidth(float percentage, int maxBarWidth) {
        return Math.round(percentage * maxBarWidth);
    }
}
