package com.grahambartley.notenougharrows.traversal;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class BridgeLine {
  private static final double NEGLIGIBLE = 1.0E-9;

  private BridgeLine() {}

  public static List<BlockPos> toward(
      final BlockPos start, final double targetX, final double targetZ, final int lengthBlocks) {
    if (start == null || lengthBlocks <= 0) {
      return List.of();
    }
    final double dx = targetX - (start.getX() + 0.5);
    final double dz = targetZ - (start.getZ() + 0.5);
    if (Math.abs(dx) < NEGLIGIBLE && Math.abs(dz) < NEGLIGIBLE) {
      return List.of();
    }
    final int stepX = (int) Math.signum(dx);
    final int stepZ = (int) Math.signum(dz);
    final double intervalX =
        Math.abs(dx) < NEGLIGIBLE ? Double.POSITIVE_INFINITY : 1.0 / Math.abs(dx);
    final double intervalZ =
        Math.abs(dz) < NEGLIGIBLE ? Double.POSITIVE_INFINITY : 1.0 / Math.abs(dz);
    double nextX = intervalX / 2.0;
    double nextZ = intervalZ / 2.0;
    int x = start.getX();
    int z = start.getZ();
    final List<BlockPos> line = new ArrayList<>(lengthBlocks);
    while (line.size() < lengthBlocks && Math.min(nextX, nextZ) <= 1.0) {
      if (nextX <= nextZ) {
        x += stepX;
        nextX += intervalX;
      } else {
        z += stepZ;
        nextZ += intervalZ;
      }
      line.add(new BlockPos(x, start.getY(), z));
    }
    return List.copyOf(line);
  }

  public static Vec3d backAlong(final Vec3d impact, final Vec3d flight, final int lengthBlocks) {
    final Vec3d level = new Vec3d(flight.getX(), 0.0, flight.getZ());
    if (level.lengthSquared() < NEGLIGIBLE) {
      return impact;
    }
    return impact.subtract(level.normalize().multiply(lengthBlocks + 1.0));
  }
}
