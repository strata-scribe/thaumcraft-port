package thaumcraft.data.tags;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ThaumcraftBlockTagProviderTest {

    @Test
    public void testClassStructure() throws Exception {
        assertTrue(BlockTagsProvider.class.isAssignableFrom(ThaumcraftBlockTagProvider.class),
            "ThaumcraftBlockTagProvider must extend BlockTagsProvider");

        Constructor<?> ctor2 = ThaumcraftBlockTagProvider.class.getConstructor(
            PackOutput.class,
            CompletableFuture.class
        );
        assertNotNull(ctor2, "Constructor(PackOutput, CompletableFuture) must exist");

        Constructor<?> ctor3 = ThaumcraftBlockTagProvider.class.getConstructor(
            PackOutput.class,
            CompletableFuture.class,
            String.class
        );
        assertNotNull(ctor3, "Constructor(PackOutput, CompletableFuture, String) must exist");

        Method addTagsMethod = ThaumcraftBlockTagProvider.class.getDeclaredMethod("addTags", HolderLookup.Provider.class);
        assertNotNull(addTagsMethod, "addTags(HolderLookup.Provider) must exist");
    }
}
