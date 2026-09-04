import java.lang.reflect.Method;
public class TestItem {
    public static void main(String[] args) throws Exception {
        Class<?> itemClass = Class.forName("net.minecraft.world.item.Item");
        for (Method m : itemClass.getDeclaredMethods()) {
            if (m.getName().equals("use")) {
                System.out.println(m);
            }
        }
    }
}
