package com.grahambartley.notenougharrows.world;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.BlockPos;

public final class BlockDisc {

  private BlockDisc() {}

  public static List<BlockPos> blocks(final BlockPos center, final int radius) {
    if (center == null) {
      return List.of();
    }
    if (radius <= 0) {
      return List.of(center);
    }

    final List<BlockPos> blocks = new ArrayList<>();
    for (int offsetX = -radius; offsetX <= radius; offsetX++) {
      for (int offsetZ = -radius; offsetZ <= radius; offsetZ++) {
        if (offsetX * offsetX + offsetZ * offsetZ <= radius * radius) {
          blocks.add(center.add(offsetX, 0, offsetZ));
        }
      }
    }
    blocks.sort(BlockOrder.nearestFirst(center));
    return List.copyOf(blocks);
  }
}
