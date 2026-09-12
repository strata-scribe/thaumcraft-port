package thaumcraft.common.golems.seals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Pure Java logic class for Golem Stock Seals.
 * Decoupled from Minecraft/Forge to allow for fast, reliable unit testing.
 */
public class SealStockLogic {

    public record Pos(int x, int y, int z) implements Comparable<Pos> {
        @Override
        public int compareTo(Pos o) {
            if (this.y != o.y) return Integer.compare(this.y, o.y);
            if (this.x != o.x) return Integer.compare(this.x, o.x);
            return Integer.compare(this.z, o.z);
        }
    }

    public record ItemStack(String itemId, int amount) {}

    public record ItemQuota(String itemId, int targetAmount) {}

    public record FetchTask(Pos containerPos, String itemId, int fetchAmount) {}

    public static List<FetchTask> evaluateQuotasAndDispatchTasks(Pos containerPos, List<ItemStack> containerInventory, List<ItemQuota> quotas) {
        if (quotas == null || quotas.isEmpty()) {
            return new ArrayList<>();
        }

        List<FetchTask> tasks = new ArrayList<>();

        List<ItemStack> inventory = containerInventory == null ? new ArrayList<>() : containerInventory;

        for (ItemQuota quota : quotas) {
            if (quota == null || quota.itemId() == null || quota.targetAmount() <= 0) continue;

            int currentAmount = 0;
            for (ItemStack stack : inventory) {
                if (stack != null && quota.itemId().equals(stack.itemId())) {
                    currentAmount += stack.amount();
                }
            }

            int amountToFetch = quota.targetAmount() - currentAmount;
            if (amountToFetch > 0) {
                tasks.add(new FetchTask(containerPos, quota.itemId(), amountToFetch));
            }
        }

        tasks.sort(Comparator.comparing(FetchTask::itemId));
        return tasks;
    }
}
