import os
import glob
for root, dirs, files in os.walk(os.path.expanduser("~/.gradle/caches")):
    for f in files:
        if f.endswith(".jar") and "minecraft" in f.lower():
            print(os.path.join(root, f))
