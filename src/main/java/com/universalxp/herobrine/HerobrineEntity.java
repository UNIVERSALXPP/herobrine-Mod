package com.universalxp.herobrine;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Harmless Herobrine: no AI goals are registered, so he just stands there.
 * While he exists, lightning keeps striking around his spawn point.
 */
public class HerobrineEntity extends Mob {
    /** Ticks between lightning strikes (20 ticks = 1 second). Lower = more thunder. */
    private static final int STRIKE_INTERVAL_TICKS = 12;
    /** Lightning lands between these distances (in blocks) from Herobrine. */
    private static final double MIN_STRIKE_DISTANCE = 3.0D;
    private static final double MAX_STRIKE_DISTANCE = 9.0D;
    /** true = only visual + sound lightning (no damage, no fire). */
    private static final boolean VISUAL_ONLY_LIGHTNING = false;

    public HerobrineEntity(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
    }

    // Deliberately no registerGoals() override: Herobrine has no AI and does nothing.

    @Override
    public boolean removeWhenFarAway(double distanceSquared) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        // The thunder he summons must not kill him.
        if (source.is(DamageTypeTags.IS_LIGHTNING) || source.is(DamageTypeTags.IS_FIRE)) {
            return false;
        }
        return super.hurtServer(level, source, amount);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel serverLevel
                && this.isAlive()
                && this.tickCount % STRIKE_INTERVAL_TICKS == 0) {
            strikeLightning(serverLevel);
        }
    }

    private void strikeLightning(ServerLevel level) {
        double angle = this.random.nextDouble() * Math.PI * 2.0D;
        double distance = MIN_STRIKE_DISTANCE + this.random.nextDouble() * (MAX_STRIKE_DISTANCE - MIN_STRIKE_DISTANCE);
        double x = this.getX() + Math.cos(angle) * distance;
        double z = this.getZ() + Math.sin(angle) * distance;

        BlockPos columnPos = BlockPos.containing(x, this.getY(), z);
        if (!level.hasChunkAt(columnPos)) {
            return;
        }

        double y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, columnPos.getX(), columnPos.getZ());
        if (y > this.getY() + 6.0D) {
            // Inside a cave/roof: strike at his own level instead of the surface far above.
            y = this.getY();
        }

        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
        if (bolt != null) {
            bolt.setPos(x, y, z);
            bolt.setVisualOnly(VISUAL_ONLY_LIGHTNING);
            level.addFreshEntity(bolt);
        }
    }
}
