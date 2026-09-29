package com.grahambartley.notenougharrows.arrow;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class FaceClearance {
  public static final double CLEARANCE = 0.25;

  private FaceClearance() {}

  public static Vec3d inFrontOf(final Vec3d hit, final Direction face) {
    return hit.add(Vec3d.of(face.getVector()).multiply(CLEARANCE));
  }
}
