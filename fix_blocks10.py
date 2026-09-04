import re

with open("src/main/java/thaumcraft/common/lib/research/theorycraft/CardInfuse.java", "r") as f:
    c = f.read()
c = re.sub(r'ThaumcraftBlocks\.(\w+)', r'ThaumcraftBlocks.\1.get()', c)
with open("src/main/java/thaumcraft/common/lib/research/theorycraft/CardInfuse.java", "w") as f:
    f.write(c)
