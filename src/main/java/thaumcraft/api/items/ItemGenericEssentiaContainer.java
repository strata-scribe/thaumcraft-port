package thaumcraft.api.items;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;

public class ItemGenericEssentiaContainer extends Item implements IEssentiaContainerItem
{
	protected int base = 1;
	
	public ItemGenericEssentiaContainer(int base)
    {
        super(new Item.Properties().stacksTo(64));
        this.base = base;
    }	
    
	@Override
	public AspectList getAspects(ItemStack itemstack) {
		if (itemstack.has(DataComponents.CUSTOM_DATA)) {
			AspectList aspects = new AspectList();
			aspects.readFromNBT(itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag());
			return aspects.size() > 0 ? aspects : null;
		}
		return null;
	}

	@Override
	public void setAspects(ItemStack itemstack, AspectList aspects) {
		CompoundTag tag = itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		aspects.writeToNBT(tag);
		itemstack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
	}
	
	@Override
	public boolean ignoreContainedAspects() { return false; }
	
	@Override
	public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel world, Entity entity, net.minecraft.world.entity.EquipmentSlot slot) {
		if (!stack.has(DataComponents.CUSTOM_DATA)) {
			Aspect[] displayAspects = Aspect.aspects.values().toArray(new Aspect[]{});
			setAspects(stack, new AspectList().add(displayAspects[world.getRandom().nextInt(displayAspects.length)], base));
		}
		super.inventoryTick(stack, world, entity, slot);
	}

	@Override
	public void onCraftedBy(ItemStack stack, Player player) {
		Level world = player.level();
		if (!world.isClientSide() && !stack.has(DataComponents.CUSTOM_DATA)) {
			Aspect[] displayAspects = Aspect.aspects.values().toArray(new Aspect[]{});
			setAspects(stack, new AspectList().add(displayAspects[world.getRandom().nextInt(displayAspects.length)], base));
		}
	}
}
