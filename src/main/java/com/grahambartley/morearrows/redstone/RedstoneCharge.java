package com.grahambartley.morearrows.redstone;

import net.minecraft.util.math.BlockPos;

public record RedstoneCharge(BlockPos pos, long expiryTick) {

  public RedstoneCharge {
    if (pos == null) {
      throw new IllegalArgumentException("Redstone charge requires a position");
    }
    pos = pos.toImmutable();
  }

  public boolean hasExpired(final long tick) {
    return tick >= expiryTick;
  }
}
