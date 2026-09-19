package com.grahambartley.notenougharrows.combat;

import net.minecraft.util.math.Vec3d;

public final class HomingSteering {

  private HomingSteering() {}

  public static boolean withinCone(
      final Vec3d velocity, final Vec3d toTarget, final float coneDegrees) {
    if (isDegenerate(velocity) || isDegenerate(toTarget) || coneDegrees <= 0.0f) {
      return false;
    }
    final double cosine = velocity.normalize().dotProduct(toTarget.normalize());
    return cosine >= Math.cos(Math.toRadians(coneDegrees / 2.0));
  }

  public static Vec3d steer(final Vec3d velocity, final Vec3d toTarget, final float turnRate) {
    if (isDegenerate(velocity) || isDegenerate(toTarget) || turnRate <= 0.0f) {
      return velocity == null ? Vec3d.ZERO : velocity;
    }

    final double speed = velocity.length();
    final double blend = Math.min(1.0f, turnRate);
    final Vec3d blended =
        velocity.normalize().multiply(1.0 - blend).add(toTarget.normalize().multiply(blend));
    if (isDegenerate(blended)) {
      return velocity;
    }
    return blended.normalize().multiply(speed);
  }

  private static boolean isDegenerate(final Vec3d vector) {
    return vector == null || vector.lengthSquared() <= 0.0;
  }
}
