package com.grahambartley.notenougharrows.world;

import java.util.Objects;
import net.minecraft.util.math.Vec3d;

public final class Reach {

  private Reach() {}

  public static double between(final Vec3d from, final Vec3d to) {
    Objects.requireNonNull(from, "from");
    Objects.requireNonNull(to, "to");
    return from.distanceTo(to);
  }

  public static boolean isWithin(final Vec3d from, final Vec3d to, final int maxBlocks) {
    return maxBlocks > 0 && between(from, to) <= maxBlocks;
  }
}
