package com.grahambartley.volley.arrow;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class FaceClearance {
  public static final double CLEARANCE = 0.25;

  private FaceClearance() {}

  public static BlockPos blockInFrontOf(final BlockHitResult hit) {
    return hit.getBlockPos().offset(hit.getSide());
  }

  public static Vec3d groundInFrontOf(final BlockHitResult hit) {
    return Vec3d.ofBottomCenter(blockInFrontOf(hit));
  }

  public static Vec3d inFrontOf(final Vec3d hit, final Direction face) {
    return hit.add(Vec3d.of(face.getVector()).multiply(CLEARANCE));
  }
}
