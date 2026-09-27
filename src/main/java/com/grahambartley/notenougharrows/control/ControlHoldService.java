package com.grahambartley.notenougharrows.control;

import com.grahambartley.notenougharrows.config.AllegianceArrowConfig;
import com.grahambartley.notenougharrows.config.TargetingArrowConfig;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class ControlHoldService {
  private static final double STEERING_SPEED = 1.1;
  private static final double FLEEING_SPEED = 1.5;
  private static final double ESCORT_SPEED = 1.25;

  private static final Map<RegistryKey<World>, ControlHoldTracker> TRACKERS = new HashMap<>();
  private static final Set<UUID> CORNERED = new HashSet<>();

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
      trackerFor(world).hold(hold);
      mob.setAttached(ControlPersistence.HOLD, hold);
      if (hold.subjectId().isEmpty()) {
        MobAggression.aim(mob, null);
        steer(mob, hold.anchor(), hold.steering());
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
      trackerFor(world).hold(hold);
      mob.setAttached(ControlPersistence.HOLD, hold);
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
    trackerFor(world).hold(hold);
    mob.setAttached(ControlPersistence.HOLD, hold);
    applyTargeting(world, mob, hold);
    return true;
  }

  static void resume(final ServerWorld world, final ControlHold hold) {
    trackerFor(world).hold(hold);
  }

  public static List<ControlHold> holdsIn(final ServerWorld world) {
    final ControlHoldTracker tracker = TRACKERS.get(world.getRegistryKey());
    return tracker == null ? List.of() : tracker.live();
  }

  public static Optional<ControlHold> heldIn(final ServerWorld world, final MobEntity mob) {
    final ControlHoldTracker tracker = TRACKERS.get(world.getRegistryKey());
    return tracker == null ? Optional.empty() : tracker.find(mob.getUuid());
  }

  public static boolean isCornered(final MobEntity mob) {
    return CORNERED.contains(mob.getUuid());
  }

  private static void forget() {
    TRACKERS.clear();
    CORNERED.clear();
    BatFlight.forgetAll();
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
    if (hold.steering() == ControlSteering.FLEEING) {
      keepFleeing(world, mob, hold);
    } else if (hold.steering().navigates()
        && hold.subjectId().isEmpty()
        && (!MobSteering.isUnderway(mob)
            || MobSteering.steersItself(mob)
            || MobSteering.hasWanderedOffCourse(mob, hold.anchor()))) {
      steer(mob, hold.anchor(), hold.steering());
    }
    if (hold.steering().navigates()) {
      MobSteering.keepPace(
          mob, hold.steering() == ControlSteering.FLEEING ? FLEEING_SPEED : STEERING_SPEED);
    }
  }

  private static void keepFleeing(
      final ServerWorld world, final MobEntity mob, final ControlHold hold) {
    if (hold.hasFledFarEnough(mob.getPos())) {
      MobSteering.halt(mob);
      CORNERED.remove(mob.getUuid());
      MobAggression.aim(mob, null);
      return;
    }
    final boolean pulledBackToAFight = mob.getTarget() != null;
    final boolean hasSomewhereToRun =
        pulledBackToAFight
                || MobSteering.steersItself(mob)
                || !MobSteering.isUnderway(mob)
                || MobSteering.hasWanderedOffCourse(mob, fleeDestination(mob, hold))
            ? steer(mob, hold.anchor(), hold.steering())
            : MobSteering.isUnderway(mob);
    if (hasSomewhereToRun) {
      CORNERED.remove(mob.getUuid());
      MobAggression.aim(mob, null);
    } else {
      CORNERED.add(mob.getUuid());
    }
  }

  private static boolean applyTargeting(
      final ServerWorld world, final MobEntity mob, final ControlHold hold) {
    return switch (hold.steering().targetPolicy()) {
      case AIM_AT_SUBJECT -> aimAtSubject(world, mob, hold);
      case DEFEND_SUBJECT -> defendSubject(world, mob, hold);
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

  private static boolean defendSubject(
      final ServerWorld world, final MobEntity mob, final ControlHold hold) {
    final Entity subject = hold.subjectId().map(world::getEntity).orElse(null);
    if (subject == null) {
      return true;
    }
    if (!(subject instanceof LivingEntity defended) || !defended.isAlive()) {
      return false;
    }
    final LivingEntity threat =
        DefenderTargets.threatTo(world, defended, mob, hold.reach()).orElse(null);
    MobAggression.aim(mob, threat);
    if (threat == null) {
      escort(world, mob, defended);
    }
    return true;
  }

  private static void escort(
      final ServerWorld world, final MobEntity mob, final LivingEntity defended) {
    final double squaredDistance = mob.squaredDistanceTo(defended);
    if (mob instanceof EndermanEntity && Escort.hasLostTrack(squaredDistance)) {
      mob.teleport(defended.getX(), defended.getY(), defended.getZ(), true);
      return;
    }
    if (Escort.isCloseEnough(squaredDistance, mob.getWidth())) {
      MobSteering.halt(mob);
    } else if ((Escort.shouldCloseIn(squaredDistance, mob.getWidth())
            || MobSteering.fliesAtRandom(mob))
        && (!MobSteering.isUnderway(mob)
            || MobSteering.steersItself(mob)
            || MobSteering.hasWanderedOffCourse(mob, defended.getPos()))) {
      MobSteering.moveTo(mob, defended.getPos(), ESCORT_SPEED);
    }
    MobSteering.keepPace(mob, ESCORT_SPEED);
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
    CORNERED.remove(hold.mobId());
    final MobEntity mob = mobIn(world, hold);
    if (mob == null) {
      return;
    }
    mob.removeAttached(ControlPersistence.HOLD);
    if (hold.steering() != ControlSteering.FLEEING) {
      MobAggression.aim(mob, null);
    }
    if (hold.steering().navigates()) {
      MobSteering.halt(mob);
    }
  }

  @Nullable
  private static Vec3d fleeDestination(final MobEntity mob, final ControlHold hold) {
    return hold.steering().destination(mob.getPos(), hold.anchor()).orElse(null);
  }

  private static boolean steer(
      final MobEntity mob, final Vec3d anchor, final ControlSteering steering) {
    final double speed = steering == ControlSteering.FLEEING ? FLEEING_SPEED : STEERING_SPEED;
    return steering
        .destination(mob.getPos(), anchor)
        .map(destination -> MobSteering.moveTo(mob, destination, speed))
        .orElse(false);
  }

  private static MobEntity mobIn(final ServerWorld world, final ControlHold hold) {
    final Entity entity = world.getEntity(hold.mobId());
    return entity instanceof MobEntity mob && mob.isAlive() ? mob : null;
  }

  private static ControlHoldTracker trackerFor(final ServerWorld world) {
    return TRACKERS.computeIfAbsent(world.getRegistryKey(), key -> new ControlHoldTracker());
  }
}
