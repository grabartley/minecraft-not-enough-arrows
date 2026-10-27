package com.grahambartley.volley.control;

import com.grahambartley.volley.config.AllegianceArrowConfig;
import com.grahambartley.volley.config.TargetingArrowConfig;
import com.grahambartley.volley.world.PerWorld;
import com.grahambartley.volley.world.SessionTracker;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class ControlHoldService {

  private static final PerWorld<SessionTracker<ControlHold>> TRACKERS =
      new PerWorld<>(() -> new SessionTracker<>(ControlHold::mobId));

  private ControlHoldService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(ControlHoldService::tick);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> forget());
  }

  public static int taunt(
      final ServerWorld world,
      final Vec3d center,
      @Nullable final LivingEntity struck,
      @Nullable final LivingEntity shooter,
      final TargetingArrowConfig targeting) {
    if (!targeting.taunts()) {
      return 0;
    }

    final Set<MobEntity> drawn =
        new LinkedHashSet<>(MobScan.engagedMobsAround(world, center, targeting.tauntRadius()));
    if (struck != null && shooter != null) {
      drawn.addAll(MobScan.mobsHunting(world, shooter, targeting.tauntRadius()));
    }
    int held = 0;
    for (final MobEntity mob : drawn) {
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
      TRACKERS.of(world).add(hold);
      mob.setAttached(ControlPersistence.HOLD, hold);
      if (hold.subjectId().isEmpty()) {
        MobAggression.aim(mob, null);
        MobSteering.steer(mob, hold.anchor(), hold.steering());
      } else {
        applyTargeting(world, mob, hold);
      }
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
    for (final MobEntity mob : MobScan.mobsAround(world, center, targeting.repelRadius())) {
      final ControlHold hold =
          ControlHold.fleeing(
              mob.getUuid(),
              center,
              targeting.repelDistance(),
              world.getTime() + targeting.repelDurationTicks());
      TRACKERS.of(world).add(hold);
      mob.setAttached(ControlPersistence.HOLD, hold);
      MobSteering.steer(mob, hold.anchor(), hold.steering());
      held++;
    }
    return held;
  }

  public static boolean enlist(
      final ServerWorld world,
      final MobEntity mob,
      @Nullable final LivingEntity protectedEntity,
      final AllegianceArrowConfig allegiance) {
    if (protectedEntity == null
        || !allegiance.turns()
        || !mob.isAlive()
        || mob == protectedEntity) {
      return false;
    }
    final ControlHold hold =
        ControlHold.defending(
            mob.getUuid(),
            mob.getBoundingBox().getCenter(),
            protectedEntity.getUuid(),
            allegiance.defendRadius(),
            world.getTime() + allegiance.durationTicks());
    TRACKERS.of(world).add(hold);
    mob.setAttached(ControlPersistence.HOLD, hold);
    applyTargeting(world, mob, hold);
    return true;
  }

  static void resume(final ServerWorld world, final ControlHold hold) {
    TRACKERS.of(world).add(hold);
  }

  public static List<ControlHold> holdsIn(final ServerWorld world) {
    final SessionTracker<ControlHold> tracker = TRACKERS.in(world);
    return tracker == null ? List.of() : tracker.all();
  }

  public static Optional<ControlHold> heldIn(final ServerWorld world, final MobEntity mob) {
    final SessionTracker<ControlHold> tracker = TRACKERS.in(world);
    return tracker == null ? Optional.empty() : Optional.ofNullable(tracker.get(mob.getUuid()));
  }

  private static void forget() {
    TRACKERS.clear();
    FleeBehaviour.forget();
    BatFlight.forgetAll();
    SteeringGrace.SLIMES.forgetAll();
  }

  private static void tick(final ServerWorld world) {
    final SessionTracker<ControlHold> tracker = TRACKERS.in(world);
    if (tracker == null || tracker.isEmpty()) {
      return;
    }
    tracker
        .removeIf(hold -> hold.hasExpired(world.getTime()))
        .forEach(hold -> handBack(world, hold));
    tracker.all().forEach(hold -> apply(world, tracker, hold));
  }

  private static void apply(
      final ServerWorld world, final SessionTracker<ControlHold> tracker, final ControlHold hold) {
    final MobEntity mob = mobIn(world, hold);
    if (mob == null) {
      tracker.remove(hold.mobId());
      SteeringGrace.SLIMES.release(hold.mobId());
      return;
    }
    if (hold.steering().targetPolicy().retargetsEveryTick() && !applyTargeting(world, mob, hold)) {
      tracker.remove(hold.mobId());
      handBack(world, hold);
      return;
    }
    if (hold.steering() == ControlSteering.FLEEING) {
      FleeBehaviour.keepFleeing(mob, hold);
    } else if (hold.steering().navigates()
        && hold.subjectId().isEmpty()
        && MobSteering.needsNewCourse(mob, hold.anchor())) {
      MobSteering.steer(mob, hold.anchor(), hold.steering());
    }
    if (hold.steering().navigates()) {
      MobSteering.keepPace(mob, hold.steering().speed());
    }
  }

  private static boolean applyTargeting(
      final ServerWorld world, final MobEntity mob, final ControlHold hold) {
    return switch (hold.steering().targetPolicy()) {
      case AIM_AT_SUBJECT -> aimAtSubject(world, mob, hold);
      case DEFEND_SUBJECT -> EscortBehaviour.defendSubject(world, mob, hold);
      case DROP_UNLESS_CORNERED -> true;
    };
  }

  private static boolean aimAtSubject(
      final ServerWorld world, final MobEntity mob, final ControlHold hold) {
    if (hold.subjectId().isEmpty()) {
      return true;
    }
    final LivingEntity subject = livingSubject(world, hold);
    if (subject == null) {
      return false;
    }
    MobAggression.aim(mob, subject == mob ? null : subject);
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
    FleeBehaviour.release(hold.mobId());
    SteeringGrace.SLIMES.release(hold.mobId());
    final MobEntity mob = mobIn(world, hold);
    if (mob == null) {
      return;
    }
    mob.removeAttached(ControlPersistence.HOLD);
    hold.subjectId()
        .map(world::getEntity)
        .ifPresent(subject -> MobAggression.forgive(mob, subject));
    if (hold.steering() != ControlSteering.FLEEING) {
      MobAggression.aim(mob, null);
    }
    if (hold.steering().navigates()) {
      MobSteering.halt(mob);
    }
  }

  private static MobEntity mobIn(final ServerWorld world, final ControlHold hold) {
    final Entity entity = world.getEntity(hold.mobId());
    return entity instanceof MobEntity mob && mob.isAlive() ? mob : null;
  }
}
