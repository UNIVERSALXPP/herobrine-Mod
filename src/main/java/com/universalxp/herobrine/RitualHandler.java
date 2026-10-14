package com.universalxp.herobrine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Ritual layout (top view of the ground layer):
 *
 *   G G G
 *   G M G     G = Gold block (8 of them), M = Mossy Cobblestone
 *   G G G
 *
 * On top of the Mossy Cobblestone goes one Netherrack.
 * Right-click the top of that Netherrack with Flint and Steel to start the ritual.
 */
public final class RitualHandler {
    private RitualHandler() {}

    public static InteractionResult onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(Items.FLINT_AND_STEEL) || hit.getDirection() != Direction.UP) {
            return InteractionResult.PASS;
        }

        BlockPos netherrackPos = hit.getBlockPos();
        if (!isRitualComplete(level, netherrackPos)) {
            return InteractionResult.PASS;
        }

        if (level instanceof ServerLevel serverLevel) {
            summon(serverLevel, netherrackPos, player);
            stack.hurtAndBreak(1, player, hand.asEquipmentSlot());
        }
        return InteractionResult.SUCCESS;
    }

    private static boolean isRitualComplete(Level level, BlockPos netherrackPos) {
        if (!level.getBlockState(netherrackPos).is(Blocks.NETHERRACK)) {
            return false;
        }
        BlockPos center = netherrackPos.below();
        if (!level.getBlockState(center).is(Blocks.MOSSY_COBBLESTONE)) {
            return false;
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                if (!level.getBlockState(center.offset(dx, 0, dz)).is(Blocks.GOLD_BLOCK)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void summon(ServerLevel level, BlockPos netherrackPos, Player player) {
        BlockPos firePos = netherrackPos.above();
        if (level.getBlockState(firePos).isAir()) {
            level.setBlockAndUpdate(firePos, Blocks.FIRE.defaultBlockState());
        }
        level.playSound(null, firePos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);

        HerobrineEntity herobrine = HerobrineRitualMod.HEROBRINE.create(level, EntitySpawnReason.TRIGGERED);
        if (herobrine == null) {
            return;
        }
        herobrine.setPos(firePos.getX() + 0.5D, firePos.getY(), firePos.getZ() + 0.5D);
        // Face the player who lit the ritual.
        double dx = player.getX() - herobrine.getX();
        double dz = player.getZ() - herobrine.getZ();
        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0D);
        herobrine.setYRot(yaw);
        herobrine.setYHeadRot(yaw);
        level.addFreshEntity(herobrine);

        // Opening strike right on the altar.
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
        if (bolt != null) {
            bolt.setPos(firePos.getX() + 0.5D, firePos.getY(), firePos.getZ() + 0.5D);
            bolt.setVisualOnly(true);
            level.addFreshEntity(bolt);
        }
    }
}
