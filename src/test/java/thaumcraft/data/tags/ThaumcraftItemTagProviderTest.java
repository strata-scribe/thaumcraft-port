package thaumcraft.data.tags;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ThaumcraftItemTagProviderTest {

    @Test
    public void testClassHierarchy() {
        assertTrue(ItemTagsProvider.class.isAssignableFrom(ThaumcraftItemTagProvider.class),
            "ThaumcraftItemTagProvider must extend ItemTagsProvider");
    }

    @Test
    public void testConstructorTwoArgs() throws Exception {
        Constructor<?> ctor = ThaumcraftItemTagProvider.class.getConstructor(
            PackOutput.class,
            CompletableFuture.class
        );
        assertNotNull(ctor, "Constructor(PackOutput, CompletableFuture) must exist");
        assertTrue(Modifier.isPublic(ctor.getModifiers()), "Constructor(PackOutput, CompletableFuture) must be public");
    }

    @Test
    public void testConstructorThreeArgs() throws Exception {
        Constructor<?> ctor = ThaumcraftItemTagProvider.class.getConstructor(
            PackOutput.class,
            CompletableFuture.class,
            String.class
        );
        assertNotNull(ctor, "Constructor(PackOutput, CompletableFuture, String) must exist");
        assertTrue(Modifier.isPublic(ctor.getModifiers()), "Constructor(PackOutput, CompletableFuture, String) must be public");
    }

    @Test
    public void testAddTagsMethodExists() throws Exception {
        Method addTagsMethod = ThaumcraftItemTagProvider.class.getDeclaredMethod("addTags", HolderLookup.Provider.class);
        assertNotNull(addTagsMethod, "addTags(HolderLookup.Provider) must exist");
        assertTrue(Modifier.isProtected(addTagsMethod.getModifiers()), "addTags must be protected");
        assertEquals(Void.TYPE, addTagsMethod.getReturnType(), "addTags must return void");
    }
}
