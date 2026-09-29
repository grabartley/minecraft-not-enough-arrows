package com.grahambartley.notenougharrows.chaos;

import net.minecraft.util.math.Vec3d;

public final class BoomerangFlight {
  public static final double TURN_BACK = -0.4;
  public static final double HOLD = 0.7;
  public static final double TOP_SPEED = 1.2;
  public static final double MIN_SPEED = 0.3;
  public static final double ARRIVAL_DISTANCE = 1.5;
  public static final int MAX_RETURN_TICKS = 200;

  private BoomerangFlight() {}

  public static Vec3d turnBack(final Vec3d velocity) {
    return velocity.multiply(TURN_BACK);
  }

  public static Vec3d steer(final Vec3d velocity, final Vec3d position, final Vec3d home) {
    final Vec3d toward = home.subtract(position);
    final double distance = toward.length();
    if (distance < 1.0E-4) {
      return Vec3d.ZERO;
    }
    final double speed = Math.max(MIN_SPEED, Math.min(TOP_SPEED, distance / 2.0));
    final Vec3d wanted = toward.multiply(speed / distance);
    return velocity.multiply(HOLD).add(wanted.multiply(1.0 - HOLD));
  }

  public static boolean hasArrived(final Vec3d position, final Vec3d home) {
    return position.squaredDistanceTo(home) <= ARRIVAL_DISTANCE * ARRIVAL_DISTANCE;
  }

  public static boolean hasRunOutOfTime(final int returnTicks) {
    return returnTicks >= MAX_RETURN_TICKS;
  }
}
