package com.grahambartley.morearrows.rope;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.BlockPos;

public final class RopeShape {

  private RopeShape() {}

  public static List<BlockPos> below(final BlockPos anchorPos, final int maxLength) {
    if (anchorPos == null || maxLength <= 0) {
      return List.of();
    }

    final List<BlockPos> column = new ArrayList<>(maxLength);
    for (int depth = 1; depth <= maxLength; depth++) {
      column.add(anchorPos.down(depth));
    }
    return List.copyOf(column);
  }
}
