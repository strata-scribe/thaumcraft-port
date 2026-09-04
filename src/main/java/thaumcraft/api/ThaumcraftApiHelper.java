package thaumcraft.api;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.items.ItemGenericEssentiaContainer;
import thaumcraft.api.items.ItemsTC;



public class ThaumcraftApiHelper {
	
	public static Attribute CHAMPION_MOD = (new RangedAttribute("tc.mobmod", -2D, -2D, 100D)).setSyncable(true);
	
	/**
	 * @deprecated Use {@link ThaumcraftInvHelper#areItemsEqual(ItemStack,ItemStack)} instead
	 */
	public static boolean areItemsEqual(ItemStack s1,ItemStack s2)
	{
		return ThaumcraftInvHelper.areItemsEqual(s1, s2);
	}
		
	/**
	 * @deprecated Use {@link InventoryHelper#containsMatch(boolean,ItemStack[],List<ItemStack>)} instead
	 */
	public static boolean containsMatch(boolean strict, ItemStack[] inputs, List<ItemStack> targets)
	{
		return ThaumcraftInvHelper.containsMatch(strict, inputs, targets);
	}
	
	/**
	 * @deprecated Use {@link ThaumcraftInvHelper#areItemStacksEqualForCrafting(ItemStack,Object)} instead
	 */
	public static boolean areItemStacksEqualForCrafting(ItemStack stack0, Object in)
	{
		return ThaumcraftInvHelper.areItemStacksEqualForCrafting(stack0, in);
	}
	
	/**
	 * @deprecated Use {@link ThaumcraftInvHelper#areItemStackTagsEqualForCrafting(ItemStack,ItemStack)} instead
	 */
	public static boolean areItemStackTagsEqualForCrafting(ItemStack slotItem,ItemStack recipeItem)
	{
		return ThaumcraftInvHelper.areItemStackTagsEqualForCrafting(slotItem, recipeItem);
	}
   
    
    public static BlockEntity getConnectableTile(BlockGetter world, BlockPos pos, Direction face) {
		BlockEntity te = world.getBlockEntity(pos.relative(face));
		if (te instanceof IEssentiaTransport && ((IEssentiaTransport)te).isConnectable(face.getOpposite())) 
			return te;
		else
			return null;
	}  
    
	public static HitResult rayTraceIgnoringSource(Level world, Vec3 v1, Vec3 v2, 
			boolean bool1, boolean bool2, boolean bool3)
	{
		net.minecraft.world.level.ClipContext ctx = new net.minecraft.world.level.ClipContext(
				v1,
				v2,
				bool1 ? net.minecraft.world.level.ClipContext.Block.COLLIDER : net.minecraft.world.level.ClipContext.Block.OUTLINE,
				bool2 ? net.minecraft.world.level.ClipContext.Fluid.ANY : net.minecraft.world.level.ClipContext.Fluid.NONE,
				(net.minecraft.world.entity.Entity) null
		);
		return world.clip(ctx);
	}
	
	public static Object getNBTDataFromId(CompoundTag nbt, byte id, String key) {
		switch (id) {
		case 1: return nbt.getByte(key).orElse((byte)0);
		case 2: return nbt.getShort(key).orElse((short)0);
		case 3: return nbt.getInt(key).orElse(0);
		case 4: return nbt.getLong(key).orElse(0L);
		case 5: return nbt.getFloat(key).orElse(0.0f);
		case 6: return nbt.getDouble(key).orElse(0.0d);
		case 7: return nbt.getByteArray(key).orElse(new byte[0]);
		case 8: return nbt.getString(key).orElse("");
		case 9: return nbt.getList(key).orElse(new ListTag());
		case 10: return nbt.get(key);
		case 11: return nbt.getIntArray(key).orElse(new int[0]);
		default: return null;
		}
	}
	
	public static int setByteInInt(int data, byte b, int index)
	{
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(0,data);
		bb.put(index, b);
	    return bb.getInt(0);
	}
	
	public static byte getByteInInt(int data, int index) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(0,data);
		return bb.get(index);
	}	
	
	public static long setByteInLong(long data, byte b, int index)
	{
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.putLong(0,data);
		bb.put(index, b);
	    return bb.getLong(0);
	}
	
	public static byte getByteInLong(long data, int index) {
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.putLong(0,data);
		return bb.get(index);
	}	
	
	public static int setNibbleInInt(int data, int nibble, int nibbleIndex)
	{
	    int shift = nibbleIndex * 4;
	    return (data & ~(0xf << shift)) | (nibble << shift);
	}
	
	public static int getNibbleInInt(int data, int nibbleIndex) {
		return (data >> (nibbleIndex << 2)) & 0xF;
	}

	/**
	 * Create a crystal itemstack from a sent aspect. 
	 * @param aspect
	 * @param stackSize stack size
	 * @return
	 */
	public static ItemStack makeCrystal(Aspect aspect, int stackSize) {
		if (aspect==null) return null;
		ItemStack is = new ItemStack(ItemsTC.crystalEssence,stackSize);
		((ItemGenericEssentiaContainer)ItemsTC.crystalEssence).setAspects(is, new AspectList().add(aspect, 1));
		return is;
	}

	/**
	 * Create a crystal itemstack from a sent aspect. Sending a null will result in a balanced shard (one of each primal).
	 * @param aspect
	 * @return
	 */
	public static ItemStack makeCrystal(Aspect aspect) {
		return makeCrystal(aspect,1);
	}

	public static List<ItemStack> getOresWithWildCards(String oreDict) {
		ArrayList<ItemStack> ores = new ArrayList<>();
		if (oreDict.trim().endsWith("*")) {
			String prefix = oreDict.trim().replace("*", "");
			String tagPrefix = mapOreDictToTag(prefix).location().toString();
			net.minecraft.core.registries.BuiltInRegistries.ITEM.getTags().forEach(tag -> {
				if (tag.key().location().toString().startsWith(tagPrefix)) {
					tag.forEach(holder -> ores.add(new ItemStack(holder.value())));
				}
			});
		} else {
			net.minecraft.tags.TagKey<net.minecraft.world.item.Item> tagKey = mapOreDictToTag(oreDict);
			net.minecraft.core.registries.BuiltInRegistries.ITEM.get(tagKey).ifPresent(tag -> {
				tag.forEach(holder -> ores.add(new ItemStack(holder.value())));
			});
		}
		return ores;
	}

	public static Ingredient getIngredient(Object obj) {
		if (obj instanceof Ingredient) return (Ingredient) obj;
		if (obj instanceof ItemStack stack) {
			if (!stack.getComponentsPatch().isEmpty()) {
				return net.neoforged.neoforge.common.crafting.DataComponentIngredient.of(false, stack);
			}
			return Ingredient.of(stack.getItem());
		}
		if (obj instanceof net.minecraft.world.level.ItemLike itemLike) {
			return Ingredient.of(itemLike);
		}
		if (obj instanceof net.minecraft.tags.TagKey<?> tagKey) {
			if (tagKey.isFor(net.minecraft.core.registries.Registries.ITEM)) {
				return net.minecraft.core.registries.BuiltInRegistries.ITEM.get((net.minecraft.tags.TagKey<net.minecraft.world.item.Item>) tagKey)
						.map(Ingredient::of)
						.orElse(Ingredient.of());
			}
		}
		if (obj instanceof String str) {
			return net.minecraft.core.registries.BuiltInRegistries.ITEM.get(mapOreDictToTag(str))
					.map(Ingredient::of)
					.orElse(Ingredient.of());
		}
		return Ingredient.of();
	}

	public static net.minecraft.tags.TagKey<net.minecraft.world.item.Item> mapOreDictToTag(String oreDict) {
		String namespace = "c";
		String path = oreDict;
		if (oreDict.startsWith("ingot")) {
			path = "ingots/" + camelToSnake(oreDict.substring(5));
		} else if (oreDict.startsWith("ore")) {
			path = "ores/" + camelToSnake(oreDict.substring(3));
		} else if (oreDict.startsWith("gem")) {
			path = "gems/" + camelToSnake(oreDict.substring(3));
		} else if (oreDict.startsWith("dust")) {
			path = "dusts/" + camelToSnake(oreDict.substring(4));
		} else if (oreDict.startsWith("nugget")) {
			path = "nuggets/" + camelToSnake(oreDict.substring(6));
		} else if (oreDict.startsWith("block")) {
			path = "blocks/" + camelToSnake(oreDict.substring(5));
		} else if (oreDict.startsWith("plate")) {
			path = "plates/" + camelToSnake(oreDict.substring(5));
		} else if (oreDict.startsWith("gear")) {
			path = "gears/" + camelToSnake(oreDict.substring(4));
		} else if (oreDict.startsWith("rod")) {
			path = "rods/" + camelToSnake(oreDict.substring(3));
		} else {
			path = camelToSnake(oreDict);
		}
		return net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.Identifier.fromNamespaceAndPath(namespace, path));
	}

	private static String camelToSnake(String str) {
		if (str == null || str.isEmpty()) return "";
		StringBuilder sb = new StringBuilder();
		sb.append(Character.toLowerCase(str.charAt(0)));
		for (int i = 1; i < str.length(); i++) {
			char c = str.charAt(i);
			if (Character.isUpperCase(c)) {
				sb.append('_').append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * @deprecated Use {@link ThaumcraftInvHelper#getItemHandlerAt(Level,BlockPos,Direction)} instead
	 */
	public static IItemHandler getItemHandlerAt(Level world, BlockPos pos, Direction side) {
		return ThaumcraftInvHelper.getItemHandlerAt(world, pos, side);
	}

	/**
	 * @deprecated Use {@link ThaumcraftInvHelper#wrapInventory(Container,Direction)} instead
	 */
	public static IItemHandler wrapInventory(Container inventory, Direction side) {
		return ThaumcraftInvHelper.wrapInventory(inventory, side);
	}

	/**
	 * @deprecated Use {@link ThaumcraftInvHelper#areItemStackTagsEqualRelaxed(ItemStack,ItemStack)} instead
	 */	
	public static boolean areItemStackTagsEqualRelaxed(ItemStack prime, ItemStack other) {
		return ThaumcraftInvHelper.areItemStackTagsEqualRelaxed(prime, other);
	}
	
	/**
	 * @deprecated Use {@link ThaumcraftInvHelper#compareTagsRelaxed(CompoundTag,CompoundTag)} instead
	 */
	public static boolean compareTagsRelaxed(CompoundTag prime, CompoundTag other) {
		return java.util.Objects.equals(prime, other);
	}
		
}
