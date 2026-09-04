package thaumcraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import java.lang.reflect.Method;
public class Test {
    public static void print() {
        for (Method m : Screen.class.getMethods()) {
            if (m.getName().toLowerCase().contains("tooltip")) {
                System.out.println("Screen: " + m);
            }
        }
        for (Method m : GuiGraphicsExtractor.class.getMethods()) {
            if (m.getName().toLowerCase().contains("tooltip")) {
                System.out.println("GuiGraphicsExtractor: " + m);
            }
        }
    }
}
