package com.grahambartley.notenougharrows.zipline;

import java.util.Objects;
import java.util.UUID;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record PendingAnchor(
    Identifier worldId, BlockPos pos, Identifier blockId, UUID arrowId, long expiryTick) {

  public PendingAnchor {
    Objects.requireNonNull(worldId, "A pending anchor belongs to one world");
    Objects.requireNonNull(pos, "A pending anchor needs its block");
    Objects.requireNonNull(blockId, "A pending anchor needs to know what it holds onto");
    Objects.requireNonNull(arrowId, "A pending anchor is set by one arrow");
    pos = pos.toImmutable();
  }

  public boolean hasExpired(final long tick) {
    return tick >= expiryTick;
  }

  public boolean isIn(final Identifier world) {
    return worldId.equals(world);
  }

  public boolean holdsOnto(final Identifier currentBlockId) {
    return blockId.equals(currentBlockId);
  }
}
