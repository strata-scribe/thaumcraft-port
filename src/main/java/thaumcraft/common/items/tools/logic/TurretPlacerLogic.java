package thaumcraft.common.items.tools.logic;

public final class TurretPlacerLogic {

    private TurretPlacerLogic() {}

    public static boolean canPlaceOnFace(String directionName) {
        return "UP".equalsIgnoreCase(directionName);
    }

    public static boolean canSurviveOn(boolean isSolidSurface) {
        return isSolidSurface;
    }

    public static int getPlacementCooldown() {
        return 10;
    }
}
