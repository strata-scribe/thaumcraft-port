import java.io.File;
import java.nio.file.Files;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class FindMethod {
    public static void main(String[] args) throws Exception {
        search(new File("src/main/java"));
    }

    static void search(File f) throws Exception {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                search(c);
            }
        } else if (f.getName().endsWith(".java")) {
            String content = new String(Files.readAllBytes(f.toPath()));
            if (content.contains("getAuraBase")) {
                System.out.println(f.getAbsolutePath());
            }
        }
    }
}
