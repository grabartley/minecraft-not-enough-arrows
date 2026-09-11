package com.grahambartley.notenougharrows.gravity;

import com.grahambartley.notenougharrows.world.BlockOrder;
import java.util.ArrayList;
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
    blocks.sort(BlockOrder.nearestFirst(center));
    return List.copyOf(blocks);
  }
}
