package thaumcraft.common.entities.monster;

/**
 * Pure Java logic helper for Mind Spider swarm and status effects.
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class MindSpiderLogic {

    /**
     * @return Swarm alerting radius in blocks (16.0).
     */
    public static double getSwarmAlertRadius() {
        return 16.0;
    }

    /**
     * @return Base poison duration in ticks (100).
     */
    public static int getPoisonDurationTicks() {
        return 100;
    }

    /**
     * @return Temporary blindness duration in ticks (60).
     */
    public static int getBlindnessDurationTicks() {
        return 60;
    }
}
