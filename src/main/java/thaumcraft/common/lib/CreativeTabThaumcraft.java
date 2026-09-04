package thaumcraft.common.lib;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;

public class CreativeTabThaumcraft {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Thaumcraft.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> THAUMCRAFT_TAB = CREATIVE_MODE_TABS.register("thaumcraft_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.thaumcraft"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> Thaumcraft.SALIS_MUNDUS.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(Thaumcraft.THAUMONOMICON.get());
                output.accept(Thaumcraft.AMBER.get());
                output.accept(Thaumcraft.SALIS_MUNDUS.get());
                output.accept(Thaumcraft.AMBER_BLOCK_ITEM.get());
            }).build());
}
