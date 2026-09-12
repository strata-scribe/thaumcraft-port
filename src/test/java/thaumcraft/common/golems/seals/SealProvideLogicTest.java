package thaumcraft.common.golems.seals;

import org.junit.jupiter.api.Test;
import thaumcraft.common.golems.seals.SealProvideLogic.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SealProvideLogicTest {

    private final ItemStackRef stone = new ItemStackRef("minecraft:stone");
    private final ItemStackRef wood = new ItemStackRef("minecraft:oak_log");

    @Test
    void testExactFulfillment() {
        List<PlayerDeficit> deficits = Collections.singletonList(
                new PlayerDeficit("player1", stone, 10)
        );
        List<StorageContainer> containers = Collections.singletonList(
                new StorageContainer(1, stone, 10)
        );

        List<ProvisionTask> tasks = SealProvideLogic.matchDeficits(deficits, containers);

        assertEquals(1, tasks.size());
        assertEquals(new ProvisionTask("player1", 1, stone, 10), tasks.get(0));
    }

    @Test
    void testPartialFulfillment() {
        List<PlayerDeficit> deficits = Collections.singletonList(
                new PlayerDeficit("player1", stone, 20)
        );
        List<StorageContainer> containers = Collections.singletonList(
                new StorageContainer(1, stone, 10)
        );

        List<ProvisionTask> tasks = SealProvideLogic.matchDeficits(deficits, containers);

        assertEquals(1, tasks.size());
        assertEquals(new ProvisionTask("player1", 1, stone, 10), tasks.get(0));
    }

    @Test
    void testFulfillmentFromMultipleContainers() {
        List<PlayerDeficit> deficits = Collections.singletonList(
                new PlayerDeficit("player1", stone, 25)
        );
        List<StorageContainer> containers = Arrays.asList(
                new StorageContainer(1, stone, 10),
                new StorageContainer(2, stone, 20)
        );

        List<ProvisionTask> tasks = SealProvideLogic.matchDeficits(deficits, containers);

        assertEquals(2, tasks.size());
        assertEquals(new ProvisionTask("player1", 1, stone, 10), tasks.get(0));
        assertEquals(new ProvisionTask("player1", 2, stone, 15), tasks.get(1));
    }

    @Test
    void testCompetingDeficits() {
        List<PlayerDeficit> deficits = Arrays.asList(
                new PlayerDeficit("player1", stone, 15),
                new PlayerDeficit("player2", stone, 10)
        );
        List<StorageContainer> containers = Collections.singletonList(
                new StorageContainer(1, stone, 20)
        );

        List<ProvisionTask> tasks = SealProvideLogic.matchDeficits(deficits, containers);

        assertEquals(2, tasks.size());
        // player1 gets 15, leaving 5 for player2
        assertEquals(new ProvisionTask("player1", 1, stone, 15), tasks.get(0));
        assertEquals(new ProvisionTask("player2", 1, stone, 5), tasks.get(1));
    }

    @Test
    void testNoMatchingItems() {
        List<PlayerDeficit> deficits = Collections.singletonList(
                new PlayerDeficit("player1", stone, 10)
        );
        List<StorageContainer> containers = Collections.singletonList(
                new StorageContainer(1, wood, 50)
        );

        List<ProvisionTask> tasks = SealProvideLogic.matchDeficits(deficits, containers);

        assertTrue(tasks.isEmpty());
    }

    @Test
    void testEmptyInputs() {
        List<PlayerDeficit> deficits = Collections.singletonList(
                new PlayerDeficit("player1", stone, 10)
        );

        List<ProvisionTask> tasks1 = SealProvideLogic.matchDeficits(deficits, Collections.emptyList());
        assertTrue(tasks1.isEmpty());

        List<ProvisionTask> tasks2 = SealProvideLogic.matchDeficits(Collections.emptyList(), Collections.singletonList(new StorageContainer(1, stone, 10)));
        assertTrue(tasks2.isEmpty());
    }
}
