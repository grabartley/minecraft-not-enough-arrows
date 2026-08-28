package com.grahambartley.morearrows.fire;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.util.math.BlockPos;

public final class FirePatchShape {

  private FirePatchShape() {}

  public static List<BlockPos> columns(final BlockPos center, final int radius) {
    if (center == null || radius <= 0) {
      return List.of();
    }

    final List<BlockPos> columns = new ArrayList<>();
    for (int offsetX = -radius; offsetX <= radius; offsetX++) {
      for (int offsetZ = -radius; offsetZ <= radius; offsetZ++) {
        if (offsetX * offsetX + offsetZ * offsetZ <= radius * radius) {
          columns.add(center.add(offsetX, 0, offsetZ));
        }
      }
    }
    columns.sort(nearestFirst(center));
    return List.copyOf(columns);
  }

  private static Comparator<BlockPos> nearestFirst(final BlockPos center) {
    return Comparator.comparingLong((BlockPos column) -> squaredDistance(center, column))
        .thenComparingInt(BlockPos::getX)
        .thenComparingInt(BlockPos::getZ);
  }

  private static long squaredDistance(final BlockPos center, final BlockPos column) {
    final long offsetX = (long) column.getX() - center.getX();
    final long offsetZ = (long) column.getZ() - center.getZ();
    return offsetX * offsetX + offsetZ * offsetZ;
  }
}
