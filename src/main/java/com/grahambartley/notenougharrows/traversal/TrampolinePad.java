package com.grahambartley.notenougharrows.traversal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.util.math.BlockPos;

public final class TrampolinePad {
  public static final int HALF_WIDTH = 1;

  private TrampolinePad() {}

  public static List<BlockPos> around(final BlockPos center) {
    if (center == null) {
      return List.of();
    }
    final List<BlockPos> pad = new ArrayList<>();
    for (int dx = -HALF_WIDTH; dx <= HALF_WIDTH; dx++) {
      for (int dz = -HALF_WIDTH; dz <= HALF_WIDTH; dz++) {
        pad.add(center.add(dx, 0, dz));
      }
    }
    pad.sort(Comparator.comparingInt(pos -> pos.getManhattanDistance(center)));
    return List.copyOf(pad);
  }
}
