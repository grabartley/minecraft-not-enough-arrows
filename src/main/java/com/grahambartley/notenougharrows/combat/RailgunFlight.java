package com.grahambartley.notenougharrows.combat;

import net.minecraft.util.math.Vec3d;

public final class RailgunFlight {

  private RailgunFlight() {}

  public static Vec3d launchVelocity(final Vec3d velocity, final float speedMultiplier) {
    if (velocity == null) {
      return Vec3d.ZERO;
    }
    return velocity.multiply(Math.max(1.0f, speedMultiplier));
  }

  public static double gravity(final double baseGravity, final float gravityFactor) {
    return baseGravity * gravityFactor;
  }
}
