package thaumcraft.common.lib.research.theorycraft.logic;

/**
 * Pure, headless domain logic methods for Theorycraft card bonuses and calculations
 * with zero Minecraft server or registry dependencies.
 */
public class TheorycraftCardBonusLogic {

    public static int applyFocusBonus(int currentAuromancyPoints) {
        return currentAuromancyPoints + 15;
    }

    public static int applyCalibrateBonus(int currentArtificePoints) {
        return currentArtificePoints + 15;
    }

    public static int applyMeasureBonus(int currentInfusionPoints) {
        return currentInfusionPoints + 15;
    }

    public static int applySculptingBonus(int currentGolemancyPoints) {
        return currentGolemancyPoints + 15;
    }

    public static int calculateBeaconPenalty(int currentPenalty) {
        return currentPenalty + 1;
    }

    public static int calculateBeaconBonusDraws(int currentBonusDraws) {
        return currentBonusDraws + 1;
    }

    public static int calculatePortalBonusDraws(int currentBonusDraws) {
        return currentBonusDraws + 2;
    }

    public static boolean shouldGrantInspirationRefund(float randomRoll, float chance) {
        return randomRoll < chance;
    }

    public static int calculateTruthBonus(int currentEldritchPoints, int rolledBonus) {
        return currentEldritchPoints + Math.max(0, rolledBonus);
    }

    public static int calculatePortalEldritchBonus(int currentEldritchPoints, int rolledBonus) {
        return currentEldritchPoints + Math.max(0, rolledBonus);
    }
}
