package com.grahambartley.morearrows.grapple;

import com.grahambartley.morearrows.anchor.AnchorService;
import com.grahambartley.morearrows.config.GrappleArrowConfig;
import com.grahambartley.morearrows.server.ServerConfigService;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
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
    ServerLivingEntityEvents.AFTER_DEATH.register(
        (entity, damageSource) -> {
          if (entity instanceof ServerPlayerEntity player) {
            releaseEverywhere(player.getUuid());
          }
        });
    ServerPlayConnectionEvents.DISCONNECT.register(
        (handler, server) -> releaseEverywhere(handler.getPlayer().getUuid()));
  }

  @Nullable
  public static GrappleSession start(
      @Nullable final ServerWorld world,
      @Nullable final PlayerEntity player,
      @Nullable final BlockPos anchorPos) {
    if (world == null || player == null || anchorPos == null) {
      return null;
    }

    final GrappleArrowConfig config = ServerConfigService.get().grapple();
    final Vec3d origin = pullOrigin(player);
    final Vec3d target = Vec3d.ofCenter(anchorPos);
    if (!GrapplePull.isWithinRange(origin, target, config.maxRangeBlocks())) {
      return null;
    }
    if (AnchorService.anchor(world, player.getUuid(), anchorPos) == null) {
      return null;
    }

    final GrappleSession session =
        new GrappleSession(
            player.getUuid(),
            anchorPos,
            GrapplePull.lifetimeTicks(origin, target, config.pullSpeed()));
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
    final GrappleTracker tracker = trackerIn(world);
    if (tracker == null) {
      return null;
    }
    final GrappleSession released = tracker.remove(playerId);
    if (released != null) {
      AnchorService.release(world, playerId);
    }
    return released;
  }

  public static void releaseEverywhere(@Nullable final UUID playerId) {
    TRACKERS.values().forEach(tracker -> tracker.remove(playerId));
  }

  public static void forget() {
    TRACKERS.clear();
  }

  private static void pullGrapplesIn(final ServerWorld world) {
    final GrappleTracker tracker = trackerIn(world);
    if (tracker == null || tracker.isEmpty()) {
      return;
    }

    final float pullSpeed = ServerConfigService.get().grapple().pullSpeed();
    for (final GrappleSession session : tracker.sessions()) {
      if (!(world.getEntity(session.playerId()) instanceof ServerPlayerEntity player)
          || player.isRemoved()
          || !session.holdsOnto(AnchorService.anchorOf(world, session.playerId()))) {
        release(world, session.playerId());
        continue;
      }

      final GrappleSession pulled = session.pulled();
      if (pulled.hasExpired() || GrapplePull.hasArrived(pullOrigin(player), session.target())) {
        release(world, session.playerId());
        continue;
      }

      tracker.add(pulled);
      pullTowardAnchor(player, session.target(), pullSpeed);
    }
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
