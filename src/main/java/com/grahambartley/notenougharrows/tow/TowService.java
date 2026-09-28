package com.grahambartley.notenougharrows.tow;

import com.grahambartley.notenougharrows.config.EnderArrowConfig;
import com.grahambartley.notenougharrows.config.TowArrowConfig;
import com.grahambartley.notenougharrows.grapple.GrappleEnding;
import com.grahambartley.notenougharrows.grapple.GrappleFlightCheck;
import com.grahambartley.notenougharrows.grapple.GrappleService;
import com.grahambartley.notenougharrows.server.PlayerExit;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.grahambartley.notenougharrows.world.Reach;
import com.grahambartley.notenougharrows.zipline.RideEnding;
import com.grahambartley.notenougharrows.zipline.RideService;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class TowService {
  private static final Map<RegistryKey<World>, TowTracker> TRACKERS = new HashMap<>();

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
        || !TowTargets.isTowable(target, ender.recallAffectsPlayers())
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
    trackerFor(world)
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
    final TowTracker tracker = trackerIn(world);
    return tracker == null ? null : tracker.towOf(targetId);
  }

  @Nullable
  public static TowSession end(
      @Nullable final ServerWorld world, @Nullable final UUID targetId, final TowEnding ending) {
    final TowTracker tracker = trackerIn(world);
    if (tracker == null) {
      return null;
    }
    final TowSession ended = tracker.remove(targetId);
    if (ended != null
        && ending.stopsTheTarget()
        && world.getEntity(targetId) instanceof Entity target) {
      target.setVelocity(TowPull.stopped(target.getVelocity()));
      target.velocityModified = true;
    }
    return ended;
  }

  public static void stopTowingEverywhere(@Nullable final UUID entityId) {
    TRACKERS.values().forEach(tracker -> tracker.removeInvolving(entityId));
  }

  public static void forget() {
    TRACKERS.clear();
  }

  private static void towIn(final ServerWorld world) {
    final TowTracker tracker = trackerIn(world);
    if (tracker == null || tracker.isEmpty()) {
      return;
    }
    final double topSpeed = ServerConfigService.get().traversal().tow().speed();
    for (final TowSession session : tracker.tows()) {
      final TowEnding ending = towOnce(world, tracker, session, topSpeed);
      if (ending != null) {
        end(world, session.targetId(), ending);
      }
    }
  }

  @Nullable
  private static TowEnding towOnce(
      final ServerWorld world,
      final TowTracker tracker,
      final TowSession session,
      final double topSpeed) {
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
    drag(target, shooter, TowPull.speedAt(session.pulledTicks(), topSpeed));
    return null;
  }

  private static void drag(final Entity target, final PlayerEntity shooter, final double speed) {
    target.setVelocity(
        TowPull.velocity(target.getPos(), shooter.getPos(), target.getVelocity(), speed));
    target.velocityModified = true;
    if (target instanceof ServerPlayerEntity player) {
      GrappleFlightCheck.clearFloatingCountFor(player);
    }
  }

  private static boolean isGone(@Nullable final Entity entity) {
    return entity == null
        || entity.isRemoved()
        || (entity instanceof LivingEntity living && !living.isAlive());
  }

  @Nullable
  private static TowTracker trackerIn(@Nullable final ServerWorld world) {
    return world == null ? null : TRACKERS.get(world.getRegistryKey());
  }

  private static TowTracker trackerFor(final ServerWorld world) {
    return TRACKERS.computeIfAbsent(world.getRegistryKey(), key -> new TowTracker());
  }
}
