package thaumcraft.common.golems.seals;

/**
 * Pure Java logic class for Golem Fill Seals.
 * Decoupled from Minecraft/Forge to allow for fast, reliable unit testing.
 */
public class SealFillLogic {

    /**
     * Represents a simplified fluid container state.
     */
    public record ContainerState(String fluidType, int currentAmount, int maxCapacity) {
        public boolean hasSpace() {
            return currentAmount < maxCapacity;
        }

        public boolean acceptsFluid(String otherFluid) {
            if (otherFluid == null || otherFluid.isEmpty()) return false;
            return fluidType == null || fluidType.isEmpty() || fluidType.equals(otherFluid);
        }
    }

    /**
     * Represents a golem carrying fluid.
     */
    public record GolemState(String fluidType, int carryCapacity) {}

    /**
     * Checks if the container can accept the golem's fluid.
     *
     * @param container The state of the fluid container.
     * @param fluidToFill The fluid type to fill.
     * @return true if the container can accept the fluid.
     */
    public static boolean canAcceptFluid(ContainerState container, String fluidToFill) {
        if (container == null || fluidToFill == null || fluidToFill.isEmpty()) return false;
        return container.hasSpace() && container.acceptsFluid(fluidToFill);
    }

    /**
     * Calculates how many fluid units a golem will deposit in a single pass.
     *
     * @param container The state of the fluid container.
     * @param golem The state of the golem carrying fluid.
     * @return The number of units to fill. Returns 0 if incompatible or full.
     */
    public static int calculateFillUnits(ContainerState container, GolemState golem) {
        if (container == null || golem == null) return 0;
        if (!canAcceptFluid(container, golem.fluidType())) return 0;

        int spaceAvailable = container.maxCapacity() - container.currentAmount();
        return Math.min(spaceAvailable, golem.carryCapacity());
    }
}
