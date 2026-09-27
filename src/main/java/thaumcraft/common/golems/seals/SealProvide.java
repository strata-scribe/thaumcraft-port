package thaumcraft.common.golems.seals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.tasks.TaskHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SealProvide extends SealFiltered implements ISealConfigArea {

    private int delay;
    private final Identifier icon = Identifier.fromNamespaceAndPath("thaumcraft", "items/seals/seal_provide");

    public SealProvide() {
        super();
        this.delay = new Random().nextInt(20);
    }

    @Override
    public String getKey() {
        return "thaumcraft:provide";
    }

    @Override
    public void tickSeal(Level world, ISealEntity seal) {
        if (world == null || seal == null || seal.isStoppedByRedstone(world)) return;
        if (delay++ % 20 != 0) return;

        AABB area = GolemHelper.getBoundsForArea(seal);
        List<Player> players = world.getEntitiesOfClass(Player.class, area);

        List<SealProvideLogic.PlayerDeficit> deficits = new ArrayList<>();

        for (Player player : players) {
            for (int i = 0; i < filter.size(); i++) {
                ItemStack filterStack = filter.get(i);
                int size = filterSize.get(i);
                if (filterStack != null && !filterStack.isEmpty() && size > 0) {
                    int playerHas = 0;
                    for (int j = 0; j < player.getInventory().getContainerSize(); j++) {
                        ItemStack invStack = player.getInventory().getItem(j);
                        if (ItemStack.isSameItemSameComponents(filterStack, invStack)) {
                            playerHas += invStack.getCount();
                        }
                    }
                    if (playerHas < size) {
                        String itemId = BuiltInRegistries.ITEM.getKey(filterStack.getItem()).toString();
                        deficits.add(new SealProvideLogic.PlayerDeficit(
                                player.getStringUUID(),
                                new SealProvideLogic.ItemStackRef(itemId),
                                size - playerHas
                        ));
                    }
                }
            }
        }

        if (deficits.isEmpty()) return;

        List<SealProvideLogic.StorageContainer> containers = new ArrayList<>();
        for (int i = 0; i < filter.size(); i++) {
            ItemStack filterStack = filter.get(i);
            if (filterStack != null && !filterStack.isEmpty()) {
                String itemId = BuiltInRegistries.ITEM.getKey(filterStack.getItem()).toString();
                containers.add(new SealProvideLogic.StorageContainer(
                        seal.getSealPos().pos.hashCode(),
                        new SealProvideLogic.ItemStackRef(itemId),
                        9999
                ));
            }
        }

        List<SealProvideLogic.ProvisionTask> provTasks = SealProvideLogic.matchDeficits(deficits, containers);

        for (SealProvideLogic.ProvisionTask pt : provTasks) {
            Player targetPlayer = null;
            for (Player p : players) {
                if (p.getStringUUID().equals(pt.playerId())) {
                    targetPlayer = p;
                    break;
                }
            }
            if (targetPlayer != null) {
                Task task = new Task(seal.getSealPos(), targetPlayer);
                task.setPriority(seal.getPriority());
                TaskHandler.addTask(world.dimension(), task);
            }
        }
    }

    @Override
    public boolean canPlaceAt(Level world, BlockPos pos, Direction side) {
        return true;
    }

    @Override
    public void onTaskStarted(Level world, IGolemAPI golem, Task task) {}

    @Override
    public boolean onTaskCompletion(Level world, IGolemAPI golem, Task task) {
        if (task != null) {
            task.setSuspended(true);
        }
        return true;
    }

    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        return true;
    }

    @Override
    public Identifier getSealIcon() {
        return icon;
    }

    @Override
    public int[] getGuiCategories() {
        return new int[]{2, 1, 0, 4};
    }

    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return null;
    }

    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return null;
    }
}
