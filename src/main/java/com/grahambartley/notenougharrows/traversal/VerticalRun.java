package com.grahambartley.notenougharrows.traversal;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.BlockPos;

public final class VerticalRun {

  private VerticalRun() {}

  public static List<BlockPos> upFrom(final BlockPos start, final int lengthBlocks) {
    if (start == null || lengthBlocks <= 0) {
      return List.of();
    }
    final List<BlockPos> run = new ArrayList<>(lengthBlocks);
    for (int offset = 0; offset < lengthBlocks; offset++) {
      run.add(start.up(offset));
    }
    return List.copyOf(run);
  }
}
