package thaumcraft.common.items.armor;

public class GogglesRevealingRangeLogic {

    public enum DetailResolution {
        MAXIMUM,
        HIGH,
        MEDIUM,
        LOW,
        NONE
    }

    public static DetailResolution getResolutionForDistance(double distance) {
        if (distance < 0) {
            return DetailResolution.NONE;
        } else if (distance <= 2.0) {
            return DetailResolution.MAXIMUM;
        } else if (distance <= 4.0) {
            return DetailResolution.HIGH;
        } else if (distance <= 8.0) {
            return DetailResolution.MEDIUM;
        } else if (distance <= 16.0) {
            return DetailResolution.LOW;
        } else {
            return DetailResolution.NONE;
        }
    }
}
