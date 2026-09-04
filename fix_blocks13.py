import re

with open("src/main/java/thaumcraft/api/blocks/ThaumcraftBlocks.java", "r") as f:
    content = f.read()

content = content.replace("BlockBehaviour.Properties.of()", "() -> BlockBehaviour.Properties.of()")
# For registerBlock("name", SlabBlock::new, BlockBehaviour.Properties.of()) it should be () -> BlockBehaviour.Properties.of()
# Let's fix that too

with open("src/main/java/thaumcraft/api/blocks/ThaumcraftBlocks.java", "w") as f:
    f.write(content)
