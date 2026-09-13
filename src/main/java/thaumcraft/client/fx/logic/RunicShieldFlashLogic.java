package thaumcraft.client.fx.logic;

public class RunicShieldFlashLogic {

    public record FlashResult(float opacity, int duration, int colorTint) {}

    public static FlashResult calculateFlash(int currentShield, int maxShield) {
        if (maxShield <= 0) {
            return new FlashResult(0.0f, 0, 0x000000);
        }

        float percentage = Math.max(0.0f, Math.min(1.0f, (float) currentShield / maxShield));

        float opacity = 0.3f + 0.7f * (1.0f - percentage);
        int duration = 10 + (int) (20.0f * (1.0f - percentage));

        int r = 255;
        int g = (int) (215 * percentage);
        int b = 0;
        int colorTint = (r << 16) | (g << 8) | b;

        return new FlashResult(opacity, duration, colorTint);
    }
}
