package thaumcraft.api.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import thaumcraft.api.aura.AuraHelper;

/**
 * @author Azanor
 * Helper methods to manipulate charge in items that use IRechargable.
 */
public class RechargeHelper {
	
	public static String NBT_TAG = "tc.charge";

	/**
	 * This method is called to recharge an item from the aura. 
	 */
	public static float rechargeItem(Level world, ItemStack is, BlockPos pos, Player player, int amt) {	
		if (is==null || is.isEmpty() || !(is.getItem() instanceof IRechargable)) return 0;		
		IRechargable chargeItem = (IRechargable)is.getItem();		
		if (player!=null && AuraHelper.shouldPreserveAura(world,player,pos)) return 0;				
		amt = Math.min(amt, chargeItem.getMaxCharge(is,player) - getCharge(is));
		int drained = (int) AuraHelper.drainVis(world, pos, amt, false);		
		if (drained>0) {
			addCharge(is, player, drained);
			return drained;
		}		
		return 0;
	}
	
	/**
	 * This method is called to recharge an item blindly.
	 */
	public static float rechargeItemBlindly(ItemStack is, Player player, int amt) {	
		if (is==null || is.isEmpty() || !(is.getItem() instanceof IRechargable)) return 0;		
		IRechargable chargeItem = (IRechargable)is.getItem();		
		amt = Math.min(amt, chargeItem.getMaxCharge(is,player) - getCharge(is));
		if (amt>0) addCharge(is, player, amt);		
		return amt;
	}
	
	private static void addCharge(ItemStack is, LivingEntity player, int amt) {
		if (is==null || is.isEmpty() || !(is.getItem() instanceof IRechargable)) return;
		IRechargable chargeItem = (IRechargable)is.getItem();
		int amount = Math.min(chargeItem.getMaxCharge(is,player), amt + getCharge(is));
		
		CompoundTag tag = is.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		tag.putInt(NBT_TAG, amount);
		is.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
	}
	
	/**
	 * @return returns charge amount or -1 if item is not rechargable
	 */
	public static int getCharge(ItemStack is) {
		if (is==null || is.isEmpty() || !(is.getItem() instanceof IRechargable)) return -1;
		if (is.has(DataComponents.CUSTOM_DATA)) {
			return is.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt(NBT_TAG).orElse(0);
		}
		return 0;
	}
	
	/**
	 * @return return charge level as a float (with 1 being full)
	 */
	public static float getChargePercentage(ItemStack is, Player player) {
		if (is==null || is.isEmpty() || !(is.getItem() instanceof IRechargable)) return -1;
		float c = getCharge(is);
		float m =  ((IRechargable)is.getItem()).getMaxCharge(is, player);
		return c / m;
	}
	
	/**
	 * Consumes vis charge from the item
	 */
	public static boolean consumeCharge(ItemStack is, LivingEntity player, int amt) {
		if (is==null || is.isEmpty() || !(is.getItem() instanceof IRechargable)) return false;
		if (is.has(DataComponents.CUSTOM_DATA)) {
			CompoundTag tag = is.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
			int charge = tag.getInt(NBT_TAG).orElse(0);
			if (charge>=amt) {
				charge -= amt;
				tag.putInt(NBT_TAG, charge);
				is.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
				return true;
			}
		}
		return false;
	}
}
