package thaumcraft.common.tiles.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Handles the actual in-game lightning hazard for the Infusion Matrix.
 */
public class InfusionHazardLightningLogic {

    /**
     * Executes the lightning hazard event if the strike odds are met.
     */
    public static void executeHazard(Level level, BlockPos pos, float instability, int cycleTime) {
        float randomFloat = level.getRandom().nextFloat();

        if (!InfusionLightningLogic.shouldStrike(instability, cycleTime, randomFloat)) {
            return;
        }

        float targetRandom = level.getRandom().nextFloat();
        InfusionLightningLogic.TargetType targetType = InfusionLightningLogic.determineTarget(targetRandom);
        float damageRandom = level.getRandom().nextFloat();
        float damage = InfusionLightningLogic.calculateDamage(instability, damageRandom);

        switch (targetType) {
            case PLAYER -> strikePlayer(level, pos, damage);
            case PEDESTAL -> strikePedestal(level, pos, damage);
            case BLOCK -> strikeRandomBlock(level, pos);
        }
    }

    private static void strikePlayer(Level level, BlockPos pos, float damage) {
        List<Player> players = level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(10.0));
        if (!players.isEmpty()) {
            Player target = players.get(level.getRandom().nextInt(players.size()));
            target.hurt(level.damageSources().magic(), damage); // Use magic as lightning substitute for infusion hazard
        }
    }

    private static void strikePedestal(Level level, BlockPos pos, float damage) {
        // Pedestal hit could just spawn particles or logic for hitting nearby entities
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(8.0));
        if (!targets.isEmpty()) {
            LivingEntity target = targets.get(level.getRandom().nextInt(targets.size()));
            target.hurt(level.damageSources().magic(), damage);
        }
    }

    private static void strikeRandomBlock(Level level, BlockPos pos) {
        // Just harmless visual block hit effect
    }
}
