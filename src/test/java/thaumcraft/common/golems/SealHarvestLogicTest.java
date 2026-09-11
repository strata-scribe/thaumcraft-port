package thaumcraft.common.golems;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SealHarvestLogicTest {

    @Test
    @DisplayName("isMatureCrop should return true only for fully grown crops")
    void testIsMatureCrop() {
        SealHarvestLogic.Pos pos = new SealHarvestLogic.Pos(0, 0, 0);

        // Mature crop
        SealHarvestLogic.CropState mature = new SealHarvestLogic.CropState(pos, true, 7, 7);
        assertTrue(SealHarvestLogic.isMatureCrop(mature));

        // Over-mature crop (just in case)
        SealHarvestLogic.CropState overMature = new SealHarvestLogic.CropState(pos, true, 8, 7);
        assertTrue(SealHarvestLogic.isMatureCrop(overMature));

        // Immature crop
        SealHarvestLogic.CropState immature = new SealHarvestLogic.CropState(pos, true, 3, 7);
        assertFalse(SealHarvestLogic.isMatureCrop(immature));

        // Not a crop
        SealHarvestLogic.CropState notCrop = new SealHarvestLogic.CropState(pos, false, 7, 7);
        assertFalse(SealHarvestLogic.isMatureCrop(notCrop));

        // Null crop
        assertFalse(SealHarvestLogic.isMatureCrop(null));
    }

    @Test
    @DisplayName("scanAndQueueHarvestTasks should filter and prioritize mature crops")
    void testScanAndQueueHarvestTasks() {
        SealHarvestLogic.Pos pos1 = new SealHarvestLogic.Pos(0, 0, 0);
        SealHarvestLogic.Pos pos2 = new SealHarvestLogic.Pos(1, 0, 0);
        SealHarvestLogic.Pos pos3 = new SealHarvestLogic.Pos(0, 1, 0);
        SealHarvestLogic.Pos pos4 = new SealHarvestLogic.Pos(0, 0, 1);

        SealHarvestLogic.CropState crop1 = new SealHarvestLogic.CropState(pos1, true, 7, 7); // Mature
        SealHarvestLogic.CropState crop2 = new SealHarvestLogic.CropState(pos2, true, 3, 7); // Immature
        SealHarvestLogic.CropState crop3 = new SealHarvestLogic.CropState(pos3, true, 7, 7); // Mature
        SealHarvestLogic.CropState crop4 = new SealHarvestLogic.CropState(pos4, false, 7, 7); // Not crop

        List<SealHarvestLogic.CropState> crops = Arrays.asList(crop2, crop3, crop1, crop4);
        List<SealHarvestLogic.HarvestTask> tasks = SealHarvestLogic.scanAndQueueHarvestTasks(crops);

        assertEquals(2, tasks.size(), "Should only queue tasks for mature crops");

        // Should be sorted deterministically: pos1 (0,0,0) comes before pos3 (0,1,0) based on Pos.compareTo
        assertEquals(pos1, tasks.get(0).pos(), "Task 1 position should be pos1");
        assertTrue(tasks.get(0).queueReplant(), "Task 1 should queue replant");

        assertEquals(pos3, tasks.get(1).pos(), "Task 2 position should be pos3");
        assertTrue(tasks.get(1).queueReplant(), "Task 2 should queue replant");
    }

    @Test
    @DisplayName("scanAndQueueHarvestTasks should handle null and empty lists gracefully")
    void testScanAndQueueHarvestTasksEmptyOrNull() {
        assertTrue(SealHarvestLogic.scanAndQueueHarvestTasks(null).isEmpty(), "Null list should return empty list");
        assertTrue(SealHarvestLogic.scanAndQueueHarvestTasks(Collections.emptyList()).isEmpty(), "Empty list should return empty list");
    }
}
