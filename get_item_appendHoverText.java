import java.lang.reflect.Method;
public class get_item_appendHoverText {
    public static void main(String[] args) throws Exception {
        Class<?> clazz = Class.forName("net.minecraft.world.item.Item");
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equals("appendHoverText")) {
                System.out.println(m);
            }
        }
    }
}
