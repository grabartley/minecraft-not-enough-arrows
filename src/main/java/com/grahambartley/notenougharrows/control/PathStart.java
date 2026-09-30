package com.grahambartley.notenougharrows.control;

import java.util.function.IntFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public final class PathStart {

  private static final double HALF_A_BLOCK = 0.5;

  private PathStart() {}

  public static int firstNodeAhead(
      final IntFunction<BlockPos> nodeAt,
      final int current,
      final int length,
      final Box footprint) {
    int ahead = current;
    while (ahead < length - 1 && isUnderfoot(nodeAt.apply(ahead), footprint)) {
      ahead++;
    }
    return ahead;
  }

  static boolean isUnderfoot(final BlockPos node, final Box footprint) {
    return node.getY() + 1 > footprint.minY
        && node.getY() <= footprint.minY + HALF_A_BLOCK
        && node.getX() < footprint.maxX
        && node.getX() + 1 > footprint.minX
        && node.getZ() < footprint.maxZ
        && node.getZ() + 1 > footprint.minZ;
  }
}
