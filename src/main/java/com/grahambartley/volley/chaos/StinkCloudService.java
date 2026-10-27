package com.grahambartley.volley.chaos;

import com.grahambartley.volley.cloud.TimedCloud;
import com.grahambartley.volley.cloud.TimedCloudTracker;
import com.grahambartley.volley.config.StinkArrowConfig;
import com.grahambartley.volley.world.PerWorld;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public final class StinkCloudService {
  public static final double RADIUS = 3.0;

  private static final int NAUSEA_TICKS = 100;
  private static final int PULSE_INTERVAL_TICKS = 4;
  private static final int PARTICLES_PER_BURST = 20;
  private static final double PARTICLE_DRIFT = 0.01;

  private static final PerWorld<TimedCloudTracker> TRACKERS =
      new PerWorld<>(() -> new TimedCloudTracker());

  private StinkCloudService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(StinkCloudService::tick);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> TRACKERS.clear());
  }

  public static boolean open(
      final ServerWorld world, final Vec3d center, final StinkArrowConfig stink) {
    if (world == null || center == null || !stink.enabled() || stink.cloudLifetimeTicks() <= 0) {
      return false;
    }
    TRACKERS
        .of(world)
        .add(new TimedCloud(center, RADIUS, world.getTime() + stink.cloudLifetimeTicks()));
    return true;
  }

  public static boolean isInCloud(final ServerWorld world, final Vec3d point) {
    final TimedCloudTracker tracker = TRACKERS.in(world);
    return tracker != null && tracker.live().stream().anyMatch(cloud -> cloud.contains(point));
  }

  private static void tick(final ServerWorld world) {
    final TimedCloudTracker tracker = TRACKERS.in(world);
    if (tracker == null || tracker.isEmpty()) {
      return;
    }
    tracker.removeExpired(world.getTime());
    for (final TimedCloud cloud : tracker.live()) {
      StinkRepel.repel(world, cloud);
      if (world.getTime() % PULSE_INTERVAL_TICKS == 0) {
        show(world, cloud);
        nauseate(world, cloud);
      }
    }
  }

  private static void nauseate(final ServerWorld world, final TimedCloud cloud) {
    for (final PlayerEntity inside :
        world.getEntitiesByClass(
            PlayerEntity.class,
            cloud.bounds(),
            player ->
                player.isAlive()
                    && !player.isSpectator()
                    && cloud.contains(player.getBoundingBox().getCenter()))) {
      inside.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, NAUSEA_TICKS));
    }
  }

  private static void show(final ServerWorld world, final TimedCloud cloud) {
    final Vec3d center = cloud.center();
    final double spread = cloud.radius() / 2.0;
    world.spawnParticles(
        ParticleTypes.SNEEZE,
        center.x,
        center.y,
        center.z,
        PARTICLES_PER_BURST,
        spread,
        spread,
        spread,
        PARTICLE_DRIFT);
  }
}
