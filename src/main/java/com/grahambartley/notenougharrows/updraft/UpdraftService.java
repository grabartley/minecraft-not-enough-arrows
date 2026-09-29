package com.grahambartley.notenougharrows.updraft;

import com.grahambartley.notenougharrows.ModSounds;
import com.grahambartley.notenougharrows.audio.ModSoundPlayer;
import com.grahambartley.notenougharrows.config.UpdraftArrowConfig;
import com.grahambartley.notenougharrows.grapple.GrappleFlightCheck;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class UpdraftService {
  private static final int PULSE_INTERVAL_TICKS = 4;
  private static final int PARTICLES_PER_BLOCK = 2;
  private static final double PARTICLE_RISE = 0.15;
  private static final float OPEN_PITCH = 1.0f;
  private static final Map<RegistryKey<World>, UpdraftTracker> TRACKERS = new HashMap<>();

  private UpdraftService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(UpdraftService::liftIn);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> forget());
  }

  public static boolean open(
      @Nullable final ServerWorld world,
      @Nullable final Vec3d base,
      @Nullable final UpdraftArrowConfig updraft) {
    if (world == null
        || base == null
        || updraft == null
        || updraft.lifetimeTicks() <= 0
        || updraft.strength() <= 0.0f) {
      return false;
    }
    trackerFor(world)
        .add(
            LiveUpdraft.opening(
                new UpdraftColumn(
                    base,
                    UpdraftColumn.RADIUS,
                    updraft.heightBlocks(),
                    updraft.strength(),
                    world.getTime() + updraft.lifetimeTicks())));
    ModSoundPlayer.play(
        world,
        base,
        ModSounds.UPDRAFT_ARROW_OPEN,
        SoundCategory.NEUTRAL,
        ModSoundPlayer.LANDING_VOLUME,
        OPEN_PITCH);
    return true;
  }

  public static List<LiveUpdraft> liveIn(@Nullable final ServerWorld world) {
    final UpdraftTracker tracker = world == null ? null : TRACKERS.get(world.getRegistryKey());
    return tracker == null ? List.of() : tracker.live();
  }

  public static void forget() {
    TRACKERS.clear();
  }

  private static void liftIn(final ServerWorld world) {
    final UpdraftTracker tracker = TRACKERS.get(world.getRegistryKey());
    if (tracker == null || tracker.isEmpty()) {
      return;
    }
    tracker.removeExpired(world.getTime());
    tracker.live().forEach(updraft -> liftOnce(world, updraft));
  }

  private static void liftOnce(final ServerWorld world, final LiveUpdraft updraft) {
    final UpdraftColumn column = updraft.column();
    final List<Entity> inside =
        world.getOtherEntities(
            null,
            column.bounds(),
            candidate ->
                UpdraftTargets.canBeLifted(candidate) && column.contains(candidate.getPos()));
    final List<UUID> insideIds = inside.stream().map(Entity::getUuid).toList();
    updraft.riders().settle(insideIds);
    for (final Entity entity : inside) {
      if (updraft.riders().mayLift(entity.getUuid())) {
        updraft.riders().carry(entity.getUuid());
        lift(entity, column);
      }
    }
    if (world.getTime() % PULSE_INTERVAL_TICKS == 0) {
      show(world, column);
    }
  }

  private static void lift(final Entity entity, final UpdraftColumn column) {
    entity.setVelocity(column.lift(entity.getVelocity()));
    entity.velocityModified = true;
    entity.fallDistance = 0.0f;
    if (entity instanceof ServerPlayerEntity player) {
      GrappleFlightCheck.clearFloatingCountFor(player);
    }
  }

  private static void show(final ServerWorld world, final UpdraftColumn column) {
    final double halfHeight = column.heightBlocks() / 2.0;
    world.spawnParticles(
        ParticleTypes.CLOUD,
        column.base().getX(),
        column.base().getY() + halfHeight,
        column.base().getZ(),
        PARTICLES_PER_BLOCK * column.heightBlocks(),
        column.radius() / 2.0,
        halfHeight,
        column.radius() / 2.0,
        PARTICLE_RISE);
  }

  private static UpdraftTracker trackerFor(final ServerWorld world) {
    return TRACKERS.computeIfAbsent(world.getRegistryKey(), key -> new UpdraftTracker());
  }
}
