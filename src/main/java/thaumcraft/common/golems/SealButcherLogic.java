package thaumcraft.common.golems;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pure Java logic for evaluating animal populations for the Butcher/Breeder Seal.
 * Decoupled from Minecraft and Forge imports for unit testing.
 */
public final class SealButcherLogic {

    private SealButcherLogic() {}

    /**
     * Pure Java representation of an animal for population evaluation.
     */
    public record AnimalData(String species, boolean isAdult) {}

    /**
     * The task that should be dispatched for a given species.
     */
    public enum TaskType {
        CULL, // Too many adults, need to dispatch a kill task
        FEED, // Adults exist and can breed, but below threshold
        NONE  // No actionable population state
    }

    /**
     * Evaluates a list of animals and determines the appropriate action for each species.
     *
     * @param animals        The list of animals in the seal's area.
     * @param adultThreshold The maximum number of adults allowed before culling starts.
     * @return A map of species names to the evaluated TaskType.
     */
    public static Map<String, TaskType> evaluatePopulation(List<AnimalData> animals, int adultThreshold) {
        if (animals == null || animals.isEmpty()) {
            return new HashMap<>();
        }

        // Count adults per species
        Map<String, Integer> adultCounts = new HashMap<>();

        for (AnimalData animal : animals) {
            if (animal.isAdult()) {
                adultCounts.put(animal.species(), adultCounts.getOrDefault(animal.species(), 0) + 1);
            } else {
                // Ensure the species exists in the map even if there are only juveniles
                adultCounts.putIfAbsent(animal.species(), 0);
            }
        }

        Map<String, TaskType> results = new HashMap<>();
        for (Map.Entry<String, Integer> entry : adultCounts.entrySet()) {
            String species = entry.getKey();
            int adults = entry.getValue();

            if (adults > adultThreshold) {
                results.put(species, TaskType.CULL);
            } else if (adults >= 2) {
                results.put(species, TaskType.FEED);
            } else {
                results.put(species, TaskType.NONE);
            }
        }

        return results;
    }
}
