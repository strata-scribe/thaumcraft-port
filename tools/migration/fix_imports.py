import os
import glob

def replace_in_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    replacements = {
        'net.minecraft.block.properties.IProperty': 'net.minecraft.world.level.block.state.properties.Property',
        'net.minecraft.block.state.IBlockState': 'net.minecraft.world.level.block.state.BlockState',
        'net.minecraft.util.EnumFacing': 'net.minecraft.core.Direction',
        'net.minecraft.util.math.BlockPos': 'net.minecraft.core.BlockPos',
        'net.minecraft.entity.player.EntityPlayer': 'net.minecraft.world.entity.player.Player',
        'net.minecraft.world.World': 'net.minecraft.world.level.Level',
        'net.minecraft.item.ItemStack': 'net.minecraft.world.item.ItemStack',
        'IBlockState': 'BlockState',
        'EnumFacing': 'Direction',
        'IProperty': 'Property',
        'EntityPlayer': 'Player',
        'World': 'Level',
        'net.minecraft.block.material.Material': 'net.minecraft.world.level.material.Material',
        'net.minecraft.block.Block': 'net.minecraft.world.level.block.Block',
        'net.minecraft.entity.Entity': 'net.minecraft.world.entity.Entity',
        'net.minecraft.entity.EntityLivingBase': 'net.minecraft.world.entity.LivingEntity',
        'EntityLivingBase': 'LivingEntity',
        'net.minecraft.item.Item': 'net.minecraft.world.item.Item',
        'net.minecraft.util.ResourceLocation': 'net.minecraft.resources.ResourceLocation',
        'net.minecraft.init.Blocks': 'net.minecraft.world.level.block.Blocks',
        'net.minecraft.util.math.MathHelper': 'net.minecraft.util.Mth',
        'MathHelper': 'Mth',
        'net.minecraft.util.math.Vec3d': 'net.minecraft.world.phys.Vec3',
        'Vec3d': 'Vec3',
        'net.minecraft.util.math.AxisAlignedBB': 'net.minecraft.world.phys.AABB',
        'AxisAlignedBB': 'AABB'
    }

    for old, new in replacements.items():
        content = content.replace(old, new)

    with open(filepath, 'w') as f:
        f.write(content)

for root, _, files in os.walk('src/main/java/thaumcraft/common/lib/utils/'):
    for file in files:
        if file.endswith('.java'):
            replace_in_file(os.path.join(root, file))

print("Imports updated")
