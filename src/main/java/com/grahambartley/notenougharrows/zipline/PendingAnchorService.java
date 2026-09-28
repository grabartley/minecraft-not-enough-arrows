package com.grahambartley.notenougharrows.zipline;

import com.grahambartley.notenougharrows.anchor.AnchorSite;
import com.grahambartley.notenougharrows.server.PlayerExit;
import java.util.Optional;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public final class PendingAnchorService {
  private static final PendingAnchors PENDING = new PendingAnchors();

  private PendingAnchorService() {}

  public static void register() {
    ServerTickEvents.END_WORLD_TICK.register(PendingAnchorService::dropExpiredIn);
    ServerLifecycleEvents.SERVER_STOPPED.register(server -> forget());
    PlayerExit.whenLeaving(PendingAnchorService::discard);
  }

  public static Optional<PendingAnchor> hold(
      final ServerWorld world,
      final PlayerEntity owner,
      final BlockPos pos,
      final UUID arrowId,
      final int windowTicks) {
    final Identifier blockId = AnchorSite.blockIdAt(world, pos);
    if (owner == null || arrowId == null || blockId == null || windowTicks <= 0) {
      return Optional.empty();
    }
    final PendingAnchor anchor =
        new PendingAnchor(
            world.getRegistryKey().getValue(),
            pos,
            blockId,
            arrowId,
            world.getTime() + windowTicks);
    PENDING.hold(owner.getUuid(), anchor);
    return Optional.of(anchor);
  }

  public static Optional<PendingAnchor> claim(final ServerWorld world, final PlayerEntity owner) {
    if (world == null || owner == null) {
      return Optional.empty();
    }
    return PENDING
        .take(owner.getUuid())
        .filter(anchor -> anchor.isIn(world.getRegistryKey().getValue()))
        .filter(anchor -> !anchor.hasExpired(world.getTime()))
        .filter(anchor -> anchor.holdsOnto(AnchorSite.blockIdAt(world, anchor.pos())));
  }

  public static Optional<PendingAnchor> pendingFor(@Nullable final UUID ownerId) {
    return PENDING.of(ownerId);
  }

  public static void discard(@Nullable final UUID ownerId) {
    PENDING.take(ownerId);
  }

  public static void forget() {
    PENDING.clear();
  }

  private static void dropExpiredIn(final ServerWorld world) {
    if (!PENDING.isEmpty()) {
      PENDING.dropExpiredIn(world.getRegistryKey().getValue(), world.getTime());
    }
  }
}
