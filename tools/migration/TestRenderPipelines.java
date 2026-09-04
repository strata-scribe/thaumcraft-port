import java.lang.reflect.Field;
import net.minecraft.client.renderer.RenderPipelines;

public class TestRenderPipelines {
    public static void main(String[] args) {
        for (Field f : RenderPipelines.class.getDeclaredFields()) {
            System.out.println(f.getName());
        }
    }
}
