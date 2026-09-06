package thaumcraft.common.container;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;

/**
 * Central registry for all Thaumcraft {@link MenuType}s.
 *
 * Call {@code ThaumcraftMenus.MENU_TYPES.register(modEventBus)}
 * from {@link thaumcraft.Thaumcraft}'s constructor to activate.
 */
public class ThaumcraftMenus {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, Thaumcraft.MODID);

    /**
     * MenuType for the Arcane Workbench.
     * Uses IMenuTypeExtension to pass the workbench's BlockPos from the network packet.
     */
    public static final DeferredHolder<MenuType<?>, MenuType<thaumcraft.common.container.ResearchTableMenu>>
            RESEARCH_TABLE = MENU_TYPES.register(
                    "research_table",
                    () -> IMenuTypeExtension.create(thaumcraft.common.container.ResearchTableMenu::createClientSide)
            );

    public static final DeferredHolder<MenuType<?>, MenuType<ArcaneWorkbenchMenu>>
            ARCANE_WORKBENCH = MENU_TYPES.register(
                    "arcane_workbench",
                    () -> IMenuTypeExtension.create(ArcaneWorkbenchMenu::createClientSide)
            );
}
