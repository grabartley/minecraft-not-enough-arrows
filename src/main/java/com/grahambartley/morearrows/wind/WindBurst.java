package com.grahambartley.morearrows.wind;

import net.minecraft.util.math.Vec3d;

public final class WindBurst {
  private static final Vec3d FALLBACK_DIRECTION = new Vec3d(0.0, 1.0, 0.0);

  private WindBurst() {}

  public static Vec3d push(
      final Vec3d center, final Vec3d target, final float radius, final float strength) {
    if (center == null || target == null || radius <= 0.0f || strength <= 0.0f) {
      return Vec3d.ZERO;
    }

    final double distance = center.distanceTo(target);
    if (distance > radius) {
      return Vec3d.ZERO;
    }

    return directionFrom(center, target, distance).multiply(strength * falloff(distance, radius));
  }

  public static double falloff(final double distance, final float radius) {
    if (radius <= 0.0f || distance >= radius) {
      return 0.0;
    }
    if (distance <= 0.0) {
      return 1.0;
    }
    return 1.0 - (distance / radius);
  }

  private static Vec3d directionFrom(
      final Vec3d center, final Vec3d target, final double distance) {
    return distance <= 0.0 ? FALLBACK_DIRECTION : target.subtract(center).multiply(1.0 / distance);
  }
}
