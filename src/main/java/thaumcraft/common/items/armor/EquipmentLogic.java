package thaumcraft.common.items.armor;

/**
 * Pure mathematical logic and decoupled state rules for Thaumcraft equipment:
 * - Boots of the Traveller (step height, fall damage reduction, speed, jump boost)
 * - Thaumostatic Harness (flight physics, levitation stability, vis drainage)
 * - Void Armor and Void Robes (passive repair, warp values, vis discounts)
 * - Thaumium Fortress Armor and Masks (damage absorption ratios, set bonuses, mask effects)
 * - Runic Shielding (absorption, spillover)
 *
 * PURE JAVA INVARIANT: This class must never import from net.minecraft or net.neoforged.
 */
public final class EquipmentLogic {

    private EquipmentLogic() {}

    // ==========================================
    // 1. BOOTS OF THE TRAVELLER
    // ==========================================

    /**
     * Fall damage reduction formula:
     * hasCharge ? (damage / 2.0f - 1.0f < 1.0f ? 0.0f : damage / 2.0f - 1.0f) : damage
     * Clamped to 0.0 minimum.
     */
    public static float calculateFallDamage(float damage, boolean hasCharge) {
        if (!hasCharge || damage <= 0.0f) {
            return Math.max(0.0f, damage);
        }
        float reduced = damage / 2.0f - 1.0f;
        if (reduced < 1.0f) {
            return 0.0f;
        }
        return reduced;
    }

    /**
     * Extended fall damage reduction formula supporting Boots and Ring of the Cloud.
     */
    public static float calculateFallDamage(float damage, boolean hasBoots, boolean hasCloudRing) {
        if (damage <= 0.0f) {
            return 0.0f;
        }
        if (hasBoots) {
            float reduced = Math.max(0.0f, damage / 2.0f - 1.0f);
            if (reduced < 1.0f) {
                return 0.0f;
            }
            damage = reduced;
        }
        if (hasCloudRing) {
            float reduced = Math.max(0.0f, damage / 3.0f - 2.0f);
            if (reduced < 1.0f) {
                return 0.0f;
            }
            if (reduced < damage) {
                damage = reduced;
            }
        }
        return damage;
    }

    /**
     * Jump boost velocity: adds +0.275 vertical velocity when charged.
     */
    public static double calculateJumpBoost(boolean hasCharge) {
        return hasCharge ? 0.2750000059604645 : 0.0;
    }

    /**
     * Calculate jump velocity modification.
     */
    public static double calculateJumpVelocity(double currentMotionY, boolean hasBoots, boolean hasCharge) {
        if (hasBoots && hasCharge) {
            return currentMotionY + 0.2750000059604645;
        }
        return currentMotionY;
    }

    /**
     * Step height modification:
     * Increases player step height from base (0.6f) to 1.0f while active (moving forward, not sneaking, charged).
     */
    public static float calculateStepHeight(float baseStep, boolean hasBoots, boolean hasCharge, boolean isSneaking, boolean movingForward) {
        if (hasBoots && hasCharge && !isSneaking && movingForward) {
            return 1.0f;
        }
        return baseStep;
    }

    public static float calculateStepHeight(float baseStep, boolean active) {
        return active ? 1.0f : baseStep;
    }

    /**
     * Ground speed bonus:
     * Adds +0.05f on ground (0.0125f in water).
     */
    public static float calculateGroundSpeedBonus(boolean onGround, boolean inWater, boolean hasBoots, boolean hasCharge) {
        if (!hasBoots || !hasCharge || !onGround) {
            return 0.0f;
        }
        return inWater ? (0.05f / 4.0f) : 0.05f;
    }

    /**
     * Air jump movement factor (vanilla base 0.02f -> 0.05f with boots).
     */
    public static float calculateAirJumpFactor(boolean hasBoots, boolean hasCharge) {
        return (hasBoots && hasCharge) ? 0.05f : 0.02f;
    }

    /**
     * Water movement relative boost (0.025f).
     */
    public static float calculateWaterMovementBonus(boolean hasBoots, boolean hasCharge) {
        return (hasBoots && hasCharge) ? 0.025f : 0.0f;
    }


    // ==========================================
    // 2. THAUMOSTATIC HARNESS
    // ==========================================

    /**
     * Vis drain per second:
     * 1 charge/sec hovering, 2.5 charges/sec flying. 0 if inactive.
     */
    public static float calculateHarnessVisDrain(boolean isFlying, boolean isHovering) {
        if (isFlying) {
            return 2.5f;
        }
        if (isHovering) {
            return 1.0f;
        }
        return 0.0f;
    }

    /**
     * Vis drain per tick (20 ticks per second).
     */
    public static float calculateHarnessDrainPerTick(boolean isFlying, boolean isHovering) {
        return calculateHarnessVisDrain(isFlying, isHovering) / 20.0f;
    }

    /**
     * Flight physics velocity damping:
     * Dampens current motion toward target speed with acceleration factor 0.15.
     */
    public static double calculateHarnessSpeed(double currentMotion, double targetSpeed) {
        double diff = targetSpeed - currentMotion;
        if (Math.abs(diff) < 0.001) {
            return targetSpeed;
        }
        return currentMotion + diff * 0.15;
    }

    /**
     * Vertical motion for flight:
     * Jump provides +0.15 thrust (up to +0.60).
     * Sneak provides -0.15 descent (down to -0.40).
     * Jump + Sneak cancel out to hover.
     * Hover dampens vertical motion (* 0.85), snapping to 0.0 when small (< 0.01).
     */
    public static double calculateHarnessVerticalMotion(double currentMotionY, boolean jumpHeld, boolean sneakHeld, boolean hoverActive) {
        if (jumpHeld && sneakHeld) {
            return hoverActive ? (Math.abs(currentMotionY) < 0.01 ? 0.0 : currentMotionY * 0.85) : currentMotionY;
        }
        if (jumpHeld) {
            return Math.min(0.60, currentMotionY + 0.15);
        }
        if (sneakHeld) {
            return Math.max(-0.40, currentMotionY - 0.15);
        }
        if (hoverActive) {
            return Math.abs(currentMotionY) < 0.01 ? 0.0 : currentMotionY * 0.85;
        }
        return currentMotionY;
    }

    /**
     * Dampens descent upon fuel depletion to prevent instant fall death (clamped to max -0.30).
     */
    public static double calculateHarnessDescentDamping(double currentMotionY) {
        return Math.max(-0.30, currentMotionY);
    }


    // ==========================================
    // 3. VOID ARMOR & VOID ROBES
    // ==========================================

    /**
     * Passive repair: repairs 1 durability every 20 ticks if damaged.
     */
    public static int calculateVoidRepair(int currentDamage, int tickCount) {
        if (currentDamage > 0 && tickCount > 0 && tickCount % 20 == 0) {
            return currentDamage - 1;
        }
        return Math.max(0, currentDamage);
    }

    public static int calculateVoidSelfRepair(int currentDamage, int ticksExisted) {
        return calculateVoidRepair(currentDamage, ticksExisted);
    }

    /**
     * Warping gear values:
     * Void Armor: +1 Warp per piece.
     * Void Robes: +3 Warp per piece.
     */
    public static int getVoidArmorWarp() {
        return 1;
    }

    public static int getVoidRobeWarp() {
        return 3;
    }

    public static int calculateWarp(boolean isVoidRobe, int pieceCount) {
        return (isVoidRobe ? 3 : 1) * Math.max(0, pieceCount);
    }

    /**
     * Vis discount: 5% per piece of Void Robes (max 100%).
     */
    public static int calculateVisDiscount(int voidRobePieces) {
        return Math.min(100, Math.max(0, voidRobePieces * 5));
    }


    // ==========================================
    // 4. THAUMIUM FORTRESS ARMOR & MASKS
    // ==========================================

    public enum DamageSourceType {
        NORMAL,
        MAGIC,
        FIRE,
        EXPLOSION,
        UNBLOCKABLE
    }

    /**
     * Damage absorption ratio:
     * NORMAL: totalArmor / 25.0
     * MAGIC: totalArmor / 35.0
     * FIRE / EXPLOSION: totalArmor / 20.0
     * UNBLOCKABLE: 0.0
     */
    public static double calculateArmorAbsorptionRatio(DamageSourceType source, int totalArmor) {
        if (totalArmor <= 0) {
            return 0.0;
        }
        switch (source) {
            case MAGIC:
                return totalArmor / 35.0;
            case FIRE:
            case EXPLOSION:
                return totalArmor / 20.0;
            case UNBLOCKABLE:
                return 0.0;
            case NORMAL:
            default:
                return totalArmor / 25.0;
        }
    }

    /**
     * Calculates absorbed damage amount:
     * absorbed = incomingDamage * ratio (clamped to incomingDamage).
     */
    public static float calculateArmorAbsorption(DamageSourceType source, int totalArmor, float incomingDamage) {
        if (incomingDamage <= 0.0f || totalArmor <= 0) {
            return 0.0f;
        }
        double ratio = calculateArmorAbsorptionRatio(source, totalArmor);
        float absorbed = (float) (incomingDamage * ratio);
        return Math.min(incomingDamage, Math.max(0.0f, absorbed));
    }

    /**
     * Calculates damage penetrating after armor absorption.
     */
    public static float calculatePenetratingDamage(DamageSourceType source, int totalArmor, float incomingDamage) {
        if (incomingDamage <= 0.0f) {
            return 0.0f;
        }
        return Math.max(0.0f, incomingDamage - calculateArmorAbsorption(source, totalArmor, incomingDamage));
    }

    /**
     * Fortress set bonuses:
     * 1 piece: 0 bonus
     * 2 pieces: +1 Armor, +1 Toughness
     * 3 pieces: +2 Armor, +2 Toughness
     */
    public static int calculateFortressBonusArmor(int wornPiecesCount) {
        return Math.max(0, wornPiecesCount - 1);
    }

    public static int calculateFortressBonusToughness(int wornPiecesCount) {
        return Math.max(0, wornPiecesCount - 1);
    }

    /**
     * Mask 0 (Grinning Devil):
     * Reduces Warp Event severity roll by 2 + rand4 (where rand4 is in [0, 3]).
     * Result is clamped to minimum 0.
     */
    public static int calculateMaskWarpReduction(int baseRoll, int rand4) {
        return Math.max(0, baseRoll - (2 + Math.max(0, Math.min(3, rand4))));
    }

    /**
     * Mask 1 (Angry Ghost):
     * Wither retaliation trigger: randFloat < incomingDamage / 10.0f.
     */
    public static boolean shouldTriggerMaskWither(float incomingDamage, float randomRoll) {
        if (incomingDamage <= 0.0f) {
            return false;
        }
        return randomRoll < (incomingDamage / 10.0f);
    }

    /**
     * Mask 2 (Sipping Fiend):
     * Lifesteal trigger: randFloat < outgoingDamage / 12.0f.
     */
    public static boolean shouldTriggerMaskLifesteal(float outgoingDamage, float randomRoll) {
        if (outgoingDamage <= 0.0f) {
            return false;
        }
        return randomRoll < (outgoingDamage / 12.0f);
    }

    /**
     * Lifesteal heal amount: 1.0 HP (0.5 heart).
     */
    public static float calculateMaskLifestealAmount() {
        return 1.0f;
    }


    // ==========================================
    // 5. RUNIC SHIELDING (COMMON EQUIPMENT UTILITY)
    // ==========================================

    public record ShieldAbsorptionResult(int newShield, float penetratingDamage) {}

    /**
     * Runic shield absorption and spillover.
     */
    public static ShieldAbsorptionResult calculateRunicAbsorption(int currentShield, float damage) {
        if (damage <= 0.0f) {
            return new ShieldAbsorptionResult(Math.max(0, currentShield), 0.0f);
        }
        if (currentShield <= 0) {
            return new ShieldAbsorptionResult(0, damage);
        }
        if (currentShield >= damage) {
            int newShield = currentShield - (int) Math.ceil(damage);
            return new ShieldAbsorptionResult(Math.max(0, newShield), 0.0f);
        } else {
            float penetrating = damage - currentShield;
            return new ShieldAbsorptionResult(0, Math.max(0.0f, penetrating));
        }
    }
}
