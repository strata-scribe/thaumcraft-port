package thaumcraft.common.entities;

/**
 * Pure Java logic helper for entity combat, valuation, and life cycle state transitions.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class EntityCombatLogic {

    // --- Pech Item Valuation & Barter Mechanics ---

    /**
     * Calculates the barter value of an item offered to a Pech.
     * Ender Pearls have a fixed value of 15.
     * Items with Aspect.DESIRE evaluate to min(32, desireAmount / 2).
     *
     * @param desireAspect The amount of Aspect.DESIRE on the item
     * @param isEnderPearl Whether the item is an Ender Pearl
     * @return Integer valuation between 0 and 32
     */
    public static int calculatePechItemValue(int desireAspect, boolean isEnderPearl) {
        if (isEnderPearl) {
            return 15;
        }
        if (desireAspect > 1) {
            return Math.min(32, desireAspect / 2);
        }
        return 0;
    }

    /**
     * Overload accepting count of Ender Pearls.
     */
    public static int calculatePechItemValue(int desireAspect, int enderPearls) {
        if (enderPearls > 0) {
            return 15;
        }
        if (desireAspect > 1) {
            return Math.min(32, desireAspect / 2);
        }
        return 0;
    }

    /**
     * Checks if offering a valued item successfully tames a wild Pech.
     * Roll is out of 10 (0..9). Value >= 10 guarantees 100% taming success.
     *
     * @param itemValue Evaluated value of the offered item
     * @param roll10 Random roll in range [0, 9]
     * @return true if taming succeeds
     */
    public static boolean isTamingSuccessful(int itemValue, int roll10) {
        if (itemValue <= 0) {
            return false;
        }
        return roll10 < itemValue;
    }

    /**
     * Alias for isTamingSuccessful.
     */
    public static boolean rollPechTaming(int itemValue, int roll10) {
        return isTamingSuccessful(itemValue, roll10);
    }

    /**
     * Determines whether a Pech reverts to untamed/wild state following a trade.
     * Untame risk percentage is (value / 2)%.
     *
     * @param itemValue Evaluated value of the traded item
     * @param roll100 Random roll in range [1, 100] (or [0, 99])
     * @return true if Pech untames
     */
    public static boolean shouldPechUntameAfterTrade(int itemValue, int roll100) {
        if (itemValue <= 0) {
            return false;
        }
        return roll100 <= (itemValue / 2);
    }

    /**
     * Alias for shouldPechUntameAfterTrade.
     */
    public static boolean shouldPechUntame(int itemValue, int roll100) {
        return shouldPechUntameAfterTrade(itemValue, roll100);
    }

    /**
     * Calculates the barter reward tier / chunk size:
     * Math.min(5, Math.max((value + 1) / 2, rollValue + 1))
     *
     * @param value Traded item value
     * @param rollValue Random roll bounded by remaining trade value
     * @return Tier clamped between 1 and 5
     */
    public static int calculateBarterChunkSize(int value, int rollValue) {
        if (value <= 0) {
            return 1;
        }
        int minChoice = (value + 1) / 2;
        int choice = Math.max(minChoice, rollValue + 1);
        return Math.min(5, Math.max(1, choice));
    }

    /**
     * Trade tier chunk partitioning for loot tables.
     */
    public static int calculateTradeChunkTier(int remainingValue, int randomRoll) {
        if (remainingValue <= 0) {
            return 1;
        }
        int minChoice = (remainingValue + 1) / 2;
        int clampedRandom = (randomRoll % remainingValue) + 1;
        return Math.min(5, Math.max(1, Math.max(minChoice, clampedRandom)));
    }


    // --- Eldritch Guardian Combat ---

    /**
     * Calculates incoming damage applied to Eldritch Guardian.
     * Magic damage is halved (damage * 0.5f).
     *
     * @param incomingDamage The base incoming damage amount
     * @param isMagic Whether the damage source is magic
     * @return Resulting damage amount
     */
    public static float calculateGuardianMagicDamage(float incomingDamage, boolean isMagic) {
        if (incomingDamage <= 0.0f) {
            return 0.0f;
        }
        return isMagic ? (incomingDamage * 0.5f) : incomingDamage;
    }

    /**
     * Alias for calculateGuardianMagicDamage.
     */
    public static float calculateGuardianDamage(float incomingDamage, boolean isMagic) {
        return calculateGuardianMagicDamage(incomingDamage, isMagic);
    }

    /**
     * Calculates splash damage dealt by Eldritch Orb projectile:
     * attackDamage * 0.666f
     *
     * @param attackDamage The Guardian's base attack damage (e.g. 7.0f)
     * @return Orb splash magic damage (e.g. 4.662f)
     */
    public static float calculateEldritchOrbDamage(float attackDamage) {
        if (attackDamage <= 0.0f) {
            return 0.0f;
        }
        return attackDamage * 0.666f;
    }

    /**
     * Calculates initial outer dimension absorption shielding for Eldritch Guardian (+25.0f).
     *
     * @param isOuterDimension Whether the entity is in the Outer Dimension
     * @return Absorption health (25.0f if outer dimension, 0.0f otherwise)
     */
    public static float calculateGuardianAbsorption(boolean isOuterDimension) {
        return isOuterDimension ? 25.0f : 0.0f;
    }

    /**
     * Overload taking baseHealth.
     */
    public static float calculateGuardianAbsorption(float baseHealth, boolean isOuterDimension) {
        return isOuterDimension ? 25.0f : 0.0f;
    }

    /**
     * Checks if Eldritch Guardian triggers a sonic screech attack (15% chance).
     *
     * @param roll Probability roll in [0.0, 1.0)
     * @return true if screech attack triggers
     */
    public static boolean shouldGuardianScreech(float roll) {
        return roll < 0.15f;
    }

    /**
     * Calculates Temporary Warp inflicted by sonic screech attack (1 + rand(3) in [1, 3]).
     *
     * @param rand3 Random integer [0, 2]
     * @return Temporary Warp value [1, 3]
     */
    public static int calculateScreechTemporaryWarp(int rand3) {
        return 1 + Math.abs(rand3 % 3);
    }


    // --- Mind Spider Hallucination & Life Cycle ---

    /**
     * Checks whether a harmless hallucination Mind Spider has expired and should despawn.
     * Default lifespan is 1200 ticks (60 seconds).
     *
     * @param ticksExisted Ticks the entity has been alive
     * @param lifespan Maximum lifespan in ticks (e.g. 1200)
     * @return true if entity should despawn
     */
    public static boolean isHarmlessExpired(int ticksExisted, int lifespan) {
        return ticksExisted >= lifespan;
    }

    /**
     * Alias for isHarmlessExpired.
     */
    public static boolean isMindSpiderExpired(int ticksExisted, int lifespan) {
        return isHarmlessExpired(ticksExisted, lifespan);
    }

    /**
     * Alias for isHarmlessExpired.
     */
    public static boolean shouldMindSpiderDespawn(int ticksExisted, int lifespan) {
        return isHarmlessExpired(ticksExisted, lifespan);
    }

    /**
     * Checks if Mind Spider can deal damage or engage in hostile attacks.
     * Harmless hallucination spiders deal 0 damage.
     *
     * @param isHarmless Whether the spider is a harmless hallucination
     * @return true if spider can attack
     */
    public static boolean canMindSpiderAttack(boolean isHarmless) {
        return !isHarmless;
    }

    /**
     * Calculates Mind Spider attack damage. Returns 0.0f if harmless.
     *
     * @param baseDamage Normal spider attack damage
     * @param isHarmless Whether the spider is a harmless hallucination
     * @return Effective attack damage
     */
    public static float calculateMindSpiderDamage(float baseDamage, boolean isHarmless) {
        return isHarmless ? 0.0f : baseDamage;
    }
}
