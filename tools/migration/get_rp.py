import os
import glob
for root, dirs, files in os.walk("/home/frost/.gradle/caches"):
    for f in files:
        if f.endswith(".jar") and "minecraft" in f.lower():
            print(os.path.join(root, f))
