package thaumcraft.common.golems.seals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Pure Java logic helper for Empty Seal (fluid draining and allocation).
 * Strict mathematical decoupling: contains zero Minecraft or NeoForge classloader imports.
 */
public class SealEmptyLogic {

    public static class FluidSource {
        public final int id;
        public final String fluidType;
        public int amount;

        public FluidSource(int id, String fluidType, int amount) {
            this.id = id;
            this.fluidType = fluidType;
            this.amount = amount;
        }

        public int getAmount() {
            return amount;
        }
    }

    public static class FluidDestination {
        public final int id;
        public final String fluidType; // can be null for empty tanks accepting anything
        public int currentAmount;
        public final int capacity;

        public FluidDestination(int id, String fluidType, int currentAmount, int capacity) {
            this.id = id;
            this.fluidType = fluidType;
            this.currentAmount = currentAmount;
            this.capacity = capacity;
        }

        public int getRemainingCapacity() {
            return capacity - currentAmount;
        }
    }

    public static class Allocation {
        public final int sourceId;
        public final int destinationId;
        public final int amountAllocated;

        public Allocation(int sourceId, int destinationId, int amountAllocated) {
            this.sourceId = sourceId;
            this.destinationId = destinationId;
            this.amountAllocated = amountAllocated;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Allocation that = (Allocation) o;
            if (sourceId != that.sourceId) return false;
            if (destinationId != that.destinationId) return false;
            return amountAllocated == that.amountAllocated;
        }

        @Override
        public int hashCode() {
            int result = sourceId;
            result = 31 * result + destinationId;
            result = 31 * result + amountAllocated;
            return result;
        }
    }

    /**
     * Priorities sources with the most fluid available.
     */
    public static List<FluidSource> sortSourcesByPriority(List<FluidSource> sources) {
        return sources.stream()
                .sorted(Comparator.comparingInt(FluidSource::getAmount).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Priorities destinations with the least remaining capacity (so they fill up completely first),
     * and only matching fluid types.
     */
    public static List<FluidDestination> matchAndSortDestinations(FluidSource source, List<FluidDestination> destinations) {
        return destinations.stream()
                .filter(d -> d.getRemainingCapacity() > 0)
                .filter(d -> d.fluidType == null || d.fluidType.equals(source.fluidType))
                .sorted(Comparator.comparingInt(FluidDestination::getRemainingCapacity))
                .collect(Collectors.toList());
    }

    /**
     * Allocates fluid from multiple sources to multiple destinations based on priority.
     */
    public static List<Allocation> allocateFluids(List<FluidSource> sources, List<FluidDestination> destinations) {
        List<Allocation> allocations = new ArrayList<>();

        List<FluidSource> sortedSources = sortSourcesByPriority(sources);

        for (FluidSource source : sortedSources) {
            if (source.amount <= 0) continue;

            List<FluidDestination> validDestinations = matchAndSortDestinations(source, destinations);

            for (FluidDestination dest : validDestinations) {
                if (source.amount <= 0) break;

                int remainingCap = dest.getRemainingCapacity();
                if (remainingCap <= 0) continue;

                int transferAmount = Math.min(source.amount, remainingCap);

                allocations.add(new Allocation(source.id, dest.id, transferAmount));

                source.amount -= transferAmount;
                dest.currentAmount += transferAmount;
            }
        }

        return allocations;
    }
}
