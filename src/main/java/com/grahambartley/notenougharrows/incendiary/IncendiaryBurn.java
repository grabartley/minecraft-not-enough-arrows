package com.grahambartley.notenougharrows.incendiary;

import net.minecraft.util.math.Vec3d;

public final class IncendiaryBurn {
  public static final int TICKS_PER_SECOND = 20;

  private IncendiaryBurn() {}

  public static boolean reaches(final Vec3d center, final Vec3d target, final int radius) {
    if (center == null || target == null || radius <= 0) {
      return false;
    }
    return center.squaredDistanceTo(target) <= (double) radius * radius;
  }

  public static int igniteTicks(final int seconds) {
    return Math.max(0, seconds) * TICKS_PER_SECOND;
  }
}
