package thaumcraft.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.client.gui.GuiThaumonomicon;

@OnlyIn(Dist.CLIENT)
public class ClientHooks {
    public static void openThaumonomicon() {
        Minecraft.getInstance().setScreen(new GuiThaumonomicon());
    }
}
