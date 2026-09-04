import net.minecraft.client.gui.GuiGraphicsExtractor;
import java.lang.reflect.Method;
public class Test {
  public static void main(String[] args) {
    try {
      System.out.println("Methods in GuiGraphicsExtractor:");
      for (Method m : GuiGraphicsExtractor.class.getMethods()) {
        if (m.getName().contains("blit")) {
          System.out.println(m);
        }
      }
    } catch (Exception e) {}
  }
}
