package thaumcraft.common.crafting.logic;

import org.junit.jupiter.api.Test;
import thaumcraft.common.crafting.logic.ArcaneWorkbenchVisSourceLogic.VisDrawResult;

import static org.junit.jupiter.api.Assertions.*;

public class ArcaneWorkbenchVisSourceLogicTest {

    @Test
    void testAuraCoversAllCost() {
        VisDrawResult result = ArcaneWorkbenchVisSourceLogic.calculateVisDraw(10.0f, 20.0f, new float[]{5.0f, 10.0f});

        assertEquals(10.0f, result.auraDrawn, 0.001f);
        assertEquals(0.0f, result.batteryDraws[0], 0.001f);
        assertEquals(0.0f, result.batteryDraws[1], 0.001f);
        assertTrue(result.isSuccess(10.0f));
    }

    @Test
    void testPartialAuraRestFromBattery() {
        VisDrawResult result = ArcaneWorkbenchVisSourceLogic.calculateVisDraw(20.0f, 10.0f, new float[]{15.0f});

        assertEquals(10.0f, result.auraDrawn, 0.001f);
        assertEquals(10.0f, result.batteryDraws[0], 0.001f);
        assertTrue(result.isSuccess(20.0f));
    }

    @Test
    void testMultipleBatteries() {
        VisDrawResult result = ArcaneWorkbenchVisSourceLogic.calculateVisDraw(30.0f, 5.0f, new float[]{10.0f, 10.0f, 10.0f});

        assertEquals(5.0f, result.auraDrawn, 0.001f);
        assertEquals(10.0f, result.batteryDraws[0], 0.001f);
        assertEquals(10.0f, result.batteryDraws[1], 0.001f);
        assertEquals(5.0f, result.batteryDraws[2], 0.001f);
        assertTrue(result.isSuccess(30.0f));
    }

    @Test
    void testNotEnoughVis() {
        VisDrawResult result = ArcaneWorkbenchVisSourceLogic.calculateVisDraw(50.0f, 10.0f, new float[]{10.0f, 10.0f});

        assertEquals(10.0f, result.auraDrawn, 0.001f);
        assertEquals(10.0f, result.batteryDraws[0], 0.001f);
        assertEquals(10.0f, result.batteryDraws[1], 0.001f);
        assertFalse(result.isSuccess(50.0f));
    }

    @Test
    void testZeroCost() {
        VisDrawResult result = ArcaneWorkbenchVisSourceLogic.calculateVisDraw(0.0f, 10.0f, new float[]{10.0f});

        assertEquals(0.0f, result.auraDrawn, 0.001f);
        assertEquals(0.0f, result.batteryDraws[0], 0.001f);
        assertTrue(result.isSuccess(0.0f));
    }

    @Test
    void testExactCostFromAuraAndBatteries() {
        VisDrawResult result = ArcaneWorkbenchVisSourceLogic.calculateVisDraw(25.0f, 10.0f, new float[]{10.0f, 5.0f});

        assertEquals(10.0f, result.auraDrawn, 0.001f);
        assertEquals(10.0f, result.batteryDraws[0], 0.001f);
        assertEquals(5.0f, result.batteryDraws[1], 0.001f);
        assertTrue(result.isSuccess(25.0f));
    }
}
