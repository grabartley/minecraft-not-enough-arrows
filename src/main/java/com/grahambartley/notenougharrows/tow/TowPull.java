package com.grahambartley.notenougharrows.tow;

import com.grahambartley.notenougharrows.grapple.GrapplePull;
import net.minecraft.util.math.Vec3d;

public final class TowPull {
  public static final double ARRIVAL_DISTANCE = GrapplePull.ARRIVAL_DISTANCE;
  public static final double ACCELERATION_SHARE = 0.2;

  private TowPull() {}

  public static double horizontalDistance(final Vec3d target, final Vec3d shooter) {
    final double dx = shooter.getX() - target.getX();
    final double dz = shooter.getZ() - target.getZ();
    return Math.sqrt(dx * dx + dz * dz);
  }

  public static boolean hasArrived(final Vec3d target, final Vec3d shooter) {
    return horizontalDistance(target, shooter) <= ARRIVAL_DISTANCE;
  }

  public static Vec3d velocity(
      final Vec3d target, final Vec3d shooter, final Vec3d current, final double speed) {
    if (speed <= 0.0 || hasArrived(target, shooter)) {
      return new Vec3d(0.0, current.getY(), 0.0);
    }
    final Vec3d across =
        new Vec3d(shooter.getX() - target.getX(), 0.0, shooter.getZ() - target.getZ()).normalize();
    return new Vec3d(across.getX() * speed, current.getY(), across.getZ() * speed);
  }

  public static Vec3d stopped(final Vec3d current) {
    return new Vec3d(0.0, current.getY(), 0.0);
  }

  public static double speedAt(final int pulledTicks, final double topSpeed) {
    return GrapplePull.speedAt(pulledTicks, topSpeed, topSpeed * ACCELERATION_SHARE);
  }
}
