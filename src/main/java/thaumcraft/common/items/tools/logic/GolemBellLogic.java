package thaumcraft.common.items.tools.logic;

/**
 * Pure headless domain logic for Golem Bell interactions and status formatting.
 * Zero Minecraft server or registry dependencies.
 */
public final class GolemBellLogic {

    private GolemBellLogic() {}

    /**
     * Determines whether the bell can interact with a seal target.
     *
     * @param isTargetSeal true if the target is a seal
     * @return true if interaction is allowed
     */
    public static boolean canInteractWithSeal(boolean isTargetSeal) {
        return isTargetSeal;
    }

    /**
     * Determines whether the bell can bind or configure a golem.
     * Binding requires a valid golem target while the player is sneaking.
     *
     * @param hasTargetGolem true if a golem is targeted
     * @param isSneaking     true if the player is sneaking/crouching
     * @return true if binding can occur
     */
    public static boolean canBindGolem(boolean hasTargetGolem, boolean isSneaking) {
        return hasTargetGolem && isSneaking;
    }

    /**
     * Formats human-readable status for a golem.
     *
     * @param golemName name of the golem (or null)
     * @param isWorking true if actively working, false if idle
     * @return formatted status string
     */
    public static String formatGolemStatus(String golemName, boolean isWorking) {
        if (golemName == null) {
            return "Unknown Golem";
        }
        return golemName + " (" + (isWorking ? "Working" : "Idle") + ")";
    }
}
