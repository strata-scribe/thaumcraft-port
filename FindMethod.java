import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.Enumeration;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FindMethod {
    public static void main(String[] args) throws IOException {
        String userHome = System.getProperty("user.home");
        File dir = new File(userHome + "/.gradle/caches");
        search(dir);
    }

    private static void search(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                search(f);
            } else if (f.getName().endsWith(".jar")) {
                if (f.getName().contains("minecraft") || f.getName().contains("neoforge") || f.getName().contains("client-extra")) {
                    try (ZipFile zip = new ZipFile(f)) {
                        Enumeration<? extends ZipEntry> entries = zip.entries();
                        while (entries.hasMoreElements()) {
                            ZipEntry entry = entries.nextElement();
                            if (entry.getName().endsWith(".class") && entry.getName().contains("Mob") && !entry.getName().contains("Test")) {
                                // cannot read directly in basic java easily without asm, just logging interesting jars for javap
                                if (entry.getName().contains("LivingEntity") || entry.getName().contains("PathfinderMob") || entry.getName().contains("Mob") || entry.getName().contains("Monster")) {
                                    // we know it's one of these
                                }
                            }
                        }
                    } catch (Exception e) {}
                }
            }
        }
    }
}
