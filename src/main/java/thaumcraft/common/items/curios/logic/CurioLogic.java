package thaumcraft.common.items.curios.logic;

public final class CurioLogic {

    public static final int VARIANT_COUNT = 6;

    private CurioLogic() {}

    public static boolean isValidVariant(int variant) {
        return variant >= 0 && variant < VARIANT_COUNT;
    }

    public static String getVariantName(int variant) {
        return switch (variant) {
            case 0 -> "arcane";
            case 1 -> "preserved";
            case 2 -> "ancient";
            case 3 -> "eldritch";
            case 4 -> "illuminated";
            case 5 -> "twisted";
            default -> "unknown";
        };
    }

    public static String getResearchCategory(int variant) {
        return switch (variant) {
            case 0 -> "BASICS";
            case 1 -> "ALCHEMY";
            case 2 -> "INFUSION";
            case 3 -> "ELDRITCH";
            case 4 -> "AUROMANCY";
            case 5 -> "GOLEMANCY";
            default -> "BASICS";
        };
    }

    public static int calculateKnowledgeGrant(int variant, float roll) {
        return roll >= 0.5f ? 2 : 1;
    }

    public static int calculateTheoryBonus(int variant) {
        return 25;
    }
}
