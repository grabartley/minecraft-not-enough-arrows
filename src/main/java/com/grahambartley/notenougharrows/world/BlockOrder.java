package com.grahambartley.notenougharrows.world;

import java.util.Comparator;
import net.minecraft.util.math.BlockPos;

public final class BlockOrder {

  private BlockOrder() {}

  public static Comparator<BlockPos> nearestFirst(final BlockPos center) {
    return Comparator.comparingLong((BlockPos block) -> squaredDistance(center, block))
        .thenComparingInt(BlockPos::getX)
        .thenComparingInt(BlockPos::getY)
        .thenComparingInt(BlockPos::getZ);
  }

  public static long squaredDistance(final BlockPos center, final BlockPos block) {
    final long offsetX = (long) block.getX() - center.getX();
    final long offsetY = (long) block.getY() - center.getY();
    final long offsetZ = (long) block.getZ() - center.getZ();
    return offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ;
  }
}
