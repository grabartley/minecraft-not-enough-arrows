package com.grahambartley.notenougharrows.grapple;

import com.grahambartley.notenougharrows.anchor.AnchorService;
import com.grahambartley.notenougharrows.anchor.AnchorSite;
import com.grahambartley.notenougharrows.config.GrappleArrowConfig;
import com.grahambartley.notenougharrows.server.PlayerExit;
import com.grahambartley.notenougharrows.server.ServerConfigService;
import com.grahambartley.notenougharrows.world.Reach;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class GrappleService {
  private static final Map<RegistryKey<World>, GrappleTracker> TRACKERS = new HashMap<>();

  private GrappleService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(GrappleService::pullGrapplesIn);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> forget());
    PlayerExit.whenLeaving(GrappleService::stopPullingEverywhere);
  }

  @Nullable
  public static GrappleSession start(
      @Nullable final ServerWorld world,
      @Nullable final PlayerEntity player,
      @Nullable final BlockPos anchorPos,
      @Nullable final UUID arrowId) {
    if (world == null || player == null || anchorPos == null) {
      return null;
    }

    final GrappleArrowConfig config = ServerConfigService.get().grapple();
    final Vec3d origin = pullOrigin(player);
    final Vec3d target = Vec3d.ofCenter(anchorPos);
    if (!GrapplePull.isWithinRange(origin, target, config.maxRangeBlocks())
        || !AnchorSite.isSuitable(world, anchorPos)) {
      return null;
    }

    end(world, player.getUuid(), GrappleEnding.CANCELLED);
    if (AnchorService.anchor(world, player.getUuid(), anchorPos) == null) {
      return null;
    }

    final GrappleSession session =
        GrappleSession.beginning(
            player.getUuid(),
            arrowId,
            anchorPos,
            GrapplePull.lifetimeTicks(
                origin, target, config.pullSpeed(), config.pullAcceleration()),
            Reach.between(origin, target));
    trackerFor(world).add(session);
    return session;
  }

  @Nullable
  public static GrappleSession sessionOf(
      @Nullable final ServerWorld world, @Nullable final UUID playerId) {
    final GrappleTracker tracker = trackerIn(world);
    return tracker == null ? null : tracker.sessionOf(playerId);
  }

  @Nullable
  public static GrappleSession release(
      @Nullable final ServerWorld world, @Nullable final UUID playerId) {
    return end(world, playerId, GrappleEnding.CANCELLED);
  }

  @Nullable
  public static GrappleSession end(
      @Nullable final ServerWorld world,
      @Nullable final UUID playerId,
      final GrappleEnding ending) {
    final GrappleTracker tracker = trackerIn(world);
    return tracker == null ? null : endIn(world, tracker, playerId, ending);
  }

  public static void stopPullingEverywhere(@Nullable final UUID playerId) {
    TRACKERS
        .values()
        .forEach(tracker -> endIn(null, tracker, playerId, GrappleEnding.SHOOTER_GONE));
  }

  public static void forget() {
    TRACKERS.clear();
  }

  @Nullable
  private static GrappleSession endIn(
      @Nullable final ServerWorld world,
      final GrappleTracker tracker,
      @Nullable final UUID playerId,
      final GrappleEnding ending) {
    final GrappleSession ended = tracker.remove(playerId);
    if (ended == null) {
      return null;
    }
    AnchorService.release(world, playerId);
    GrappleArrival.settle(world, ended, ending);
    return ended;
  }

  private static void pullGrapplesIn(final ServerWorld world) {
    final GrappleTracker tracker = trackerIn(world);
    if (tracker == null || tracker.isEmpty()) {
      return;
    }

    final GrappleArrowConfig config = ServerConfigService.get().grapple();
    for (final GrappleSession session : tracker.sessions()) {
      final GrappleEnding ending = pullOnce(world, tracker, session, config);
      if (ending != null) {
        end(world, session.playerId(), ending);
      }
    }
  }

  @Nullable
  private static GrappleEnding pullOnce(
      final ServerWorld world,
      final GrappleTracker tracker,
      final GrappleSession session,
      final GrappleArrowConfig config) {
    if (!(world.getEntity(session.playerId()) instanceof ServerPlayerEntity player)
        || player.isRemoved()) {
      return GrappleEnding.SHOOTER_GONE;
    }
    if (!session.holdsOnto(AnchorService.anchorOf(world, session.playerId()))) {
      return GrappleEnding.ANCHOR_LOST;
    }

    final Vec3d origin = pullOrigin(player);
    final Vec3d target = session.target();
    if (GrapplePull.hasArrived(origin, target)) {
      return GrappleEnding.ARRIVED;
    }

    final GrappleSession pulled = session.pulled(Reach.between(origin, target));
    if (pulled.hasExpired()) {
      return GrappleEnding.OUT_OF_TIME;
    }
    if (pulled.hasStopped()) {
      return GrappleEnding.OBSTRUCTED;
    }

    tracker.add(pulled);
    pullTowardAnchor(
        player,
        target,
        GrapplePull.speedAt(session.pulledTicks(), config.pullSpeed(), config.pullAcceleration()));
    return null;
  }

  private static void pullTowardAnchor(
      final ServerPlayerEntity player, final Vec3d target, final double pullSpeed) {
    player.setVelocity(
        GrapplePull.velocity(pullOrigin(player), target, pullSpeed, player.getFinalGravity()));
    player.velocityModified = true;
    GrappleFlightCheck.clearFloatingCountFor(player);
  }

  private static Vec3d pullOrigin(final PlayerEntity player) {
    return player.getBoundingBox().getCenter();
  }

  @Nullable
  private static GrappleTracker trackerIn(@Nullable final ServerWorld world) {
    return world == null ? null : TRACKERS.get(world.getRegistryKey());
  }

  private static GrappleTracker trackerFor(final ServerWorld world) {
    return TRACKERS.computeIfAbsent(world.getRegistryKey(), key -> new GrappleTracker());
  }
}
