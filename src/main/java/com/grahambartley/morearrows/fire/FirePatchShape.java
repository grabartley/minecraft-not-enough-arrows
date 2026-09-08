package com.grahambartley.morearrows.fire;

import com.grahambartley.morearrows.world.BlockOrder;
import java.util.ArrayList;
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
    columns.sort(BlockOrder.nearestFirst(center));
    return List.copyOf(columns);
  }
}
