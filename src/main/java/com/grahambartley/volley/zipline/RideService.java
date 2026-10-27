package com.grahambartley.volley.zipline;

import com.grahambartley.volley.grapple.GrappleEnding;
import com.grahambartley.volley.grapple.GrappleFallGuard;
import com.grahambartley.volley.grapple.GrappleService;
import com.grahambartley.volley.server.PlayerExit;
import com.grahambartley.volley.server.ServerConfigService;
import com.grahambartley.volley.tow.TowEnding;
import com.grahambartley.volley.tow.TowService;
import com.grahambartley.volley.world.PerWorld;
import com.grahambartley.volley.world.Propel;
import com.grahambartley.volley.world.SessionTracker;
import java.util.Optional;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class RideService {
  public static final int LET_GO_GRACE_TICKS = 5;
  private static final PerWorld<SessionTracker<RideSession>> TRACKERS =
      new PerWorld<>(() -> new SessionTracker<>(RideSession::riderId, RideSession::hasExpired));

  private RideService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(RideService::rideIn);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> forget());
    PlayerExit.whenLeaving(RideService::stopRidingEverywhere);
  }

  public static boolean board(
      @Nullable final ServerWorld world,
      @Nullable final ServerPlayerEntity rider,
      @Nullable final BlockPos cablePos) {
    if (world == null || rider == null || cablePos == null || !canHoldOn(rider)) {
      return false;
    }
    final Optional<Span> span = SpanService.spanAt(world, cablePos);
    if (span.isEmpty()) {
      return false;
    }
    final RideSession current = rideOf(world, rider.getUuid());
    if (current != null && current.spanId().equals(span.get().id())) {
      return true;
    }
    end(world, rider.getUuid(), RideEnding.REPLACED);
    GrappleService.end(world, rider.getUuid(), GrappleEnding.CANCELLED);
    TowService.end(world, rider.getUuid(), TowEnding.REPLACED);
    TRACKERS
        .of(world)
        .add(
            RideSession.toTheFarEnd(
                rider.getUuid(),
                span.get(),
                gripOf(rider),
                ServerConfigService.get().traversal().zipline().rideSpeed()));
    RideBroadcaster.started(rider);
    return true;
  }

  @Nullable
  public static RideSession rideOf(
      @Nullable final ServerWorld world, @Nullable final UUID riderId) {
    return TRACKERS.map(world, tracker -> tracker.get(riderId), null);
  }

  @Nullable
  public static RideSession end(
      @Nullable final ServerWorld world, @Nullable final UUID riderId, final RideEnding ending) {
    final SessionTracker<RideSession> tracker = TRACKERS.in(world);
    if (tracker == null) {
      return null;
    }
    final RideSession ended = tracker.remove(riderId);
    if (ended != null
        && world.getServer().getPlayerManager().getPlayer(riderId)
            instanceof ServerPlayerEntity rider) {
      RideBroadcaster.stopped(rider);
      if (ending.ownsTheFall()) {
        rider.onLanding();
        GrappleFallGuard.spare(riderId);
      }
    }
    return ended;
  }

  public static void stopRidingEverywhere(@Nullable final UUID riderId) {
    TRACKERS.all().forEach(tracker -> tracker.remove(riderId));
  }

  public static void forget() {
    TRACKERS.clear();
  }

  private static void rideIn(final ServerWorld world) {
    final SessionTracker<RideSession> tracker = TRACKERS.in(world);
    if (tracker == null || tracker.isEmpty()) {
      return;
    }
    final double topSpeed = ServerConfigService.get().traversal().zipline().rideSpeed();
    for (final RideSession session : tracker.all()) {
      final RideEnding ending = rideOnce(world, tracker, session, topSpeed);
      if (ending != null) {
        end(world, session.riderId(), ending);
      }
    }
  }

  @Nullable
  private static RideEnding rideOnce(
      final ServerWorld world,
      final SessionTracker<RideSession> tracker,
      final RideSession session,
      final double topSpeed) {
    if (!(world.getEntity(session.riderId()) instanceof ServerPlayerEntity rider)
        || rider.isRemoved()
        || !rider.isAlive()) {
      return RideEnding.RIDER_GONE;
    }
    if (GrappleService.sessionOf(world, session.riderId()) != null) {
      return RideEnding.REPLACED;
    }
    if (SpanService.find(world, session.spanId())
        .filter(span -> SpanService.isIntact(world, span))
        .isEmpty()) {
      return RideEnding.SPAN_LOST;
    }
    if (session.riddenTicks() >= LET_GO_GRACE_TICKS && rider.isSneaking()) {
      return RideEnding.LET_GO;
    }
    final Vec3d grip = gripOf(rider);
    if (!canHoldOn(rider) || RidePath.isThrownOff(session.from(), session.to(), grip)) {
      return RideEnding.THROWN_OFF;
    }
    if (RidePath.hasArrived(session.from(), session.to(), grip)) {
      return RideEnding.ARRIVED;
    }
    final RideSession rode = session.rode(RidePath.remaining(session.from(), session.to(), grip));
    if (rode.hasExpired()) {
      return RideEnding.OUT_OF_TIME;
    }
    if (rode.hasStopped()) {
      return RideEnding.OBSTRUCTED;
    }
    tracker.add(rode);
    carry(rider, session, grip, RidePath.speedAt(session.riddenTicks(), topSpeed));
    return null;
  }

  private static void carry(
      final ServerPlayerEntity rider,
      final RideSession session,
      final Vec3d grip,
      final double speed) {
    Propel.carry(
        rider,
        RidePath.velocity(session.from(), session.to(), grip, speed, rider.getFinalGravity()));
    rider.fallDistance = 0.0f;
  }

  private static boolean canHoldOn(final ServerPlayerEntity rider) {
    return !rider.isSpectator() && !rider.isSleeping() && !rider.hasVehicle();
  }

  private static Vec3d gripOf(final ServerPlayerEntity rider) {
    return RidePath.gripOf(rider.getBoundingBox().getCenter());
  }
}
