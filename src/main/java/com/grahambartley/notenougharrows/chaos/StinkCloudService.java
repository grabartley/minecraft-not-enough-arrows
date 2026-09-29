package com.grahambartley.notenougharrows.chaos;

import com.grahambartley.notenougharrows.cloud.TimedCloud;
import com.grahambartley.notenougharrows.cloud.TimedCloudTracker;
import com.grahambartley.notenougharrows.config.StinkArrowConfig;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class StinkCloudService {
  public static final double RADIUS = 3.0;

  private static final int NAUSEA_TICKS = 100;
  private static final int PULSE_INTERVAL_TICKS = 4;
  private static final int PARTICLES_PER_BURST = 20;
  private static final double PARTICLE_DRIFT = 0.01;

  private static final Map<RegistryKey<World>, TimedCloudTracker> TRACKERS = new HashMap<>();

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
        .computeIfAbsent(world.getRegistryKey(), key -> new TimedCloudTracker())
        .add(new TimedCloud(center, RADIUS, world.getTime() + stink.cloudLifetimeTicks()));
    return true;
  }

  public static boolean isInCloud(final ServerWorld world, final Vec3d point) {
    final TimedCloudTracker tracker = TRACKERS.get(world.getRegistryKey());
    return tracker != null && tracker.live().stream().anyMatch(cloud -> cloud.contains(point));
  }

  private static void tick(final ServerWorld world) {
    final TimedCloudTracker tracker = TRACKERS.get(world.getRegistryKey());
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
