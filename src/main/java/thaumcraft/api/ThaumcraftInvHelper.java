package thaumcraft.api;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

public class ThaumcraftInvHelper {

	public static class InvFilter {
		public boolean igDmg;
		public boolean igNBT;
		public boolean useOre;
		public boolean useMod;
		public boolean relaxedNBT = false;
	
		public InvFilter(boolean ignoreDamage, boolean ignoreNBT, boolean useOre, boolean useMod) {
			igDmg = ignoreDamage;
			igNBT = ignoreNBT;
			this.useOre = useOre;
			this.useMod = useMod;
		}		
		
		public InvFilter setRelaxedNBT() {
			relaxedNBT = true;
			return this;
		}
		
		public static InvFilter STRICT = new InvFilter(false,false,false,false);
		public static InvFilter BASEORE = new InvFilter(false,false,true,false);
	}

	public static IItemHandler getItemHandlerAt(Level world, BlockPos pos, Direction side) {
		BlockState state = world.getBlockState(pos);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		var resourceHandler = net.neoforged.neoforge.capabilities.Capabilities.Item.BLOCK.getCapability(world, pos, state, blockEntity, side);
		if (resourceHandler != null) {
			return IItemHandler.of(resourceHandler);
		} else {
			BlockEntity tileentity = world.getBlockEntity(pos);
			if (tileentity instanceof net.minecraft.world.Container container) {            	
				return wrapInventory(container, side);
			}
		}
		return null;
	}

	public static IItemHandler wrapInventory(net.minecraft.world.Container inventory, Direction side) {
		return inventory instanceof net.minecraft.world.WorldlyContainer worldly ? new SidedInvWrapper(worldly, side) : new InvWrapper(inventory);
	}

	public static boolean areItemStackTagsEqualRelaxed(ItemStack prime, ItemStack other) {
		if (prime.isEmpty() && other.isEmpty()) return true;
		if (prime.isEmpty() || other.isEmpty()) return false;

		net.minecraft.core.component.DataComponentMap primeComponents = prime.getComponents();
		net.minecraft.core.component.DataComponentMap otherComponents = other.getComponents();
		
		for (net.minecraft.core.component.TypedDataComponent<?> primeComp : primeComponents) {
			net.minecraft.core.component.DataComponentType<?> type = primeComp.type();
			Object primeVal = primeComp.value();
			Object otherVal = otherComponents.get(type);
			if (otherVal == null || !primeVal.equals(otherVal)) {
				return false;
			}
		}
		return true;
	}

	public static boolean areItemStacksEqualForCrafting(ItemStack stack0, Object in) {
		if (stack0 == null && in != null) return false;
		if (stack0 != null && in == null) return false;
		if (stack0 == null && in == null) return true;
		
		if (in instanceof String str) {
			net.minecraft.tags.TagKey<net.minecraft.world.item.Item> tagKey = ThaumcraftApiHelper.mapOreDictToTag(str);
			return stack0.is(tagKey);
		}
		
		if (in instanceof net.minecraft.tags.TagKey<?> tagKey) {
			if (tagKey.isFor(net.minecraft.core.registries.Registries.ITEM)) {
				return stack0.is((net.minecraft.tags.TagKey<net.minecraft.world.item.Item>) tagKey);
			}
		}
		
		if (in instanceof ItemStack otherStack) {
			boolean t1 = areItemStackTagsEqualRelaxed(otherStack, stack0);
			return t1 && areItemsEqual(otherStack, stack0);
		}
		
		return false;
	}

	public static boolean containsMatch(boolean strict, ItemStack[] inputs, List<ItemStack> targets) {
		for (ItemStack input : inputs) {
			for (ItemStack target : targets) {
				if (areItemsEqual(target, input) && (!strict || ItemStack.isSameItemSameComponents(target, input))) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean areItemsEqual(ItemStack s1, ItemStack s2) {
		if (s1.isDamageableItem() && s2.isDamageableItem()) {
			return s1.getItem() == s2.getItem();
		} else {
			return s1.getItem() == s2.getItem() && s1.getDamageValue() == s2.getDamageValue();
		}
	}

	public static boolean areItemStackTagsEqualForCrafting(ItemStack slotItem, ItemStack recipeItem) {
		return areItemStackTagsEqualRelaxed(recipeItem, slotItem);
	}

	public static ItemStack insertStackAt(Level world, BlockPos pos, Direction side, ItemStack stack, boolean simulate) {		
		IItemHandler inventory = getItemHandlerAt(world,pos,side); 		
		if (inventory!=null) {			
			return ItemHandlerHelper.insertItemStacked(inventory, stack, simulate);
		}
		return stack;
	}
	
	public static ItemStack hasRoomFor(Level world, BlockPos pos, Direction side, ItemStack stack) {
		ItemStack testStack = insertStackAt(world, pos, side, stack.copy(), true);
		if (testStack.isEmpty()) {
			return stack.copy();
		}
		testStack.setCount(stack.getCount() - testStack.getCount()); 
		return testStack;
	}

	public static boolean hasRoomForSome(Level world, BlockPos pos, Direction side, ItemStack stack) {
		ItemStack testStack = insertStackAt(world, pos, side, stack.copy(), true);
		return stack.getCount()==0 || testStack.getCount()!=stack.getCount();
	}
	
	public static boolean hasRoomForAll(Level world, BlockPos pos, Direction side, ItemStack stack) {
		return insertStackAt(world, pos, side, stack.copy(), true).isEmpty();
	}

	public static int countTotalItemsIn(IItemHandler inventory, ItemStack stack, InvFilter filter) {
		int count = 0;    	
		if (inventory!=null) {			
			for (int a=0;a<inventory.getSlots();a++) {
				if (areItemStacksEqual(stack,inventory.getStackInSlot(a),filter)) {
					count+=inventory.getStackInSlot(a).getCount();
				}
			}    	
		}    	
		return count;
	}

	public static int countTotalItemsIn(Level world, BlockPos pos, Direction side, ItemStack stack, InvFilter filter) {
		return countTotalItemsIn(getItemHandlerAt(world,pos,side),stack,filter);
	}

	public static boolean areItemStacksEqual(ItemStack stack0, ItemStack stack1, InvFilter filter) {
		if (stack0.isEmpty() && stack1.isEmpty()) return true;
		if (stack0.isEmpty() || stack1.isEmpty()) return false;

		if (filter.useMod) {
			String m1 = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack0.getItem()).getNamespace();
			String m2 = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack1.getItem()).getNamespace();
			return m1.equals(m2);
		}

		if (filter.useOre && !stack0.isEmpty()) {
			java.util.Set<net.minecraft.tags.TagKey<net.minecraft.world.item.Item>> tags0 = stack0.tags().collect(java.util.stream.Collectors.toSet());
			if (!tags0.isEmpty()) {
				java.util.Set<net.minecraft.tags.TagKey<net.minecraft.world.item.Item>> tags1 = stack1.tags().collect(java.util.stream.Collectors.toSet());
				tags0.retainAll(tags1);
				if (!tags0.isEmpty()) {
					return true;
				}
			}
		}

		boolean t1 = true;
		if (!filter.igNBT) {
			if (filter.relaxedNBT) {
				t1 = areItemStackTagsEqualRelaxed(stack0, stack1);
			} else {
				t1 = ItemStack.isSameItemSameComponents(stack0, stack1);
			}
		}

		boolean t2 = true;
		if (!filter.igDmg) {
			int dmg0 = stack0.getDamageValue();
			int dmg1 = stack1.getDamageValue();
			if (dmg0 != 32767 && dmg1 != 32767) {
				t2 = dmg0 == dmg1;
			}
		}

		return stack0.getItem() == stack1.getItem() && t2 && t1;
	}
}
