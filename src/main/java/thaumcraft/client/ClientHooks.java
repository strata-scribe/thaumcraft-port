package thaumcraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.client.gui.GuiThaumonomicon;

@OnlyIn(Dist.CLIENT)
public class ClientHooks {

    public static void setScreen(Screen screen) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;

        // 1. Try MC 26.2+ via mc.gui.setScreen(screen)
        try {
            java.lang.reflect.Field guiField = Minecraft.class.getField("gui");
            Object gui = guiField.get(mc);
            if (gui != null) {
                java.lang.reflect.Method setScreenMethod = gui.getClass().getMethod("setScreen", Screen.class);
                setScreenMethod.invoke(gui, screen);
                return;
            }
        } catch (Throwable ignored) {}

        // 2. Try MC 26.1.x via mc.setScreen(screen)
        try {
            java.lang.reflect.Method setScreenMethod = Minecraft.class.getMethod("setScreen", Screen.class);
            setScreenMethod.invoke(mc, screen);
            return;
        } catch (Throwable ignored) {}

        // 3. Fallback: mc.setScreenAndShow(screen)
        try {
            java.lang.reflect.Method setScreenAndShowMethod = Minecraft.class.getMethod("setScreenAndShow", Screen.class);
            setScreenAndShowMethod.invoke(mc, screen);
        } catch (Throwable t) {
            thaumcraft.Thaumcraft.LOGGER.error("Failed to set screen: " + screen, t);
        }
    }

    public static void openThaumonomicon() {
        setScreen(new GuiThaumonomicon());
    }
}

