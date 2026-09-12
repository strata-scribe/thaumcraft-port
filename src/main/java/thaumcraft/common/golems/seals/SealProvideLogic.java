package thaumcraft.common.golems.seals;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure Java logic class for Golem Provide Seals.
 * Matches player inventory deficit requests against available storage containers.
 * Decoupled from Minecraft/Forge to allow for fast, reliable unit testing.
 */
public class SealProvideLogic {

    public record ItemStackRef(String itemId) {}

    public record PlayerDeficit(String playerId, ItemStackRef item, int deficitAmount) {}

    public static class MutableStorageContainer {
        public final int containerId;
        public final ItemStackRef item;
        public int availableAmount;

        public MutableStorageContainer(int containerId, ItemStackRef item, int availableAmount) {
            this.containerId = containerId;
            this.item = item;
            this.availableAmount = availableAmount;
        }
    }

    public record StorageContainer(int containerId, ItemStackRef item, int availableAmount) {
        public MutableStorageContainer toMutable() {
            return new MutableStorageContainer(containerId, item, availableAmount);
        }
    }

    public record ProvisionTask(String playerId, int containerId, ItemStackRef item, int amountProvided) {}

    /**
     * Matches player deficits against available storage containers and generates provision tasks.
     *
     * @param deficits   A list of deficits representing what items players need.
     * @param containers A list of storage containers representing what items are available.
     * @return A list of tasks for golems to provision items from containers to players.
     */
    public static List<ProvisionTask> matchDeficits(List<PlayerDeficit> deficits, List<StorageContainer> containers) {
        List<ProvisionTask> tasks = new ArrayList<>();
        if (deficits == null || deficits.isEmpty() || containers == null || containers.isEmpty()) {
            return tasks;
        }

        // Create mutable copies of containers to track remaining amounts
        List<MutableStorageContainer> mutableContainers = containers.stream()
                .map(StorageContainer::toMutable)
                .toList();

        for (PlayerDeficit deficit : deficits) {
            int remainingDeficit = deficit.deficitAmount();

            for (MutableStorageContainer container : mutableContainers) {
                if (remainingDeficit <= 0) {
                    break; // Move to the next deficit if fulfilled
                }

                if (container.availableAmount > 0 && container.item.equals(deficit.item())) {
                    int amountToProvide = Math.min(remainingDeficit, container.availableAmount);

                    tasks.add(new ProvisionTask(deficit.playerId(), container.containerId, deficit.item(), amountToProvide));

                    remainingDeficit -= amountToProvide;
                    container.availableAmount -= amountToProvide;
                }
            }
        }

        return tasks;
    }
}
