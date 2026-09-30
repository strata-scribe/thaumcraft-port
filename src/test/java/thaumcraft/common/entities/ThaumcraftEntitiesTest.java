package thaumcraft.common.entities;

import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import thaumcraft.Thaumcraft;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ThaumcraftEntitiesTest {

    private static Class<?> entityClass;
    private static Map<String, String> fieldToIdMap;

    @BeforeAll
    public static void setup() throws Exception {
        // Load class without triggering static initialization that requires active FML environment
        entityClass = Class.forName("thaumcraft.common.entities.ThaumcraftEntities", false,
                ThaumcraftEntitiesTest.class.getClassLoader());

        // Read bytecode via ASM to inspect registered ID paths directly from <clinit> bytecode
        Map<String, String> mappings = new LinkedHashMap<>();
        try (InputStream is = entityClass.getResourceAsStream("/thaumcraft/common/entities/ThaumcraftEntities.class")) {
            assertNotNull(is, "ThaumcraftEntities.class bytecode resource must be present");
            ClassReader cr = new ClassReader(is);
            cr.accept(new ClassVisitor(Opcodes.ASM9) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    if ("<clinit>".equals(name)) {
                        return new MethodVisitor(Opcodes.ASM9) {
                            String lastLdc = null;
                            @Override
                            public void visitLdcInsn(Object value) {
                                if (value instanceof String s) {
                                    lastLdc = s;
                                }
                            }
                            @Override
                            public void visitFieldInsn(int opcode, String owner, String fieldName, String descriptor) {
                                if (opcode == Opcodes.PUTSTATIC && lastLdc != null) {
                                    mappings.put(fieldName, lastLdc);
                                }
                            }
                        };
                    }
                    return null;
                }
            }, 0);
        }
        fieldToIdMap = Collections.unmodifiableMap(mappings);
    }

    @Test
    @DisplayName("ThaumcraftEntities class exists and contains public static final ENTITIES DeferredRegister")
    public void testDeferredRegister() throws Exception {
        assertNotNull(entityClass, "ThaumcraftEntities class must exist");
        Field field = entityClass.getField("ENTITIES");
        assertNotNull(field, "ENTITIES field must exist");
        assertTrue(Modifier.isPublic(field.getModifiers()), "ENTITIES must be public");
        assertTrue(Modifier.isStatic(field.getModifiers()), "ENTITIES must be static");
        assertTrue(Modifier.isFinal(field.getModifiers()), "ENTITIES must be final");
        assertEquals(DeferredRegister.class, field.getType(), "ENTITIES must be of type DeferredRegister");

        // Verify registered namespace is thaumcraft
        assertEquals("thaumcraft", fieldToIdMap.get("ENTITIES"), "ENTITIES registry namespace must be 'thaumcraft'");
    }

    @Test
    @DisplayName("Verify FLUX_RIFT DeferredHolder field and registry ID")
    public void testFluxRiftField() throws Exception {
        assertDeferredHolderField("FLUX_RIFT", "EntityFluxRift", "flux_rift");
    }

    @Test
    @DisplayName("Verify TAINT_SEED DeferredHolder field and registry ID")
    public void testTaintSeedField() throws Exception {
        assertDeferredHolderField("TAINT_SEED", "EntityTaintSeed", "taint_seed");
    }

    @Test
    @DisplayName("Verify PECH DeferredHolder field and registry ID")
    public void testPechField() throws Exception {
        assertDeferredHolderField("PECH", "EntityPech", "pech");
    }

    @Test
    @DisplayName("Verify TAINTACLE DeferredHolder field and registry ID")
    public void testTaintacleField() throws Exception {
        assertDeferredHolderField("TAINTACLE", "EntityTaintacle", "taintacle");
    }

    @Test
    @DisplayName("Verify TAINT_CRAWLER DeferredHolder field and registry ID")
    public void testTaintCrawlerField() throws Exception {
        assertDeferredHolderField("TAINT_CRAWLER", "EntityTaintCrawler", "taint_crawler");
    }

    @Test
    @DisplayName("Verify ELDRITCH_CRAB DeferredHolder field and registry ID")
    public void testEldritchCrabField() throws Exception {
        assertDeferredHolderField("ELDRITCH_CRAB", "EntityEldritchCrab", "eldritch_crab");
    }

    @Test
    @DisplayName("Verify INHABITED_ZOMBIE DeferredHolder field and registry ID")
    public void testInhabitedZombieField() throws Exception {
        assertDeferredHolderField("INHABITED_ZOMBIE", "EntityInhabitedZombie", "inhabited_zombie");
    }

    @Test
    @DisplayName("Verify FOCUS_RIFT DeferredHolder field and registry ID")
    public void testFocusRiftField() throws Exception {
        assertDeferredHolderField("FOCUS_RIFT", "EntityFocusRift", "focus_rift");
    }

    @Test
    @DisplayName("Verify all 8 entity holder fields are present in registry mapping")
    public void testAllHoldersPresent() {
        String[] expectedHolders = {
                "FLUX_RIFT", "TAINT_SEED", "PECH", "TAINTACLE",
                "TAINT_CRAWLER", "ELDRITCH_CRAB", "INHABITED_ZOMBIE", "FOCUS_RIFT"
        };
        for (String holder : expectedHolders) {
            assertTrue(fieldToIdMap.containsKey(holder), "Missing holder registration for " + holder);
        }
    }

    private void assertDeferredHolderField(String fieldName, String entitySimpleName, String expectedId) throws Exception {
        Field field = entityClass.getField(fieldName);
        assertNotNull(field, fieldName + " field must exist");
        assertTrue(Modifier.isPublic(field.getModifiers()), fieldName + " must be public");
        assertTrue(Modifier.isStatic(field.getModifiers()), fieldName + " must be static");
        assertTrue(Modifier.isFinal(field.getModifiers()), fieldName + " must be final");
        assertEquals(DeferredHolder.class, field.getType(), fieldName + " must be DeferredHolder");

        // Verify generic type matches expected entity class
        String genericTypeName = field.getGenericType().getTypeName();
        assertTrue(genericTypeName.contains(entitySimpleName),
                fieldName + " generic type should contain " + entitySimpleName + " but was " + genericTypeName);

        // Verify registry ID
        assertEquals(expectedId, fieldToIdMap.get(fieldName),
                fieldName + " registry ID must be '" + expectedId + "'");
    }
}
