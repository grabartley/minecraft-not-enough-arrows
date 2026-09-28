package com.grahambartley.notenougharrows.zipline;

import com.grahambartley.notenougharrows.grapple.GrapplePull;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class RidePath {
  public static final double GRIP_ABOVE_CENTER = 0.95;
  public static final double CATCH_UP_RATE = 0.5;
  public static final double CATCH_UP_MAX = 0.8;
  public static final double ARRIVAL_DISTANCE = GrapplePull.ARRIVAL_DISTANCE;
  public static final double ACCELERATION_SHARE = 0.1;
  public static final double THROWN_OFF_DISTANCE = 6.0;
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

  public static double offLine(final Vec3d from, final Vec3d to, final Vec3d grip) {
    return grip.distanceTo(nearestOnLine(from, to, grip));
  }

  public static boolean isThrownOff(final Vec3d from, final Vec3d to, final Vec3d grip) {
    return offLine(from, to, grip) > THROWN_OFF_DISTANCE;
  }

  public static boolean hasArrived(final Vec3d from, final Vec3d to, final Vec3d grip) {
    return remaining(from, to, grip) <= ARRIVAL_DISTANCE;
  }

  public static Vec3d nearestOnLine(final Vec3d from, final Vec3d to, final Vec3d grip) {
    return from.lerp(to, progress(from, to, grip));
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
    final Vec3d along = to.subtract(from).normalize().multiply(speed);
    Vec3d catchUp = nearestOnLine(from, to, grip).subtract(grip).multiply(CATCH_UP_RATE);
    if (catchUp.length() > CATCH_UP_MAX) {
      catchUp = catchUp.normalize().multiply(CATCH_UP_MAX);
    }
    return along.add(catchUp).add(0.0, Math.max(0.0, gravity), 0.0);
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
