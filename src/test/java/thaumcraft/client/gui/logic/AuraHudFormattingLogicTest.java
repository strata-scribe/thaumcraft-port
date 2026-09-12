package thaumcraft.client.gui.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuraHudFormattingLogicTest {

    @Test
    public void testCalculateBarPercentage() {
        assertEquals(0.5f, AuraHudFormattingLogic.calculateBarPercentage(50.0f, 100.0f), 0.001f);
        assertEquals(0.0f, AuraHudFormattingLogic.calculateBarPercentage(0.0f, 100.0f), 0.001f);
        assertEquals(1.0f, AuraHudFormattingLogic.calculateBarPercentage(150.0f, 100.0f), 0.001f);
        assertEquals(0.0f, AuraHudFormattingLogic.calculateBarPercentage(50.0f, 0.0f), 0.001f);
        assertEquals(0.0f, AuraHudFormattingLogic.calculateBarPercentage(50.0f, -10.0f), 0.001f);
    }

    @Test
    public void testGetFluxBarColor() {
        int normalColor = 0xFFFFFF;
        int overflowColor = 0x800080;
        assertEquals(normalColor, AuraHudFormattingLogic.getFluxBarColor(50.0f, 100.0f, normalColor, overflowColor));
        assertEquals(normalColor, AuraHudFormattingLogic.getFluxBarColor(100.0f, 100.0f, normalColor, overflowColor));
        assertEquals(overflowColor, AuraHudFormattingLogic.getFluxBarColor(150.0f, 100.0f, normalColor, overflowColor));
        assertEquals(overflowColor, AuraHudFormattingLogic.getFluxBarColor(10.0f, 0.0f, normalColor, overflowColor));
    }

    @Test
    public void testFormatLocalizedText() {
        assertEquals("hud.thaumcraft.vis: 50", AuraHudFormattingLogic.formatLocalizedText("hud.thaumcraft.vis", 50.4f));
        assertEquals("hud.thaumcraft.vis: 51", AuraHudFormattingLogic.formatLocalizedText("hud.thaumcraft.vis", 50.6f));
    }

    @Test
    public void testCalculateBarX() {
        assertEquals(90, AuraHudFormattingLogic.calculateBarX(200, 20, 0));
        assertEquals(100, AuraHudFormattingLogic.calculateBarX(200, 20, 10));
        assertEquals(80, AuraHudFormattingLogic.calculateBarX(200, 20, -10));
    }

    @Test
    public void testCalculateBarY() {
        assertEquals(200, AuraHudFormattingLogic.calculateBarY(200, 0));
        assertEquals(180, AuraHudFormattingLogic.calculateBarY(200, -20));
        assertEquals(220, AuraHudFormattingLogic.calculateBarY(200, 20));
    }

    @Test
    public void testCalculateBarWidth() {
        assertEquals(50, AuraHudFormattingLogic.calculateBarWidth(0.5f, 100));
        assertEquals(0, AuraHudFormattingLogic.calculateBarWidth(0.0f, 100));
        assertEquals(100, AuraHudFormattingLogic.calculateBarWidth(1.0f, 100));
    }
}
