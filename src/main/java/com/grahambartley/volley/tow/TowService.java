package com.grahambartley.volley.tow;

import com.grahambartley.volley.config.EnderArrowConfig;
import com.grahambartley.volley.config.TowArrowConfig;
import com.grahambartley.volley.ender.RecallTargets;
import com.grahambartley.volley.grapple.GrappleEnding;
import com.grahambartley.volley.grapple.GrappleService;
import com.grahambartley.volley.server.PlayerExit;
import com.grahambartley.volley.server.ServerConfigService;
import com.grahambartley.volley.world.PerWorld;
import com.grahambartley.volley.world.Propel;
import com.grahambartley.volley.world.Reach;
import com.grahambartley.volley.world.SessionTracker;
import com.grahambartley.volley.zipline.RideEnding;
import com.grahambartley.volley.zipline.RideService;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public final class TowService {
  private static final PerWorld<SessionTracker<TowSession>> TRACKERS =
      new PerWorld<>(() -> new SessionTracker<>(TowSession::targetId, TowSession::hasExpired));

  private TowService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(TowService::towIn);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> forget());
    PlayerExit.whenLeaving(TowService::stopTowingEverywhere);
  }

  public static boolean start(
      @Nullable final ServerWorld world,
      @Nullable final PlayerEntity shooter,
      @Nullable final Entity target) {
    final var config = ServerConfigService.get();
    return start(world, shooter, target, config.traversal().tow(), config.ender());
  }

  public static boolean start(
      @Nullable final ServerWorld world,
      @Nullable final PlayerEntity shooter,
      @Nullable final Entity target,
      final TowArrowConfig tow,
      final EnderArrowConfig ender) {
    if (world == null
        || shooter == null
        || target == null
        || tow == null
        || ender == null
        || target == shooter
        || !shooter.isAlive()
        || !RecallTargets.mayMove(target, ender.recallAffectsPlayers())
        || !Reach.isWithin(target.getPos(), shooter.getPos(), tow.rangeBlocks())) {
      return false;
    }
    if (target.hasVehicle()) {
      target.stopRiding();
    }
    end(world, target.getUuid(), TowEnding.REPLACED);
    if (target instanceof PlayerEntity) {
      RideService.end(world, target.getUuid(), RideEnding.REPLACED);
      GrappleService.end(world, target.getUuid(), GrappleEnding.CANCELLED);
    }
    TRACKERS
        .of(world)
        .add(
            TowSession.beginning(
                target.getUuid(),
                shooter.getUuid(),
                tow.maxTicks(),
                TowPull.horizontalDistance(target.getPos(), shooter.getPos())));
    return true;
  }

  @Nullable
  public static TowSession towOf(@Nullable final ServerWorld world, @Nullable final UUID targetId) {
    final SessionTracker<TowSession> tracker = TRACKERS.in(world);
    return tracker == null ? null : tracker.get(targetId);
  }

  @Nullable
  public static TowSession end(
      @Nullable final ServerWorld world, @Nullable final UUID targetId, final TowEnding ending) {
    final SessionTracker<TowSession> tracker = TRACKERS.in(world);
    if (tracker == null) {
      return null;
    }
    final TowSession ended = tracker.remove(targetId);
    if (ended != null
        && ending.stopsTheTarget()
        && world.getEntity(targetId) instanceof Entity target) {
      Propel.drive(target, TowPull.stopped(target.getVelocity()));
    }
    return ended;
  }

  public static void stopTowingEverywhere(@Nullable final UUID entityId) {
    TRACKERS.all().forEach(tracker -> tracker.removeIf(tow -> tow.involves(entityId)));
  }

  public static void forget() {
    TRACKERS.clear();
  }

  private static void towIn(final ServerWorld world) {
    final SessionTracker<TowSession> tracker = TRACKERS.in(world);
    if (tracker == null || tracker.isEmpty()) {
      return;
    }
    final TowArrowConfig tow = ServerConfigService.get().traversal().tow();
    for (final TowSession session : tracker.all()) {
      final TowEnding ending = towOnce(world, tracker, session, tow);
      if (ending != null) {
        end(world, session.targetId(), ending);
      }
    }
  }

  @Nullable
  private static TowEnding towOnce(
      final ServerWorld world,
      final SessionTracker<TowSession> tracker,
      final TowSession session,
      final TowArrowConfig tow) {
    final Entity target = world.getEntity(session.targetId());
    if (isGone(target)) {
      return TowEnding.TARGET_GONE;
    }
    if (!(world.getEntity(session.shooterId()) instanceof PlayerEntity shooter)
        || isGone(shooter)) {
      return TowEnding.SHOOTER_GONE;
    }
    if (target instanceof PlayerEntity
        && (GrappleService.sessionOf(world, target.getUuid()) != null
            || RideService.rideOf(world, target.getUuid()) != null)) {
      return TowEnding.REPLACED;
    }
    if (!Reach.isWithin(target.getPos(), shooter.getPos(), tow.rangeBlocks())) {
      return TowEnding.OUT_OF_REACH;
    }
    if (TowPull.hasArrived(target.getPos(), shooter.getPos())) {
      return TowEnding.ARRIVED;
    }
    final TowSession pulled =
        session.pulled(TowPull.horizontalDistance(target.getPos(), shooter.getPos()));
    if (pulled.hasExpired()) {
      return TowEnding.OUT_OF_TIME;
    }
    if (pulled.hasStopped()) {
      return TowEnding.OBSTRUCTED;
    }
    tracker.add(pulled);
    drag(target, shooter, TowPull.speedAt(session.pulledTicks(), tow.speed()));
    return null;
  }

  private static void drag(final Entity target, final PlayerEntity shooter, final double speed) {
    Propel.drive(
        target, TowPull.velocity(target.getPos(), shooter.getPos(), target.getVelocity(), speed));
  }

  private static boolean isGone(@Nullable final Entity entity) {
    return entity == null
        || entity.isRemoved()
        || (entity instanceof LivingEntity living && !living.isAlive());
  }
}
