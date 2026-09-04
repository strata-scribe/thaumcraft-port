public class TestClass {
    public static void main(String[] args) throws Exception {
        Class<?> cls = Class.forName("net.minecraft.world.item.Item");
        for (java.lang.reflect.Method m : cls.getDeclaredMethods()) {
            if (m.getName().equals("use")) {
                System.out.println(m.getReturnType().getName());
            }
        }
    }
}
