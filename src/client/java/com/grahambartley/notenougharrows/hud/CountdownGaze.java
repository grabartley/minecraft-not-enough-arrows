package com.grahambartley.notenougharrows.hud;

import net.minecraft.util.math.Vec3d;

public final class CountdownGaze {
  public static final float LOOK_TOLERANCE_DEGREES = 8.0f;
  public static final double MAX_DISTANCE = 48.0;

  private static final double LOOK_TOLERANCE_COSINE =
      Math.cos(Math.toRadians(LOOK_TOLERANCE_DEGREES));

  private CountdownGaze() {}

  public static boolean isLookingAt(final Vec3d eye, final Vec3d look, final Vec3d target) {
    if (eye == null || look == null || target == null) {
      return false;
    }

    final Vec3d toTarget = target.subtract(eye);
    final double distance = toTarget.length();
    if (distance <= 1.0E-4 || distance > MAX_DISTANCE) {
      return false;
    }

    final double lookLength = look.length();
    if (lookLength <= 1.0E-4) {
      return false;
    }

    return toTarget.dotProduct(look) / (distance * lookLength) >= LOOK_TOLERANCE_COSINE;
  }
}
