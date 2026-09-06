package thaumcraft.common.blocks.entities;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;
import thaumcraft.Thaumcraft;
import thaumcraft.api.blocks.ThaumcraftBlocks;
import thaumcraft.common.tiles.ArcaneWorkbenchBlockEntity;
import thaumcraft.common.tiles.crafting.CrucibleBlockEntity;

public class ThaumcraftBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Thaumcraft.MODID);

    public static final Supplier<BlockEntityType<TileGolemBuilder>> GOLEM_BUILDER = BLOCK_ENTITIES.register(
            "golem_builder",
            () -> new BlockEntityType<>(TileGolemBuilder::new, ThaumcraftBlocks.golemBuilder.get())
    );

    public static final Supplier<BlockEntityType<ArcaneWorkbenchBlockEntity>> ARCANE_WORKBENCH = BLOCK_ENTITIES.register(
            "arcane_workbench",
            () -> new BlockEntityType<>(ArcaneWorkbenchBlockEntity::new, ThaumcraftBlocks.arcaneWorkbench.get())
    );

    /** Crucible — aspect vessel + alchemy engine. */
    public static final Supplier<BlockEntityType<CrucibleBlockEntity>> CRUCIBLE = BLOCK_ENTITIES.register(
            "crucible",
            () -> new BlockEntityType<>(CrucibleBlockEntity::new, ThaumcraftBlocks.crucible.get())
    );
}
