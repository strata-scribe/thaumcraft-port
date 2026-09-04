import java.lang.reflect.Method;
public class Dump {
  public static void main(String[] args) throws Exception {
    Class<?> cls = Class.forName("net.minecraft.world.entity.Entity");
    for (Method m : cls.getDeclaredMethods()) {
      if (m.getName().toLowerCase().contains("save")) {
        System.out.println(m.getName() + ": " + java.util.Arrays.toString(m.getParameterTypes()));
      }
    }
  }
}
