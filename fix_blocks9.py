import re

with open("src/main/java/thaumcraft/api/blocks/ThaumcraftBlocks.java", "r") as f:
    content = f.read()

# Fix registerSimpleBlock with BlockBehaviour.Properties.of()
content = re.sub(r'registerSimpleBlock\("(\w+)"\)', r'registerSimpleBlock("\1", BlockBehaviour.Properties.of())', content)

# Fix SlabBlocks
slab_blocks = [
    "slabGreatwood", "slabSilverwood", "slabArcaneStone", "slabArcaneBrick", "slabAncient", "slabEldritch",
    "doubleSlabGreatwood", "doubleSlabSilverwood", "doubleSlabArcaneStone", "doubleSlabArcaneBrick", "doubleSlabAncient", "doubleSlabEldritch"
]
for slab in slab_blocks:
    content = re.sub(rf'public static DeferredBlock<Block> {slab} = BLOCKS.registerSimpleBlock\("(\w+)", BlockBehaviour\.Properties\.of\(\)\);',
                     rf'public static DeferredBlock<SlabBlock> {slab} = BLOCKS.registerBlock("\1", SlabBlock::new, BlockBehaviour.Properties.of());', content)

with open("src/main/java/thaumcraft/api/blocks/ThaumcraftBlocks.java", "w") as f:
    f.write(content)

with open("src/main/java/thaumcraft/common/lib/research/theorycraft/CardInfuse.java", "r") as f:
    c = f.read()
c = re.sub(r'ThaumcraftBlocks\.(\w+)\)', r'ThaumcraftBlocks.\1.get())', c)
with open("src/main/java/thaumcraft/common/lib/research/theorycraft/CardInfuse.java", "w") as f:
    f.write(c)
