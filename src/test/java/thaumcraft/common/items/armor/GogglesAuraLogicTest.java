package thaumcraft.common.items.armor;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GogglesAuraLogicTest {

    @Test
    public void testFormatVisPercentage() {
        assertEquals("0%", GogglesAuraLogic.formatVisPercentage(0.0f, (short) 100));
        assertEquals("50%", GogglesAuraLogic.formatVisPercentage(50.4f, (short) 100));
        assertEquals("51%", GogglesAuraLogic.formatVisPercentage(50.5f, (short) 100));
        assertEquals("120%", GogglesAuraLogic.formatVisPercentage(120.0f, (short) 100));
        assertEquals("0%", GogglesAuraLogic.formatVisPercentage(50.0f, (short) 0));
        assertEquals("33%", GogglesAuraLogic.formatVisPercentage(100.0f, (short) 300));
    }

    @Test
    public void testFormatFluxPercentage() {
        assertEquals("0%", GogglesAuraLogic.formatFluxPercentage(0.0f, (short) 100));
        assertEquals("75%", GogglesAuraLogic.formatFluxPercentage(75.0f, (short) 100));
        assertEquals("200%", GogglesAuraLogic.formatFluxPercentage(100.0f, (short) 50));
        assertEquals("0%", GogglesAuraLogic.formatFluxPercentage(10.0f, (short) 0));
    }

    @Test
    public void testGetFluxWarningThreshold() {
        assertEquals(GogglesAuraLogic.AuraDangerLevel.SAFE, GogglesAuraLogic.getFluxWarningThreshold(0.0f, (short) 100));
        assertEquals(GogglesAuraLogic.AuraDangerLevel.SAFE, GogglesAuraLogic.getFluxWarningThreshold(49.9f, (short) 100));

        assertEquals(GogglesAuraLogic.AuraDangerLevel.ELEVATED, GogglesAuraLogic.getFluxWarningThreshold(50.0f, (short) 100));
        assertEquals(GogglesAuraLogic.AuraDangerLevel.ELEVATED, GogglesAuraLogic.getFluxWarningThreshold(74.9f, (short) 100));

        assertEquals(GogglesAuraLogic.AuraDangerLevel.DANGEROUS, GogglesAuraLogic.getFluxWarningThreshold(75.0f, (short) 100));
        assertEquals(GogglesAuraLogic.AuraDangerLevel.DANGEROUS, GogglesAuraLogic.getFluxWarningThreshold(150.0f, (short) 100));

        // Edge cases with zero base
        assertEquals(GogglesAuraLogic.AuraDangerLevel.SAFE, GogglesAuraLogic.getFluxWarningThreshold(0.0f, (short) 0));
        assertEquals(GogglesAuraLogic.AuraDangerLevel.DANGEROUS, GogglesAuraLogic.getFluxWarningThreshold(10.0f, (short) 0));
    }

    @Test
    public void testFormatAspectTags() {
        assertEquals("No Aspects", GogglesAuraLogic.formatAspectTags(null));
        assertEquals("No Aspects", GogglesAuraLogic.formatAspectTags(Collections.emptyMap()));

        Map<String, Integer> aspects = new LinkedHashMap<>();
        aspects.put("ignis", 15);
        aspects.put("terra", 5);

        assertEquals("ignis (15), terra (5)", GogglesAuraLogic.formatAspectTags(aspects));

        Map<String, Integer> singleAspect = new LinkedHashMap<>();
        singleAspect.put("ordo", 100);

        assertEquals("ordo (100)", GogglesAuraLogic.formatAspectTags(singleAspect));
    }
}
