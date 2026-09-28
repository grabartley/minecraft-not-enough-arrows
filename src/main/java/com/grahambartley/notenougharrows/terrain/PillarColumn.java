package com.grahambartley.notenougharrows.terrain;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.BlockPos;

public final class PillarColumn {

  private PillarColumn() {}

  public static List<BlockPos> above(final BlockPos struck, final int heightBlocks) {
    if (struck == null || heightBlocks <= 0) {
      return List.of();
    }
    final List<BlockPos> column = new ArrayList<>(heightBlocks);
    for (int offset = 1; offset <= heightBlocks; offset++) {
      column.add(struck.up(offset));
    }
    return List.copyOf(column);
  }
}
