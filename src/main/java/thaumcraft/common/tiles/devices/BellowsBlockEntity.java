package thaumcraft.common.tiles.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import thaumcraft.common.blocks.devices.BlockBellows;
import thaumcraft.common.blocks.entities.ThaumcraftBlockEntities;
import thaumcraft.common.lib.BellowsLogic;
import thaumcraft.common.tiles.crafting.CrucibleBlockEntity;
import thaumcraft.common.tiles.essentia.SmelterBlockEntity;

import java.lang.reflect.Field;

public class BellowsBlockEntity extends BlockEntity {

    private int delay = 0;
    public float inflation = 1.0f;
    public int direction = 1;
    private static Field crucibleHeatField;
    private static Field furnaceCookTimeField;

    static {
        try {
            crucibleHeatField = CrucibleBlockEntity.class.getDeclaredField("heat");
            crucibleHeatField.setAccessible(true);
        } catch (Exception e) {
            System.err.println("Failed to reflect Crucible heat field: " + e.getMessage());
        }

        try {
            // Using "cookingProgress" as per feedback.
            furnaceCookTimeField = AbstractFurnaceBlockEntity.class.getDeclaredField("cookingProgress");
            furnaceCookTimeField.setAccessible(true);
        } catch (Exception e) {
            System.err.println("Failed to reflect Furnace cookingProgress field: " + e.getMessage());
        }
    }

    public BellowsBlockEntity(BlockPos pos, BlockState state) {
        super(ThaumcraftBlockEntities.BELLOWS.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BellowsBlockEntity tile) {
        tile.tick(level, pos, state);
    }

    private void tick(Level level, BlockPos pos, BlockState state) {
        if (!state.hasProperty(BlockBellows.FACING)) return;

        Direction facing = state.getValue(BlockBellows.FACING);
        BlockPos targetPos = pos.relative(facing);
        BlockEntity targetBE = level.getBlockEntity(targetPos);

        delay++;
        if (delay >= 2) {
            delay = 0;

            if (targetBE instanceof AbstractFurnaceBlockEntity furnace) {
                // Accelerate furnace
                if (furnaceCookTimeField != null) {
                    try {
                        int progress = (int) furnaceCookTimeField.get(furnace);
                        if (progress > 0) {
                            furnaceCookTimeField.set(furnace, BellowsLogic.getAcceleratedFurnaceProgress(progress, 1));
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to accelerate Furnace: " + e.getMessage());
                    }
                }
            } else if (targetBE instanceof CrucibleBlockEntity crucible) {
                // Accelerate crucible heat
                if (crucibleHeatField != null) {
                    try {
                        short currentHeat = (short) crucibleHeatField.get(crucible);
                        short newHeat = BellowsLogic.getAcceleratedCrucibleHeat(currentHeat, 1);
                        if (newHeat != currentHeat) {
                            crucibleHeatField.set(crucible, newHeat);
                            crucible.setChanged();
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to accelerate Crucible: " + e.getMessage());
                    }
                }
            } else if (targetBE instanceof SmelterBlockEntity smelter) {
                // The smelter handles its own scanning for speedup normally, but since we're porting and the smelter says "deferred to TileBellows integration", we just set it here.
                smelter.setBellows(1);
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        addAdditionalSaveData(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        readAdditionalSaveData(input);
    }

    public void addAdditionalSaveData(ValueOutput output) {
        output.store("Inflation", com.mojang.serialization.Codec.FLOAT, inflation);
        output.store("Direction", com.mojang.serialization.Codec.INT, direction);
        output.store("Delay", com.mojang.serialization.Codec.INT, delay);
    }

    public void readAdditionalSaveData(ValueInput input) {
        inflation = input.read("Inflation", com.mojang.serialization.Codec.FLOAT).orElse(1.0f);
        direction = input.read("Direction", com.mojang.serialization.Codec.INT).orElse(1);
        delay = input.read("Delay", com.mojang.serialization.Codec.INT).orElse(0);
    }
}
