package thaumcraft.common.items.curios;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;

public class ItemLabel extends Item implements IEssentiaContainerItem {

    public ItemLabel(Properties properties) {
        super(properties.stacksTo(64));
    }

    public ItemLabel() {
        this(new Item.Properties());
    }

    public static Aspect getAspect(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.has(DataComponents.CUSTOM_DATA)) {
            return null;
        }
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        String aspectTag = tag.getStringOr("Aspect", "");
        if (aspectTag.isEmpty()) {
            aspectTag = tag.getStringOr("aspect", "");
        }
        if (!aspectTag.isEmpty()) {
            return Aspect.getAspect(aspectTag);
        }
        AspectList list = new AspectList();
        list.readFromNBT(tag);
        if (list.size() > 0) {
            return list.getAspects()[0];
        }
        return null;
    }

    public static void setAspect(ItemStack stack, Aspect aspect) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (aspect == null) {
            tag.remove("Aspect");
            tag.remove("aspect");
            tag.remove("Aspects");
            if (tag.isEmpty()) {
                stack.remove(DataComponents.CUSTOM_DATA);
            } else {
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            }
        } else {
            tag.putString("Aspect", aspect.getTag());
            tag.putString("aspect", aspect.getTag());
            AspectList list = new AspectList().add(aspect, 1);
            list.writeToNBT(tag);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    @Override
    public AspectList getAspects(ItemStack itemstack) {
        Aspect aspect = getAspect(itemstack);
        if (aspect != null) {
            return new AspectList().add(aspect, 1);
        }
        return null;
    }

    @Override
    public void setAspects(ItemStack itemstack, AspectList aspects) {
        if (aspects != null && aspects.size() > 0) {
            setAspect(itemstack, aspects.getAspects()[0]);
        } else {
            setAspect(itemstack, null);
        }
    }

    @Override
    public boolean ignoreContainedAspects() {
        return true;
    }
}
