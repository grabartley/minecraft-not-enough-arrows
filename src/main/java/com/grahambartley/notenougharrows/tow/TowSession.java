package com.grahambartley.notenougharrows.tow;

import com.grahambartley.notenougharrows.grapple.GrappleProgress;
import java.util.Objects;
import java.util.UUID;

public record TowSession(
    UUID targetId, UUID shooterId, int remainingTicks, int pulledTicks, GrappleProgress progress) {

  public TowSession {
    Objects.requireNonNull(targetId, "A tow needs what it drags");
    Objects.requireNonNull(shooterId, "A tow needs who it drags toward");
    Objects.requireNonNull(progress, "A tow needs to know how close it has come");
    remainingTicks = Math.max(0, remainingTicks);
    pulledTicks = Math.max(0, pulledTicks);
  }

  public static TowSession beginning(
      final UUID targetId,
      final UUID shooterId,
      final int budgetTicks,
      final double startingDistance) {
    return new TowSession(
        targetId, shooterId, budgetTicks, 0, GrappleProgress.startingAt(startingDistance));
  }

  public boolean hasExpired() {
    return remainingTicks == 0;
  }

  public boolean hasStopped() {
    return progress.hasStopped();
  }

  public boolean involves(final UUID entityId) {
    return targetId.equals(entityId) || shooterId.equals(entityId);
  }

  public TowSession pulled(final double distanceNow) {
    return new TowSession(
        targetId, shooterId, remainingTicks - 1, pulledTicks + 1, progress.closedTo(distanceNow));
  }
}
