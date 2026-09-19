package com.grahambartley.notenougharrows.control;

import com.grahambartley.notenougharrows.config.SmokeArrowConfig;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class SmokeCloudService {
  private static final int BLINDNESS_REFRESH_TICKS = 40;
  private static final int BLINDNESS_AMPLIFIER = 0;

  private static final int PULSE_INTERVAL_TICKS = 4;
  private static final int PARTICLES_PER_BURST = 12;
  private static final double PARTICLE_DRIFT = 0.01;

  private static final Map<RegistryKey<World>, SmokeCloudTracker> TRACKERS = new HashMap<>();

  private SmokeCloudService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(SmokeCloudService::tick);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> forget());
  }

  public static boolean open(
      final ServerWorld world, final Vec3d center, final SmokeArrowConfig smoke) {
    if (world == null || center == null || !smoke.clouds()) {
      return false;
    }
    trackerFor(world)
        .add(new SmokeCloud(center, smoke.radius(), world.getTime() + smoke.durationTicks()));
    return true;
  }

  private static void forget() {
    TRACKERS.clear();
  }

  private static void tick(final ServerWorld world) {
    final SmokeCloudTracker tracker = TRACKERS.get(world.getRegistryKey());
    if (tracker == null || tracker.isEmpty()) {
      return;
    }
    tracker.removeExpired(world.getTime());
    tracker.live().forEach(cloud -> blindInside(world, cloud));
  }

  private static void blindInside(final ServerWorld world, final SmokeCloud cloud) {
    if (world.getTime() % PULSE_INTERVAL_TICKS != 0) {
      return;
    }
    showCloud(world, cloud);
    for (final LivingEntity inside :
        world.getEntitiesByClass(
            LivingEntity.class,
            cloud.bounds(),
            candidate ->
                candidate.isAlive() && cloud.contains(candidate.getBoundingBox().getCenter()))) {
      inside.addStatusEffect(
          new StatusEffectInstance(
              StatusEffects.BLINDNESS, BLINDNESS_REFRESH_TICKS, BLINDNESS_AMPLIFIER));
    }
  }

  private static void showCloud(final ServerWorld world, final SmokeCloud cloud) {
    world.spawnParticles(
        ParticleTypes.CAMPFIRE_COSY_SMOKE,
        cloud.center().getX(),
        cloud.center().getY(),
        cloud.center().getZ(),
        PARTICLES_PER_BURST,
        cloud.radius() / 2.0,
        cloud.radius() / 2.0,
        cloud.radius() / 2.0,
        PARTICLE_DRIFT);
  }

  private static SmokeCloudTracker trackerFor(final ServerWorld world) {
    return TRACKERS.computeIfAbsent(world.getRegistryKey(), key -> new SmokeCloudTracker());
  }
}
