package com.grahambartley.notenougharrows.social;

import net.minecraft.util.math.Vec3d;

public final class MagnetSteering {
  public static final double TOP_SPEED = 0.6;
  public static final double ARRIVAL_DISTANCE = 1.0;
  public static final int MAX_PULL_TICKS = 100;

  private static final double CLOSING_FRACTION = 0.5;

  private MagnetSteering() {}

  public static Vec3d velocityToward(final Vec3d position, final Vec3d destination) {
    final Vec3d toward = destination.subtract(position);
    final double distance = toward.length();
    if (distance < 1.0E-4) {
      return Vec3d.ZERO;
    }
    final double speed = Math.min(TOP_SPEED, distance * CLOSING_FRACTION);
    return toward.multiply(speed / distance);
  }

  public static boolean hasArrived(final Vec3d position, final Vec3d destination) {
    return position.squaredDistanceTo(destination) <= ARRIVAL_DISTANCE * ARRIVAL_DISTANCE;
  }

  public static boolean isWithinReach(final Vec3d position, final Vec3d impact, final int radius) {
    return radius > 0 && position.squaredDistanceTo(impact) <= (double) radius * radius;
  }
}
