import zipfile
import re

def extract_methods(jar_path, class_name):
    try:
        with zipfile.ZipFile(jar_path, 'r') as z:
            class_content = z.read(class_name)
            # Find method names using basic string extraction since we don't have javap
            strings = re.findall(b'[a-zA-Z_$][a-zA-Z0-9_$]+', class_content)
            return set([s.decode('utf-8') for s in strings])
    except Exception as e:
        return str(e)

import glob
import os
jars = glob.glob(os.path.expanduser("~/.gradle/caches/transforms-4/*/transformed/*mapped_official_1.21.4.jar"))
if jars:
    print(extract_methods(jars[0], 'net/minecraft/client/gui/GuiGraphicsExtractor.class'))
else:
    print("No jars found")
