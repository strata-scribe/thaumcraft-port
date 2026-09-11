package thaumcraft.common.golems.tasks;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import thaumcraft.common.golems.GolemSealLogic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global task broker for golemancy logistics.
 * Holds active task queues per dimension, sorts candidate tasks using GolemSealLogic
 * effective distance priority metric, and handles task completion and lifespan pruning.
 */
public class TaskHandler {

    public static final int TASK_LIMIT = 10000;
    public static final ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<Integer, Task>> tasks = new ConcurrentHashMap<>();

    /**
     * Registers a new task in the specified dimension's queue.
     * Prunes oldest entry if queue capacity exceeds TASK_LIMIT.
     */
    public static void addTask(ResourceKey<Level> dim, Task ticket) {
        if (dim == null || ticket == null) return;

        ConcurrentHashMap<Integer, Task> dimTasks = getTasks(dim);
        if (dimTasks.size() >= TASK_LIMIT) {
            try {
                Iterator<Task> it = dimTasks.values().iterator();
                if (it.hasNext()) {
                    it.next();
                    it.remove();
                }
            } catch (Exception ignored) {}
        }
        dimTasks.put(ticket.getId(), ticket);
    }

    /**
     * Retrieves a task by ID in the given dimension.
     */
    public static Task getTask(ResourceKey<Level> dim, int id) {
        if (dim == null) return null;
        return getTasks(dim).get(id);
    }

    /**
     * Gets or initializes the task map for a dimension.
     */
    public static ConcurrentHashMap<Integer, Task> getTasks(ResourceKey<Level> dim) {
        return tasks.computeIfAbsent(dim, k -> new ConcurrentHashMap<>());
    }

    /**
     * Returns an ArrayList of unreserved Block Tasks (type 0) sorted by effective priority distance
     * relative to the querying golem.
     */
    public static ArrayList<Task> getBlockTasksSorted(ResourceKey<Level> dim, UUID uuid, Entity golem) {
        ConcurrentHashMap<Integer, Task> tickets = getTasks(dim);
        ArrayList<Task> out = new ArrayList<>();
        if (golem == null) return out;

        for (Task ticket : tickets.values()) {
            if (ticket.isReserved()) continue;
            if (ticket.getType() != 0) continue;
            if (uuid != null && ticket.getGolemUUID() != null && !uuid.equals(ticket.getGolemUUID())) continue;

            out.add(ticket);
        }

        out.sort(Comparator.comparingDouble(t -> {
            BlockPos p = t.getPos();
            double distSq = (p != null) ? p.distToCenterSqr(golem.getX(), golem.getY(), golem.getZ()) : Double.MAX_VALUE;
            long age = 300 - t.getLifespan();
            return GolemTaskPriorityLogic.calculatePriorityScore(distSq, t.getPriority(), age);
        }));

        return out;
    }

    /**
     * Returns an ArrayList of unreserved Entity Tasks (type 1) sorted by effective priority distance
     * relative to the querying golem. Dead or removed entities automatically cause task suspension.
     */
    public static ArrayList<Task> getEntityTasksSorted(ResourceKey<Level> dim, UUID uuid, Entity golem) {
        ConcurrentHashMap<Integer, Task> tickets = getTasks(dim);
        ArrayList<Task> out = new ArrayList<>();
        if (golem == null) return out;

        for (Task ticket : tickets.values()) {
            if (ticket.isReserved()) continue;
            if (ticket.getType() != 1) continue;
            if (uuid != null && ticket.getGolemUUID() != null && !uuid.equals(ticket.getGolemUUID())) continue;

            Entity target = ticket.getEntity();
            if (target == null || !target.isAlive()) {
                ticket.setSuspended(true);
                continue;
            }

            out.add(ticket);
        }

        out.sort(Comparator.comparingDouble(t -> {
            Entity target = t.getEntity();
            double distSq = (target != null) ? target.distanceToSqr(golem.getX(), golem.getY(), golem.getZ()) : Double.MAX_VALUE;
            long age = 300 - t.getLifespan();
            return GolemTaskPriorityLogic.calculatePriorityScore(distSq, t.getPriority(), age);
        }));

        return out;
    }

    /**
     * Dispatches task completion to the originating seal.
     */
    public static void completeTask(Task task, IGolemAPI golem) {
        if (task == null || task.isCompleted() || task.isSuspended() || golem == null) {
            return;
        }

        Level world = golem.getGolemWorld();
        ISealEntity se = GolemHelper.getSealEntity(world.dimension(), task.getSealPos());
        if (se != null && se.getSeal() != null) {
            task.setCompletion(se.getSeal().onTaskCompletion(world, golem, task));
        } else {
            task.setCompletion(true);
        }
    }

    /**
     * Overload for EntityThaumcraftGolem.
     */
    public static void completeTask(Task task, EntityThaumcraftGolem golem) {
        completeTask(task, (IGolemAPI) golem);
    }

    /**
     * Ticks down task lifespans and notifies seals upon suspension or expiration.
     */
    public static void clearSuspendedOrExpiredTasks(Level world) {
        if (world == null) return;

        ConcurrentHashMap<Integer, Task> tickets = getTasks(world.dimension());
        ConcurrentHashMap<Integer, Task> remaining = new ConcurrentHashMap<>();

        for (Task ticket : tickets.values()) {
            if (!ticket.isSuspended() && ticket.getLifespan() > 0) {
                ticket.setLifespan(GolemSealLogic.decrementLifespan((short) ticket.getLifespan()));
                remaining.put(ticket.getId(), ticket);
            } else {
                ISealEntity sEnt = GolemHelper.getSealEntity(world.dimension(), ticket.getSealPos());
                if (sEnt != null && sEnt.getSeal() != null) {
                    sEnt.getSeal().onTaskSuspension(world, ticket);
                }
            }
        }

        tasks.put(world.dimension(), remaining);
    }

    /**
     * Clears all dimension queues (useful for test resets).
     */
    public static void reset() {
        tasks.clear();
    }
}
