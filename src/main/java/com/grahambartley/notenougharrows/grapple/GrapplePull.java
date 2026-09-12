package com.grahambartley.notenougharrows.grapple;

import com.grahambartley.notenougharrows.world.Reach;
import net.minecraft.util.math.Vec3d;

public final class GrapplePull {
  public static final double ARRIVAL_DISTANCE = 1.75;
  public static final double DESCENT_SPEED_CAP = 0.6;
  public static final int OVERRUN_GRACE_TICKS = 20;
  public static final int LONGEST_PULL_TICKS = 20 * 60;

  private GrapplePull() {}

  public static boolean hasArrived(final Vec3d puller, final Vec3d target) {
    return distanceBetween(puller, target) <= ARRIVAL_DISTANCE;
  }

  public static boolean isWithinRange(
      final Vec3d puller, final Vec3d target, final int maxRangeBlocks) {
    return Reach.isWithin(puller, target, maxRangeBlocks);
  }

  public static double cappedToward(final Vec3d puller, final Vec3d target, final double topSpeed) {
    return target.getY() < puller.getY() ? Math.min(topSpeed, DESCENT_SPEED_CAP) : topSpeed;
  }

  public static double speedAt(
      final int pulledTicks, final double topSpeed, final double acceleration) {
    if (topSpeed <= 0.0 || acceleration <= 0.0) {
      return 0.0;
    }
    return Math.min(topSpeed, acceleration * (Math.max(0, pulledTicks) + 1));
  }

  public static Vec3d velocity(
      final Vec3d puller, final Vec3d target, final double speed, final double gravity) {
    if (speed <= 0.0 || hasArrived(puller, target)) {
      return Vec3d.ZERO;
    }
    return target
        .subtract(puller)
        .normalize()
        .multiply(cappedToward(puller, target, speed))
        .add(0.0, Math.max(0.0, gravity), 0.0);
  }

  public static int lifetimeTicks(
      final Vec3d puller, final Vec3d target, final double topSpeed, final double acceleration) {
    final double reachableTopSpeed = cappedToward(puller, target, topSpeed);
    if (reachableTopSpeed <= 0.0 || acceleration <= 0.0) {
      return OVERRUN_GRACE_TICKS;
    }

    double remaining = distanceBetween(puller, target) - ARRIVAL_DISTANCE;
    int ticks = 0;
    while (remaining > 0.0 && ticks < LONGEST_PULL_TICKS) {
      remaining -= speedAt(ticks, reachableTopSpeed, acceleration);
      ticks++;
    }
    return ticks + OVERRUN_GRACE_TICKS;
  }

  private static double distanceBetween(final Vec3d puller, final Vec3d target) {
    return Reach.between(puller, target);
  }
}
