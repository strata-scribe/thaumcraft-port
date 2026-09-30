package thaumcraft.data;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.junit.jupiter.api.Test;
import thaumcraft.Thaumcraft;

import static org.junit.jupiter.api.Assertions.*;

public class DataGeneratorsTest {

    @Test
    public void testClassExists() {
        assertNotNull(DataGenerators.class, "DataGenerators class must exist");
    }

    @Test
    public void testEventBusSubscriberAnnotation() {
        assertTrue(DataGenerators.class.isAnnotationPresent(EventBusSubscriber.class),
            "DataGenerators must be annotated with @EventBusSubscriber");

        EventBusSubscriber annotation = DataGenerators.class.getAnnotation(EventBusSubscriber.class);
        assertEquals(Thaumcraft.MODID, annotation.modid(),
            "modid must match Thaumcraft.MODID");
    }

    @Test
    public void testGatherDataMethodStructure() throws Exception {
        Method method = DataGenerators.class.getDeclaredMethod("gatherData", GatherDataEvent.class);
        assertNotNull(method, "gatherData(GatherDataEvent) must exist");

        assertTrue(Modifier.isPublic(method.getModifiers()), "gatherData must be public");
        assertTrue(Modifier.isStatic(method.getModifiers()), "gatherData must be static");
        assertEquals(Void.TYPE, method.getReturnType(), "gatherData return type must be void");

        assertTrue(method.isAnnotationPresent(SubscribeEvent.class),
            "gatherData must be annotated with @SubscribeEvent");
    }
}
