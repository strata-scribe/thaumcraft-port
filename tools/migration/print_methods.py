import os
import zipfile

jar_path = "/home/frost/.gradle/caches/transforms-4/1567ba7ce7a505b3af58410b0e505ea5/transformed/neoforge-21.4.15-beta-mapped_official_1.21.4.jar"
with zipfile.ZipFile(jar_path, 'r') as z:
    for name in z.namelist():
        if "GuiGraphics" in name and "class" in name:
            print(name)

