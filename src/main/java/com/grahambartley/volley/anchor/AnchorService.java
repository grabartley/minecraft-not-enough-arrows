package com.grahambartley.volley.anchor;

import com.grahambartley.volley.server.PlayerExit;
import com.grahambartley.volley.world.PerWorld;
import java.util.List;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class AnchorService {
  public static final int DEFAULT_LIFETIME_TICKS = 20 * 60;

  private static final PerWorld<AnchorTracker> TRACKERS = new PerWorld<>(() -> new AnchorTracker());

  private AnchorService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(AnchorService::dropLostAnchorsIn);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> forget());
    PlayerExit.whenLeaving(AnchorService::releaseEverywhere);
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
    TRACKERS.of(world).add(anchor);
    return anchor;
  }

  @Nullable
  public static BlockAnchor anchorOf(
      @Nullable final ServerWorld world, @Nullable final UUID ownerId) {
    final AnchorTracker tracker = TRACKERS.in(world);
    return tracker == null ? null : tracker.anchorOf(ownerId);
  }

  @Nullable
  public static BlockAnchor release(
      @Nullable final ServerWorld world, @Nullable final UUID ownerId) {
    final AnchorTracker tracker = TRACKERS.in(world);
    return tracker == null ? null : tracker.remove(ownerId);
  }

  public static void releaseEverywhere(@Nullable final UUID ownerId) {
    TRACKERS.all().forEach(tracker -> tracker.remove(ownerId));
  }

  public static List<BlockAnchor> anchorsIn(@Nullable final ServerWorld world) {
    final AnchorTracker tracker = TRACKERS.in(world);
    return tracker == null ? List.of() : tracker.anchors();
  }

  public static void forget() {
    TRACKERS.clear();
  }

  private static void dropLostAnchorsIn(final ServerWorld world) {
    final AnchorTracker tracker = TRACKERS.in(world);
    if (tracker == null || tracker.isEmpty()) {
      return;
    }
    tracker.takeIf(anchor -> anchor.hasExpired(world.getTime()) || !stillHolds(world, anchor));
  }

  private static boolean stillHolds(final ServerWorld world, final BlockAnchor anchor) {
    return anchor.holdsOnto(AnchorSite.blockIdAt(world, anchor.pos()));
  }
}
