package thaumcraft.common.tiles.crafting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InfusionStateMachineLogicTest {

    private InfusionStateMachineLogic logic;

    @BeforeEach
    void setUp() {
        logic = new InfusionStateMachineLogic();
    }

    @Test
    void testInitialState() {
        assertEquals(InfusionStateMachineLogic.State.IDLE, logic.getCurrentState());
        assertEquals(0, logic.getProgressTicks());
        assertEquals(20, logic.getCycleTime());
        assertEquals(10, logic.getEssentiaDrainInterval());
    }

    @Test
    void testStateTransitions() {
        // IDLE -> SCANNING
        logic.startScanning();
        assertEquals(InfusionStateMachineLogic.State.SCANNING, logic.getCurrentState());

        // SCANNING -> CRAFTING_ESSENTIA
        logic.startCraftingEssentia();
        assertEquals(InfusionStateMachineLogic.State.CRAFTING_ESSENTIA, logic.getCurrentState());

        // CRAFTING_ESSENTIA -> CRAFTING_ITEMS
        logic.startCraftingItems();
        assertEquals(InfusionStateMachineLogic.State.CRAFTING_ITEMS, logic.getCurrentState());

        // CRAFTING_ITEMS -> SUCCESS
        logic.complete();
        assertEquals(InfusionStateMachineLogic.State.SUCCESS, logic.getCurrentState());
    }

    @Test
    void testInterrupt() {
        logic.startScanning();
        logic.interrupt();
        assertEquals(InfusionStateMachineLogic.State.INTERRUPTED, logic.getCurrentState());

        logic.reset();
        logic.startScanning();
        logic.startCraftingEssentia();
        logic.interrupt();
        assertEquals(InfusionStateMachineLogic.State.INTERRUPTED, logic.getCurrentState());

        logic.reset();
        logic.startScanning();
        logic.startCraftingEssentia();
        logic.startCraftingItems();
        logic.interrupt();
        assertEquals(InfusionStateMachineLogic.State.INTERRUPTED, logic.getCurrentState());
    }

    @Test
    void testTickAdvancesProgress() {
        logic.startScanning();
        logic.tick();
        assertEquals(1, logic.getProgressTicks());

        logic.startCraftingEssentia(); // Resets progress
        logic.tick();
        logic.tick();
        assertEquals(2, logic.getProgressTicks());

        logic.startCraftingItems(); // Resets progress
        logic.tick();
        assertEquals(1, logic.getProgressTicks());
    }

    @Test
    void testTickDoesNotAdvanceWhenIdleOrComplete() {
        logic.tick();
        assertEquals(0, logic.getProgressTicks());

        logic.startScanning();
        logic.startCraftingEssentia();
        logic.startCraftingItems();
        logic.complete();
        logic.tick();
        assertEquals(0, logic.getProgressTicks());
    }

    @Test
    void testSetCycleTime() {
        logic.setCycleTime(10);
        assertEquals(10, logic.getCycleTime());
        assertEquals(5, logic.getEssentiaDrainInterval());

        logic.setCycleTime(1);
        assertEquals(1, logic.getCycleTime());
        assertEquals(1, logic.getEssentiaDrainInterval());
    }

    @Test
    void testIsReadyForCycle() {
        logic.setCycleTime(20);
        logic.startScanning();
        for (int i = 0; i < 10; i++) logic.tick();
        assertFalse(logic.isReadyForCycle(), "Scanning state should not be ready for cycle");

        logic.startCraftingEssentia();
        for (int i = 0; i < 9; i++) {
            logic.tick();
            assertFalse(logic.isReadyForCycle(), "Should not be ready before count delay");
        }
        logic.tick(); // 10th tick
        assertTrue(logic.isReadyForCycle(), "Should be ready on count delay");

        logic.tick();
        assertFalse(logic.isReadyForCycle(), "Should not be ready after count delay");
    }

    @Test
    void testShouldConsumeItem() {
        logic.startScanning();
        logic.startCraftingEssentia();
        logic.startCraftingItems();

        assertFalse(logic.shouldConsumeItem());
        assertEquals(5, logic.getItemConsumptionDelay());

        assertFalse(logic.shouldConsumeItem());
        assertEquals(4, logic.getItemConsumptionDelay());

        assertFalse(logic.shouldConsumeItem());
        assertEquals(3, logic.getItemConsumptionDelay());

        assertFalse(logic.shouldConsumeItem());
        assertEquals(2, logic.getItemConsumptionDelay());

        assertTrue(logic.shouldConsumeItem());
        assertEquals(0, logic.getItemConsumptionDelay()); // Resets

        // Start next item
        assertFalse(logic.shouldConsumeItem());
        assertEquals(5, logic.getItemConsumptionDelay());
    }
}
