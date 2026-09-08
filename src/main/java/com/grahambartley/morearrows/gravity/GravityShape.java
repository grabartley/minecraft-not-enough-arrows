package com.grahambartley.morearrows.gravity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.util.math.BlockPos;

public final class GravityShape {

  private GravityShape() {}

  public static List<BlockPos> blocks(final BlockPos center, final int radius) {
    if (center == null) {
      return List.of();
    }
    if (radius <= 0) {
      return List.of(center);
    }

    final List<BlockPos> blocks = new ArrayList<>();
    for (int offsetX = -radius; offsetX <= radius; offsetX++) {
      for (int offsetY = -radius; offsetY <= radius; offsetY++) {
        for (int offsetZ = -radius; offsetZ <= radius; offsetZ++) {
          if (offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ <= radius * radius) {
            blocks.add(center.add(offsetX, offsetY, offsetZ));
          }
        }
      }
    }
    blocks.sort(nearestFirst(center));
    return List.copyOf(blocks);
  }

  private static Comparator<BlockPos> nearestFirst(final BlockPos center) {
    return Comparator.comparingLong((BlockPos block) -> squaredDistance(center, block))
        .thenComparingInt(BlockPos::getX)
        .thenComparingInt(BlockPos::getY)
        .thenComparingInt(BlockPos::getZ);
  }

  private static long squaredDistance(final BlockPos center, final BlockPos block) {
    final long offsetX = (long) block.getX() - center.getX();
    final long offsetY = (long) block.getY() - center.getY();
    final long offsetZ = (long) block.getZ() - center.getZ();
    return offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ;
  }
}
