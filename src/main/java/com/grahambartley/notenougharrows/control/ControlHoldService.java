package com.grahambartley.notenougharrows.control;

import com.grahambartley.notenougharrows.config.AllegianceArrowConfig;
import com.grahambartley.notenougharrows.config.TargetingArrowConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class ControlHoldService {
  private static final double STEERING_SPEED = 1.1;
  private static final int REPATH_INTERVAL_TICKS = 5;

  private static final Map<RegistryKey<World>, ControlHoldTracker> TRACKERS = new HashMap<>();

  private ControlHoldService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(ControlHoldService::tick);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> forget());
  }

  public static int taunt(
      final ServerWorld world,
      final Vec3d center,
      @Nullable final LivingEntity struck,
      final TargetingArrowConfig targeting) {
    if (!targeting.taunts()) {
      return 0;
    }

    int held = 0;
    for (final MobEntity mob :
        HostileScan.engagedHostilesAround(world, center, targeting.tauntRadius())) {
      if (mob == struck) {
        continue;
      }
      final ControlHold hold =
          struck == null
              ? ControlHold.at(
                  mob.getUuid(),
                  center,
                  ControlSteering.DRAWN,
                  world.getTime() + targeting.tauntDurationTicks())
              : ControlHold.on(
                  mob.getUuid(),
                  center,
                  struck.getUuid(),
                  ControlSteering.DRAWN,
                  world.getTime() + targeting.tauntDurationTicks());
      trackerFor(world).hold(hold);
      applyTargeting(world, mob, hold);
      steer(mob, hold.anchor(), hold.steering());
      held++;
    }
    return held;
  }

  public static int repel(
      final ServerWorld world, final Vec3d center, final TargetingArrowConfig targeting) {
    if (!targeting.repels()) {
      return 0;
    }

    int held = 0;
    for (final MobEntity mob : HostileScan.hostilesAround(world, center, targeting.repelRadius())) {
      final ControlHold hold =
          ControlHold.at(
              mob.getUuid(),
              center,
              ControlSteering.FLEEING,
              world.getTime() + targeting.repelDurationTicks());
      trackerFor(world).hold(hold);
      steer(mob, hold.anchor(), hold.steering());
      held++;
    }
    return held;
  }

  public static boolean enlist(
      final ServerWorld world,
      final MobEntity mob,
      @Nullable final LivingEntity protectedEntity,
      final AllegianceArrowConfig allegiance) {
    if (protectedEntity == null || !allegiance.turns() || !HostileScan.isHostile(mob)) {
      return false;
    }
    final ControlHold hold =
        ControlHold.defending(
            mob.getUuid(),
            mob.getBoundingBox().getCenter(),
            protectedEntity.getUuid(),
            allegiance.defendRadius(),
            world.getTime() + allegiance.durationTicks());
    trackerFor(world).hold(hold);
    applyTargeting(world, mob, hold);
    return true;
  }

  public static Optional<ControlHold> heldIn(final ServerWorld world, final MobEntity mob) {
    final ControlHoldTracker tracker = TRACKERS.get(world.getRegistryKey());
    return tracker == null ? Optional.empty() : tracker.find(mob.getUuid());
  }

  private static void forget() {
    TRACKERS.clear();
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
    if (hold.steering().targetPolicy().retargetsEveryTick() && !applyTargeting(world, mob, hold)) {
      tracker.forget(hold.mobId());
      handBack(world, hold);
      return;
    }
    if (hold.steering().navigates() && world.getTime() % REPATH_INTERVAL_TICKS == 0) {
      steer(mob, hold.anchor(), hold.steering());
    }
  }

  private static boolean applyTargeting(
      final ServerWorld world, final MobEntity mob, final ControlHold hold) {
    return switch (hold.steering().targetPolicy()) {
      case AIM_AT_SUBJECT -> {
        aimAtSubject(world, mob, hold);
        yield true;
      }
      case DEFEND_SUBJECT -> defendSubject(world, mob, hold);
      case LEAVE_ALONE -> true;
    };
  }

  private static void aimAtSubject(
      final ServerWorld world, final MobEntity mob, final ControlHold hold) {
    final LivingEntity subject = livingSubject(world, hold);
    mob.setTarget(subject == mob ? null : subject);
  }

  private static boolean defendSubject(
      final ServerWorld world, final MobEntity mob, final ControlHold hold) {
    final LivingEntity defended = livingSubject(world, hold);
    if (defended == null) {
      return false;
    }
    mob.setTarget(DefenderTargets.threatTo(world, defended, mob, hold.defendRadius()).orElse(null));
    return true;
  }

  @Nullable
  private static LivingEntity livingSubject(final ServerWorld world, final ControlHold hold) {
    final UUID subjectId = hold.subjectId().orElse(null);
    if (subjectId == null) {
      return null;
    }
    final Entity subject = world.getEntity(subjectId);
    return subject instanceof LivingEntity living && living.isAlive() ? living : null;
  }

  private static void handBack(final ServerWorld world, final ControlHold hold) {
    final MobEntity mob = mobIn(world, hold);
    if (mob == null) {
      return;
    }
    if (hold.steering() == ControlSteering.DEFENDING) {
      mob.setTarget(null);
    }
    if (hold.steering().navigates()) {
      mob.getNavigation().stop();
    }
  }

  private static void steer(
      final MobEntity mob, final Vec3d anchor, final ControlSteering steering) {
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
