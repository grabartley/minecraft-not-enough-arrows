package com.grahambartley.notenougharrows.zipline;

import com.grahambartley.notenougharrows.grapple.GrapplePull;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class RidePath {
  public static final double GRIP_ABOVE_CENTER = 1.0;
  public static final double LOOK_AHEAD_BLOCKS = 2.0;
  public static final double ARRIVAL_DISTANCE = GrapplePull.ARRIVAL_DISTANCE;
  public static final double ACCELERATION_SHARE = 0.1;
  private static final double NEGLIGIBLE = 1.0E-6;

  private RidePath() {}

  public static Vec3d gripOf(final Vec3d center) {
    return center.add(0.0, GRIP_ABOVE_CENTER, 0.0);
  }

  public static double progress(final Vec3d from, final Vec3d to, final Vec3d grip) {
    final Vec3d line = to.subtract(from);
    final double lengthSquared = line.lengthSquared();
    if (lengthSquared < NEGLIGIBLE) {
      return 1.0;
    }
    return MathHelper.clamp(grip.subtract(from).dotProduct(line) / lengthSquared, 0.0, 1.0);
  }

  public static double remaining(final Vec3d from, final Vec3d to, final Vec3d grip) {
    return (1.0 - progress(from, to, grip)) * from.distanceTo(to);
  }

  public static boolean hasArrived(final Vec3d from, final Vec3d to, final Vec3d grip) {
    return remaining(from, to, grip) <= ARRIVAL_DISTANCE;
  }

  public static Vec3d aim(final Vec3d from, final Vec3d to, final Vec3d grip) {
    final double length = from.distanceTo(to);
    if (length < NEGLIGIBLE) {
      return to;
    }
    final double ahead = Math.min(1.0, progress(from, to, grip) + LOOK_AHEAD_BLOCKS / length);
    return from.lerp(to, ahead);
  }

  public static Vec3d velocity(
      final Vec3d from,
      final Vec3d to,
      final Vec3d grip,
      final double speed,
      final double gravity) {
    if (speed <= 0.0 || hasArrived(from, to, grip)) {
      return Vec3d.ZERO;
    }
    final Vec3d toward = aim(from, to, grip).subtract(grip);
    final Vec3d heading =
        toward.lengthSquared() < NEGLIGIBLE ? to.subtract(from).normalize() : toward.normalize();
    return heading.multiply(speed).add(0.0, Math.max(0.0, gravity), 0.0);
  }

  public static double speedAt(final int riddenTicks, final double topSpeed) {
    return GrapplePull.speedAt(riddenTicks, topSpeed, topSpeed * ACCELERATION_SHARE);
  }

  public static int lifetimeTicks(final Vec3d from, final Vec3d to, final double topSpeed) {
    if (topSpeed <= 0.0) {
      return GrapplePull.OVERRUN_GRACE_TICKS;
    }
    final double length = from.distanceTo(to);
    final int rampTicks = (int) Math.ceil(1.0 / ACCELERATION_SHARE);
    return (int) Math.ceil(length / topSpeed) + rampTicks + GrapplePull.OVERRUN_GRACE_TICKS;
  }
}
