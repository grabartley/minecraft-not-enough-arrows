package com.grahambartley.notenougharrows.zipline;

import com.grahambartley.notenougharrows.grapple.GrappleProgress;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.util.math.Vec3d;

public record RideSession(
    UUID riderId,
    UUID spanId,
    Vec3d from,
    Vec3d to,
    int remainingTicks,
    int riddenTicks,
    GrappleProgress progress) {

  public RideSession {
    Objects.requireNonNull(riderId, "A ride needs its rider");
    Objects.requireNonNull(spanId, "A ride needs the span it runs along");
    Objects.requireNonNull(from, "A ride needs the end it set out from");
    Objects.requireNonNull(to, "A ride needs the end it is heading for");
    Objects.requireNonNull(progress, "A ride needs to know how close it has come");
    remainingTicks = Math.max(0, remainingTicks);
    riddenTicks = Math.max(0, riddenTicks);
  }

  public static RideSession beginning(
      final UUID riderId,
      final UUID spanId,
      final Vec3d from,
      final Vec3d to,
      final int lifetimeTicks,
      final double startingRemaining) {
    return new RideSession(
        riderId, spanId, from, to, lifetimeTicks, 0, GrappleProgress.startingAt(startingRemaining));
  }

  public static RideSession toTheFarEnd(
      final UUID riderId, final Span span, final Vec3d grip, final double topSpeed) {
    final boolean firstIsNearer =
        span.firstEnd().squaredDistanceTo(grip) <= span.lastEnd().squaredDistanceTo(grip);
    final Vec3d from = firstIsNearer ? span.firstEnd() : span.lastEnd();
    final Vec3d to = firstIsNearer ? span.lastEnd() : span.firstEnd();
    return beginning(
        riderId,
        span.id(),
        from,
        to,
        RidePath.lifetimeTicks(from, to, topSpeed),
        RidePath.remaining(from, to, grip));
  }

  public boolean hasExpired() {
    return remainingTicks == 0;
  }

  public boolean hasStopped() {
    return progress.hasStopped();
  }

  public RideSession rode(final double remainingNow) {
    return new RideSession(
        riderId,
        spanId,
        from,
        to,
        remainingTicks - 1,
        riddenTicks + 1,
        progress.closedTo(remainingNow));
  }
}
