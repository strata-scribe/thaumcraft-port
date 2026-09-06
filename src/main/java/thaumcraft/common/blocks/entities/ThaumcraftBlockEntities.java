package thaumcraft.common.blocks.entities;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;
import thaumcraft.Thaumcraft;
import thaumcraft.api.blocks.ThaumcraftBlocks;
import thaumcraft.common.tiles.ArcaneWorkbenchBlockEntity;
import thaumcraft.common.tiles.crafting.CrucibleBlockEntity;
import thaumcraft.common.tiles.essentia.JarBlockEntity;
import thaumcraft.common.tiles.crafting.PedestalBlockEntity;
import thaumcraft.common.tiles.crafting.InfusionMatrixBlockEntity;

public class ThaumcraftBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Thaumcraft.MODID);

    public static final Supplier<BlockEntityType<TileGolemBuilder>> GOLEM_BUILDER = BLOCK_ENTITIES.register(
            "golem_builder",
            () -> new BlockEntityType<>(TileGolemBuilder::new, ThaumcraftBlocks.golemBuilder.get())
    );

    public static final Supplier<BlockEntityType<thaumcraft.common.tiles.devices.TileFocalManipulator>> FOCAL_MANIPULATOR = BLOCK_ENTITIES.register(
            "focal_manipulator",
            () -> new BlockEntityType<>(thaumcraft.common.tiles.devices.TileFocalManipulator::new, ThaumcraftBlocks.focalManipulator.get())
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

    /** Warded Jar — single-aspect essentia storage (normal and void variants). */
    public static final Supplier<BlockEntityType<JarBlockEntity>> JAR = BLOCK_ENTITIES.register(
            "jar",
            () -> new BlockEntityType<>(JarBlockEntity::new,
                    ThaumcraftBlocks.jarNormal.get(),
                    ThaumcraftBlocks.jarVoid.get())
    );

    /** Pedestal — single-item holder for infusion altar. */
    public static final Supplier<BlockEntityType<PedestalBlockEntity>> PEDESTAL = BLOCK_ENTITIES.register(
            "pedestal",
            () -> new BlockEntityType<>(PedestalBlockEntity::new,
                    ThaumcraftBlocks.pedestalArcane.get(),
                    ThaumcraftBlocks.pedestalAncient.get(),
                    ThaumcraftBlocks.pedestalEldritch.get())
    );

    /** Infusion Matrix — runic crafting controller. */
    public static final Supplier<BlockEntityType<InfusionMatrixBlockEntity>> INFUSION_MATRIX = BLOCK_ENTITIES.register(
            "infusion_matrix",
            () -> new BlockEntityType<>(InfusionMatrixBlockEntity::new, ThaumcraftBlocks.infusionMatrix.get())
    );
}

