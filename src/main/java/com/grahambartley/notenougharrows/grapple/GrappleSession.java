package com.grahambartley.notenougharrows.grapple;

import com.grahambartley.notenougharrows.anchor.BlockAnchor;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public record GrappleSession(
    UUID playerId,
    @Nullable UUID arrowId,
    BlockPos anchor,
    int remainingTicks,
    int pulledTicks,
    GrappleProgress progress) {

  public GrappleSession {
    Objects.requireNonNull(playerId, "A grapple needs the player it pulls");
    Objects.requireNonNull(anchor, "A grapple needs the block it pulls toward");
    Objects.requireNonNull(progress, "A grapple needs to know how close it has come");
    anchor = anchor.toImmutable();
    remainingTicks = Math.max(0, remainingTicks);
    pulledTicks = Math.max(0, pulledTicks);
  }

  public static GrappleSession beginning(
      final UUID playerId,
      @Nullable final UUID arrowId,
      final BlockPos anchor,
      final int lifetimeTicks,
      final double startingDistance) {
    return new GrappleSession(
        playerId, arrowId, anchor, lifetimeTicks, 0, GrappleProgress.startingAt(startingDistance));
  }

  public boolean hasExpired() {
    return remainingTicks == 0;
  }

  public boolean hasStopped() {
    return progress.hasStopped();
  }

  public GrappleSession pulled(final double distanceNow) {
    return new GrappleSession(
        playerId,
        arrowId,
        anchor,
        remainingTicks - 1,
        pulledTicks + 1,
        progress.closedTo(distanceNow));
  }

  public Vec3d target() {
    return Vec3d.ofCenter(anchor);
  }

  public boolean holdsOnto(@Nullable final BlockAnchor held) {
    return held != null && anchor.equals(held.pos());
  }
}
