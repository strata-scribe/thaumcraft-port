with open("src/main/java/thaumcraft/common/lib/research/theorycraft/CardInfuse.java", "r") as f:
    c = f.read()
c = c.replace("import thaumcraft.api.blocks.BlocksTC;", "import thaumcraft.api.blocks.ThaumcraftBlocks;")
c = c.replace("BlocksTC.", "ThaumcraftBlocks.")
with open("src/main/java/thaumcraft/common/lib/research/theorycraft/CardInfuse.java", "w") as f:
    f.write(c)
