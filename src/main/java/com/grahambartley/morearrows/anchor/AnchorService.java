package com.grahambartley.morearrows.anchor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class AnchorService {
  public static final int DEFAULT_LIFETIME_TICKS = 20 * 60;

  private static final Map<RegistryKey<World>, AnchorTracker> TRACKERS = new HashMap<>();

  private AnchorService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(AnchorService::dropLostAnchorsIn);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> forget());
    ServerPlayConnectionEvents.DISCONNECT.register(
        (handler, server) -> releaseEverywhere(handler.getPlayer().getUuid()));
  }

  @Nullable
  public static BlockAnchor anchor(
      final ServerWorld world, final UUID ownerId, final BlockPos pos) {
    return anchor(world, ownerId, pos, DEFAULT_LIFETIME_TICKS);
  }

  @Nullable
  public static BlockAnchor anchor(
      @Nullable final ServerWorld world,
      @Nullable final UUID ownerId,
      @Nullable final BlockPos pos,
      final int lifetimeTicks) {
    if (world == null || ownerId == null || pos == null || lifetimeTicks <= 0) {
      return null;
    }
    if (!AnchorSite.isSuitable(world, pos)) {
      return null;
    }

    final Identifier blockId = AnchorSite.blockIdAt(world, pos);
    if (blockId == null) {
      return null;
    }

    final BlockAnchor anchor =
        new BlockAnchor(ownerId, pos, blockId, world.getTime() + lifetimeTicks);
    trackerFor(world).add(anchor);
    return anchor;
  }

  @Nullable
  public static BlockAnchor anchorOf(
      @Nullable final ServerWorld world, @Nullable final UUID ownerId) {
    final AnchorTracker tracker = trackerIn(world);
    return tracker == null ? null : tracker.anchorOf(ownerId);
  }

  @Nullable
  public static BlockAnchor release(
      @Nullable final ServerWorld world, @Nullable final UUID ownerId) {
    final AnchorTracker tracker = trackerIn(world);
    return tracker == null ? null : tracker.remove(ownerId);
  }

  public static void releaseEverywhere(@Nullable final UUID ownerId) {
    TRACKERS.values().forEach(tracker -> tracker.remove(ownerId));
  }

  public static List<BlockAnchor> anchorsIn(@Nullable final ServerWorld world) {
    final AnchorTracker tracker = trackerIn(world);
    return tracker == null ? List.of() : tracker.anchors();
  }

  public static void forget() {
    TRACKERS.clear();
  }

  private static void dropLostAnchorsIn(final ServerWorld world) {
    final AnchorTracker tracker = trackerIn(world);
    if (tracker == null || tracker.isEmpty()) {
      return;
    }
    tracker.takeIf(anchor -> anchor.hasExpired(world.getTime()) || !stillHolds(world, anchor));
  }

  private static boolean stillHolds(final ServerWorld world, final BlockAnchor anchor) {
    return anchor.holdsOnto(AnchorSite.blockIdAt(world, anchor.pos()));
  }

  @Nullable
  private static AnchorTracker trackerIn(@Nullable final ServerWorld world) {
    return world == null ? null : TRACKERS.get(world.getRegistryKey());
  }

  private static AnchorTracker trackerFor(final ServerWorld world) {
    return TRACKERS.computeIfAbsent(world.getRegistryKey(), key -> new AnchorTracker());
  }
}
