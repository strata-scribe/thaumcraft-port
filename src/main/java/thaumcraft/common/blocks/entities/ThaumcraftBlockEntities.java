package thaumcraft.common.blocks.entities;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;
import thaumcraft.Thaumcraft;
import thaumcraft.api.blocks.ThaumcraftBlocks;

public class ThaumcraftBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Thaumcraft.MODID);

    public static final Supplier<BlockEntityType<TileGolemBuilder>> GOLEM_BUILDER = BLOCK_ENTITIES.register(
            "golem_builder",
            () -> new BlockEntityType<>(TileGolemBuilder::new, ThaumcraftBlocks.golemBuilder.get())
    );
}
