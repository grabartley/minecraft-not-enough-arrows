package com.grahambartley.morearrows.fire;

import java.util.List;
import net.minecraft.util.math.BlockPos;

public record FirePatch(List<BlockPos> positions, long expiryTick) {

  public FirePatch {
    positions = positions == null ? List.of() : List.copyOf(positions);
  }

  public boolean isEmpty() {
    return positions.isEmpty();
  }

  public boolean hasExpired(final long tick) {
    return tick >= expiryTick;
  }
}
