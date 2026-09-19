package com.grahambartley.notenougharrows.control;

import com.grahambartley.notenougharrows.config.TargetingArrowConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class ControlHoldService {
  public static final double STEERING_SPEED = 1.1;

  private static final Map<RegistryKey<World>, ControlHoldTracker> TRACKERS = new HashMap<>();

  private ControlHoldService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(ControlHoldService::tick);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> forget());
  }

  public static int taunt(
      final ServerWorld world, final Vec3d center, final TargetingArrowConfig targeting) {
    if (!targeting.taunts()) {
      return 0;
    }
    return holdAll(
        world,
        HostileScan.engagedHostilesAround(world, center, targeting.tauntRadius()),
        center,
        ControlSteering.DRAWN,
        targeting.tauntDurationTicks());
  }

  public static int repel(
      final ServerWorld world, final Vec3d center, final TargetingArrowConfig targeting) {
    if (!targeting.repels()) {
      return 0;
    }
    return holdAll(
        world,
        HostileScan.hostilesAround(world, center, targeting.repelRadius()),
        center,
        ControlSteering.FLEEING,
        targeting.repelDurationTicks());
  }

  public static boolean daze(
      final ServerWorld world, final MobEntity mob, final TargetingArrowConfig targeting) {
    if (world == null || mob == null || !targeting.dazes() || !HostileScan.isHostile(mob)) {
      return false;
    }
    return hold(
            world,
            mob,
            mob.getBoundingBox().getCenter(),
            ControlSteering.WANDERING,
            targeting.dazeDurationTicks())
        > 0;
  }

  public static Optional<ControlHold> heldIn(final ServerWorld world, final MobEntity mob) {
    final ControlHoldTracker tracker = TRACKERS.get(world.getRegistryKey());
    return tracker == null ? Optional.empty() : tracker.find(mob.getUuid());
  }

  public static void forget() {
    TRACKERS.clear();
  }

  private static int holdAll(
      final ServerWorld world,
      final List<MobEntity> mobs,
      final Vec3d anchor,
      final ControlSteering steering,
      final int durationTicks) {
    int held = 0;
    for (final MobEntity mob : mobs) {
      held += hold(world, mob, anchor, steering, durationTicks);
    }
    return held;
  }

  private static int hold(
      final ServerWorld world,
      final MobEntity mob,
      final Vec3d anchor,
      final ControlSteering steering,
      final int durationTicks) {
    if (durationTicks <= 0) {
      return 0;
    }
    trackerFor(world)
        .hold(new ControlHold(mob.getUuid(), anchor, steering, world.getTime() + durationTicks));
    steer(mob, anchor, steering);
    return 1;
  }

  private static void tick(final ServerWorld world) {
    final ControlHoldTracker tracker = TRACKERS.get(world.getRegistryKey());
    if (tracker == null || tracker.isEmpty()) {
      return;
    }
    tracker.takeExpired(world.getTime()).forEach(hold -> handBack(world, hold));
    tracker.live().forEach(hold -> apply(world, tracker, hold));
  }

  private static void apply(
      final ServerWorld world, final ControlHoldTracker tracker, final ControlHold hold) {
    final MobEntity mob = mobIn(world, hold);
    if (mob == null) {
      tracker.forget(hold.mobId());
      return;
    }
    steer(mob, hold.anchor(), hold.steering());
  }

  private static void handBack(final ServerWorld world, final ControlHold hold) {
    final MobEntity mob = mobIn(world, hold);
    if (mob != null) {
      mob.getNavigation().stop();
    }
  }

  private static void steer(
      final MobEntity mob, final Vec3d anchor, final ControlSteering steering) {
    if (steering.clearsTarget()) {
      mob.setTarget(null);
    }
    if (!mob.getNavigation().isIdle()) {
      return;
    }
    steering
        .destination(mob.getBoundingBox().getCenter(), anchor)
        .ifPresent(
            destination ->
                mob.getNavigation()
                    .startMovingTo(
                        destination.getX(),
                        destination.getY(),
                        destination.getZ(),
                        STEERING_SPEED));
  }

  private static MobEntity mobIn(final ServerWorld world, final ControlHold hold) {
    final Entity entity = world.getEntity(hold.mobId());
    return entity instanceof MobEntity mob && mob.isAlive() ? mob : null;
  }

  private static ControlHoldTracker trackerFor(final ServerWorld world) {
    return TRACKERS.computeIfAbsent(world.getRegistryKey(), key -> new ControlHoldTracker());
  }
}
