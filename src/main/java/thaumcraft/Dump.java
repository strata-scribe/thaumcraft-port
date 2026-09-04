package thaumcraft;

import net.minecraft.client.gui.components.events.ContainerEventHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Dump {
    public static void dump() {
        try {
            System.out.println("--- DUMPING ContainerEventHandler ---");
            for (Method m : ContainerEventHandler.class.getMethods()) {
                if (m.getName().equals("mouseDragged")) {
                    System.out.println(Modifier.toString(m.getModifiers()) + " " + m.getReturnType().getSimpleName() + " " + m.getName() + "()");
                    for (Class<?> p : m.getParameterTypes()) {
                        System.out.println("  Param: " + p.getName());
                    }
                }
            }
        } catch (Exception e) {}
    }
}
