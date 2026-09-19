package com.grahambartley.notenougharrows.combat;

import net.minecraft.util.math.Vec3d;

public final class RailgunFlight {
  public static final double MINIMUM_GRAVITY = 0.001;

  private RailgunFlight() {}

  public static Vec3d launchVelocity(final Vec3d velocity, final float speedMultiplier) {
    if (velocity == null) {
      return Vec3d.ZERO;
    }
    return velocity.multiply(Math.max(1.0f, speedMultiplier));
  }

  public static double gravity(final double baseGravity, final float gravityFactor) {
    if (baseGravity <= 0.0) {
      return baseGravity;
    }
    return Math.max(MINIMUM_GRAVITY, baseGravity * gravityFactor);
  }
}
